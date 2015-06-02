package xiao.teammanagement;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends ActionBarActivity  implements OnDialogDoneListener{

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private boolean isDeletable = false;
    private static int RECORD_FIELD_COUNT = 6;

    public static final String LOGTAG = "Main Activity";
    public static String ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG";
    public static String PROMPT_DIALOG_TAG_LOAD = "PROMPT_DIALOG_TAG_LOAD";
    public static String PROMPT_DIALOG_TAG_SAVE = "PROMPT_DIALOG_TAG_SAVE";
    public static String PROMPT_DIALOG_TAG_LOAD_CLOUD ="PROMPT_DIALOG_TAG_LOAD_CLOUD";

    private static String TAG = MainActivity.class.getSimpleName();

    private boolean syncupcall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);

        Log.v("Main Activity****", "OnCreate called");

    }

    @Override
    protected void onResume() {
        Log.v("Main Activity****", "Resume called");
        super.onResume();
    }

    @Override
    protected void onStart() {

        setContentView(R.layout.activity_main);

        manager = getFragmentManager();
        transaction = manager.beginTransaction();
        ActivityLeftFragment fragmentLeft = new ActivityLeftFragment();
        ActivityRightFragment fragmentRight = new ActivityRightFragment();
        transaction.add(R.id.left, fragmentLeft, "list");
        transaction.add(R.id.right, fragmentRight, "detail");
        transaction.commit();

        int netConnectResult = NetworkUtil.getConnectivityStatus(getApplicationContext());
        if (netConnectResult != NetworkUtil.TYPE_NOT_CONNECTED){
            checkAvailableSyncup();
        }

        Log.v("Main Activity****", "Start called");

        super.onStart();
    }

    private void checkAvailableSyncup() {

        // Save last_updated timestamp from server to shared preference
        final String key = "last_updated";
        final SharedPreferences  mySharedPreferences = getSharedPreferences(DataSet.PREFERENCE_SYNC_UP_TIMESTAMP, Activity.MODE_PRIVATE);
        final String last_updated = mySharedPreferences.getString(key, "");
        StringRequest stringRequest = new StringRequest(DataSet.ACTION_GET_LATEST_TIMESTAMP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (last_updated.compareTo(response) == 0){
                            Log.d(TAG, "no change");
                            if (syncupcall){
                                Toast.makeText(getApplicationContext(), "已是最新数据", Toast.LENGTH_LONG).show();
                                syncupcall = false;
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "服务器数据有更新，开始同步数据", Toast.LENGTH_LONG).show();

                            // get latest data
                            fetchLatestData();

                            // update lastest update timestamp
                            SharedPreferences.Editor editor = mySharedPreferences.edit();
                            editor.putString(key, response);
                            editor.apply();
                        }
                        Log.d(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String toastMsg = "";

        switch (id) {
            case R.id.menu_settings:
                handleSetting();
                break;
            case R.id.menu_load:
                handleLoad();
                break;
             case R.id.menu_clear:
                 handleClear();
                break;
            case R.id.menu_save:
                handleSave();
                break;
            case R.id.menu_save_to_cloud:
                uploadResultToCloud();
                break;
            case R.id.menu_team:
                startTeamActivity();
                break;
            case R.id.menu_sync:
                syncupcall = true;
                if (syncUpWithServer() != NetworkUtil.TYPE_NOT_CONNECTED) checkAvailableSyncup();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Uploaded match result to Cloud db from local shared preference
    private void uploadResultToCloud() {

        int netConnectResult = NetworkUtil.getConnectivityStatus(getApplicationContext());
        if (netConnectResult == NetworkUtil.TYPE_NOT_CONNECTED){
            Toast.makeText(getApplicationContext(), "没有网络连接，无法上传", Toast.LENGTH_LONG).show();
        } else {

            SharedPreferences mySharedPreferences = getSharedPreferences(DataSet.PREFERENCE_SCORE_DB, Activity.MODE_PRIVATE);
            Map<String, ?> results = mySharedPreferences.getAll();
            final Map<String, String> hMap = new HashMap<String, String>();
             int total = 0;
             for (Map.Entry<String, ?> entry:results.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue().toString();
                String[] fields = value.split(DataSet.FIELD_SEPERATOR_COMMA);
                int player1_position = Integer.parseInt(fields[0]);
                int player2_position = Integer.parseInt(fields[1]);
                int player3_position = Integer.parseInt(fields[2]);
                int player4_position = Integer.parseInt(fields[3]);
                String score = fields[4];
                int player1 = DataSet.rowItems.get(player1_position).getId();
                int player2 = DataSet.rowItems.get(player2_position).getId();
                int player3 = DataSet.rowItems.get(player3_position).getId();
                int player4 = DataSet.rowItems.get(player4_position).getId();
                String result = key + DataSet.FIELD_SEPERATOR_COMMA + player1 + DataSet.FIELD_SEPERATOR_COMMA
                        + player2 + DataSet.FIELD_SEPERATOR_COMMA + player3 + DataSet.FIELD_SEPERATOR_COMMA + player4 + DataSet.FIELD_SEPERATOR_COMMA + score;
                total++;
                hMap.put("record" + String.valueOf(total), result);
                Log.d("Record " + total, result);
            }
            if (total > 0){
                hMap.put("total", String.valueOf(total));
                doUpLoad(hMap);
            } else {
                Toast.makeText(getApplicationContext(), "没有需要上传的数据", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Perform upload via volley
    private void doUpLoad(Map<String, String> hMap) {
        Request<JSONObject> jsonRequest = new CustomRequest(Request.Method.POST, DataSet.ACTION_UPLOAD_MATCH_RESULT, hMap,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response -> " + response.toString());
                        try {
                            boolean success = response.getBoolean("success");
                            if (success){
                                Toast.makeText(getApplicationContext(),"成功上传比赛数据",Toast.LENGTH_LONG).show();

                                //clear shared preference
                                SharedPreferences mySharedPreferences = getSharedPreferences(DataSet.PREFERENCE_SCORE_DB, Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                editor.clear();
                                editor.apply();

                         } else{
                                Toast.makeText(getApplicationContext(),"上传失败",Toast.LENGTH_LONG).show();
                            }

                        } catch(JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonRequest);

    }

    private int syncUpWithServer() {

        int netConnectResult = NetworkUtil.getConnectivityStatus(getApplicationContext());

        switch (netConnectResult){
            case 0:
                Toast.makeText(getApplicationContext(), "没有网络连接，无法同步", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(getApplicationContext(), "Wifi连接可用，检查是否有更新", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "移动数据可用，检查是否有更新", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        return netConnectResult;
    }

    private void fetchLatestData() {

        JsonArrayRequest req = new JsonArrayRequest(DataSet.ACTION_FETCHALL_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Parsing json array response
                            // loop through each json object
                            String jsonResponse = "";

                            // clear old info data
                            SharedPreferences mySharedPreferences = getSharedPreferences(DataSet.PREFERENCE_TEAM_DB, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mySharedPreferences.edit();
                            editor.clear();

                            // Save to sharedPreference "team.xml"
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response.get(i);

                                String id = person.getString("id");
                                String name = person.getString("name");
                                String alias = person.getString("alias");
                                String age = person.getString("age");
                                String sex = person.getString("sex");
                                String created_date = person.getString("created");
                                String modified_date = person.getString("modified");
                                String photo = person.getString("photo");

                                jsonResponse +=  name + DataSet.FIELD_SEPERATOR_DASH;
                                jsonResponse +=  alias + DataSet.FIELD_SEPERATOR_DASH;
                                jsonResponse +=  age + DataSet.FIELD_SEPERATOR_DASH;
                                jsonResponse +=  sex + DataSet.FIELD_SEPERATOR_DASH;
                                jsonResponse +=  created_date + DataSet.FIELD_SEPERATOR_DASH;
                                jsonResponse +=  modified_date + DataSet.FIELD_SEPERATOR_DASH;
                                jsonResponse +=  photo;

                                editor.putString(id, jsonResponse);
                                // Log.d(TAG, jsonResponse);
                                jsonResponse = "";
                            }

                            editor.apply();
                            // clear old photo data
                            SharedPreferences sp = getSharedPreferences(DataSet.PREFERENCE_TEAM_PHOTO_DB, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor sp_editor = sp.edit();
                            sp_editor.clear();
                            sp_editor.apply();

                            Toast.makeText(getApplicationContext(), "数据同步成功", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "获取服务器数据失败: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }


    private void startTeamActivity() {
        Intent intent = new Intent(this, TeamActivity.class);
        startActivity(intent);
    }

    private void handleSave() {
        String sb = getMatchResult();
        if (sb.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(), "没有新添加的记录", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } else {
            FragmentTransaction pft = getFragmentManager().beginTransaction();
            PromptDialogFragment pdf = PromptDialogFragment.newInstance("请输入要存入的数据文件名");
            pdf.show(pft, PROMPT_DIALOG_TAG_SAVE);
        }

    }

    private void handleSetting() {
        Toast toast = Toast.makeText(getApplicationContext(), "功能还未完成", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    private void handleClear() {
        isDeletable = false;
        FragmentTransaction aft = getFragmentManager().beginTransaction();
        AlertDialogFragment adf = AlertDialogFragment.newInstance("确认要删除历史数据吗？");
        adf.show(aft, ALERT_DIALOG_TAG);
    }

    private void handleLoad() {
        String promptMsg = "";

        FragmentTransaction pft = getFragmentManager().beginTransaction();
        if (DataSet.USING_CLOUD_DATA){
            promptMsg = "请选择要载入数据的时间";
            PromptDialogLoadDataFragment pdf = PromptDialogLoadDataFragment.newInstance(promptMsg);
            pdf.show(pft, PROMPT_DIALOG_TAG_LOAD_CLOUD);
        } else {
            promptMsg = "请输入历史数据文件名";
            PromptDialogFragment pdf = PromptDialogFragment.newInstance(promptMsg);
            pdf.show(pft, PROMPT_DIALOG_TAG_LOAD);
        }

    }

    private void saveData(String fname) {
        String toastMsg = "Failed";
        if (isExternalStorageWritable()){
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fname);
            try {
                if (!file.exists()) file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                out.write(getMatchResult().getBytes("utf-8"));
                out.close();
                toastMsg = "比赛成绩已存档到文件" + fname;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private String loadHistoryData(String datafile) {

        String toastMsg = "";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), datafile);

        if (!file.exists()) {
            toastMsg = "文件不存在，请确定输入正确的文件名";
        } else {

            BufferedReader reader = null;

            try{
                reader = new BufferedReader(new FileReader(file));
                String record = null;
                int line = 0;
                int lineOK = 0;
                String match_data = "";
                while((record = reader.readLine()) != null){
                    line++;
                    String[] fields = record.split(DataSet.FIELD_SEPERATOR_COMMA);
                    if (fields.length != RECORD_FIELD_COUNT){
                        Log.v("LoadHistoryData", "Invalid record, ignored");
                    } else {
                        record = fields[0] + " " + fields[1] + "+" + fields[2] + " vs "
                                + fields[3] + "+" + fields[4] + " " + fields[5];
                        match_data += record + "\n";
                        lineOK++;
                    }
                }
                reader.close();
                showHistoryData(match_data);

                toastMsg = "已载入历史比赛数据" + datafile + ", 共" + line + "条，" + "成功处理" + lineOK + "条";
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }


        }

        return toastMsg;
    }

    private void showHistoryData(String matchResult) {

        TextView tv = (TextView)findViewById(R.id.textResult);
        tv.setText(matchResult);

        //set content to be showed in textResult in right frame
        PlayerSlot.setShowHistoryData(true);
        PlayerSlot.setHistoryData(matchResult);
    }

    private void clearHistoryData() {

        // 清空sharedPreference
        SharedPreferences sharedPref = getSharedPreferences(DataSet.PREFERENCE_SCORE_DB, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        // Clear TextView Result
        TextView tv = (TextView)findViewById(R.id.textResult);
        tv.setText("");
     }

    private String getMatchResult() {

        String record = "";
        SharedPreferences mySharedPreferences = getSharedPreferences(DataSet.PREFERENCE_SCORE_DB, Activity.MODE_PRIVATE);
        Map<String, ?> results = mySharedPreferences.getAll();

        for (Map.Entry<String, ?> entry:results.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            String[] fields = value.split(DataSet.FIELD_SEPERATOR_COMMA);
            int player1_position = Integer.parseInt(fields[0]);
            int player2_position = Integer.parseInt(fields[1]);
            int player3_position = Integer.parseInt(fields[2]);
            int player4_position = Integer.parseInt(fields[3]);
            String score = fields[4];
            String player1 = DataSet.rowItems.get(player1_position).getAlias() + "-" + DataSet.rowItems.get(player1_position).getName();
            String player2 = DataSet.rowItems.get(player2_position).getAlias() + "-" + DataSet.rowItems.get(player2_position).getName();
            String player3 = DataSet.rowItems.get(player3_position).getAlias() + "-" + DataSet.rowItems.get(player3_position).getName();
            String player4 = DataSet.rowItems.get(player4_position).getAlias() + "-" + DataSet.rowItems.get(player4_position).getName();
            String matchDate = key.substring(0, 10);
            record += matchDate + DataSet.FIELD_SEPERATOR_COMMA + player1 + DataSet.FIELD_SEPERATOR_COMMA + player2
                    + DataSet.FIELD_SEPERATOR_COMMA + player3 + DataSet.FIELD_SEPERATOR_COMMA + player4 + DataSet.FIELD_SEPERATOR_COMMA + score;
            record += "\n";
        }
        return record;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) return true;

        return false;
    }

    public void handleButton(View target){
        switch(target.getId()){
            case R.id.button:
                handleAdd();
                break;
            case R.id.imageBtn_save:
                handleSave();
                break;
            case R.id.imageBtn_setting:
                handleSetting();
                break;
            case R.id.imageBtn_clear:
                handleClear();
                break;
            case R.id.imageBtn_load:
                handleLoad();
                break;
            default:
                break;

        }
    }

    public void handleAdd(){
        String toastMsg = "";
        String match = "";
        String match_code = "";

        // clear showHistoryData to make textResult will only show new added data
        if (PlayerSlot.isShowHistoryData()){
            PlayerSlot.setShowHistoryData(false);
            PlayerSlot.setHistoryData("");
        }

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeKey = df.format(date);

        boolean isValid = true;

        int slot = PlayerSlot.getNextSlot();
        if (slot < 4){
           toastMsg += "缺少队员！";
           isValid = false;
         } else {
            SparseArray<Integer> players = PlayerSlot.getSavedPlayer();

            int player1_position = players.get(R.id.textPalyer1);
            int player2_position = players.get(R.id.textPalyer2);
            int player3_position = players.get(R.id.textPalyer3);
            int player4_position = players.get(R.id.textPalyer4);

            String player1 = DataSet.rowItems.get(player1_position).getName();
            String player2 = DataSet.rowItems.get(player2_position).getName();
            String player3 = DataSet.rowItems.get(player3_position).getName();
            String player4 = DataSet.rowItems.get(player4_position).getName();

            if ((player1.compareTo(player2) == 0) || (player1.compareTo(player3) == 0) || (player1.compareTo(player4) == 0)
               || (player2.compareTo(player3) == 0) || (player2.compareTo(player4) == 0)
               ||  (player3.compareTo(player4) == 0)){
                toastMsg += "队员选择重复！";
                isValid = false;
            } else
                match = player1 + "+" + player2 + " vs " + player3 + "+" + player4;
                match_code = player1_position + DataSet.FIELD_SEPERATOR_COMMA + player2_position + DataSet.FIELD_SEPERATOR_COMMA
                        + player3_position + DataSet.FIELD_SEPERATOR_COMMA + player4_position;
            }

            TextView tv = (TextView)findViewById(R.id.editText);
            String score = String.valueOf(tv.getText());
            if (score.isEmpty()){
               toastMsg += "比赛结果不能为空！";
               isValid = false;
             } else {
                int intScore = Integer.parseInt(score);
                if (intScore < -21 || intScore > 21 || intScore == 0) {
                    toastMsg += "比赛结果无效（需在-21到21之间,不能为0）！";
                    isValid = false;
                 }
             }

             if (isValid){
                   // Save to sharedPreference "score.xml"
               SharedPreferences mySharedPreferences = getSharedPreferences(DataSet.PREFERENCE_SCORE_DB, Activity.MODE_PRIVATE);
               SharedPreferences.Editor editor = mySharedPreferences.edit();
               editor.putString(timeKey, match_code + DataSet.FIELD_SEPERATOR_COMMA + score);
               editor.apply();

               // Add new record to textResult view
               TextView tvResult = (TextView)findViewById(R.id.textResult);
               String strRecord = timeKey.substring(11) + " " + match + " " + score;
               tvResult.setText(tvResult.getText() + strRecord + "\n");

               prepareNewMatch();
               toastMsg = "新的比赛记录已经添加!";

             } else {
                toastMsg += "添加失败！";
             }

             Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
             toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
             toast.show();
    }

    private void prepareNewMatch() {
        PlayerSlot.clearAllSlot();
        PlayerSlot.clearPlayer();

        TextView tv = (TextView)findViewById(R.id.textPalyer1);
        tv.setText("");

        tv = (TextView)findViewById(R.id.textPalyer2);
        tv.setText("");

        tv = (TextView)findViewById(R.id.textPalyer3);
        tv.setText("");

        tv = (TextView)findViewById(R.id.textPalyer4);
        tv.setText("");

        tv = (TextView)findViewById(R.id.editText);
        tv.setText("");
    }

    public void removePlayer(View target) {
        String toastMsg = "Pls. add new player ";
        TextView textPlayerView = null;
        int slot = 0;
        int key = target.getId();
        switch(key){
            case R.id.textPalyer1:
                slot = 0;
                textPlayerView = (TextView)this.findViewById(R.id.textPalyer1);
                break;
            case R.id.textPalyer2:
                slot = 1;
                textPlayerView = (TextView)this.findViewById(R.id.textPalyer2);
                break;
            case R.id.textPalyer3:
                slot = 2;
                textPlayerView = (TextView)this.findViewById(R.id.textPalyer3);
                break;
            case R.id.textPalyer4:
                slot = 3;
                textPlayerView = (TextView)this.findViewById(R.id.textPalyer4);
                break;
            default:
                break;
        }

        PlayerSlot.clearSlot(slot);
        PlayerSlot.removePlayer(key);
        assert textPlayerView != null;
        textPlayerView.setText("");

        slot++;
        toastMsg += slot;
        Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    @Override
    protected void onStop() {
        AppController.getInstance().cancelPendingRequests(AppController.TAG);
        super.onStop();
    }

    @Override
    public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
        String s = tag + " responds with: " + message;
        String toastMsg = "";
        boolean showMsg = true;
        switch (tag) {
            case "ALERT_DIALOG_TAG":
                if(cancelled){
                    isDeletable = false;
                    toastMsg = "已撤销删除";
                } else {
                    clearHistoryData();
                    toastMsg = "已成功清除历史数据";
                    isDeletable = !isDeletable;
                }
                break;
            case "PROMPT_DIALOG_TAG_LOAD":
                if (cancelled){
                    toastMsg = "已取消操作";
                } else {
                    if (message.toString().isEmpty()){
                        toastMsg = "文件名不能为空";
                    } else{
                        toastMsg = loadHistoryData(message.toString());
                    }
                }
                break;
            case "PROMPT_DIALOG_TAG_SAVE":
                if (cancelled){
                    toastMsg = "已取消操作";
                } else {
                    if (message.toString().isEmpty()){
                        toastMsg = "文件名不能为空";
                    } else{
                       saveData(message.toString());
                    }
                }
                break;
            case "PROMPT_DIALOG_TAG_LOAD_CLOUD":
                if (cancelled){
                    toastMsg = "已取消操作";
                } else{
                    int id = Integer.parseInt(message.toString());
                    String condition = "";
                    showMsg = false;
                    switch (id){
                        case R.id.dayRBtn:
                            condition = "1";
                            break;
                        case R.id.weekRBtn:
                            condition = "2";
                            break;
                        case R.id.threeMmonthRBtn:
                            condition = "3";
                            break;
                        case R.id.yearRBtn:
                            condition = "4";
                            break;
                        default:
                            break;
                    }

                  downloadData(DataSet.ACTION_DOWNLOAD_DATA_URL, condition);
                }
                break;
            default:
                break;
        }

        if (showMsg){
            Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }

     }

    // dowload history match result from cloud
    private void downloadData(String url, String condition) {

        Map<String, String> hMap = new HashMap<String, String>();
        hMap.put("condition", condition);

        Request<JSONObject> jsonRequest = new CustomRequest(Request.Method.POST, url, hMap,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response -> " + response.toString());
                        TextView tv = (TextView)findViewById(R.id.textResult);
                        tv.setText("");
                        String downloadedResult = "";
                        try {
                            int total = response.getInt("total");

                            if (total > 0){
                                for (int i = 1; i <= total; i++){
                                    String record = response.getString("record" + String.valueOf(i));
                                    String[] details = record.split(DataSet.FIELD_SEPERATOR_COMMA);
                                    String player1 = getDetail(details[0]);
                                    String player2 = getDetail(details[1]);
                                    String player3 = getDetail(details[2]);
                                    String player4 = getDetail(details[3]);
                                    String score = details[4];
                                    String matchtime = details[5];
                                    String output = matchtime.substring(0,10) + DataSet.FIELD_SEPERATOR_COMMA
                                            + player1 + DataSet.FIELD_SEPERATOR_COMMA + player2 + DataSet.FIELD_SEPERATOR_COMMA + player3 + DataSet.FIELD_SEPERATOR_COMMA + player4
                                            + DataSet.FIELD_SEPERATOR_COMMA + score;
                                    output += "\n";
                                    tv.append(output);
                                    downloadedResult += output;
                                }
                                tv.append("\n共载入" + total + "条比赛记录.");

                                //set content to be showed in textResult in right frame
                                PlayerSlot.setShowHistoryData(true);
                                PlayerSlot.setHistoryData(downloadedResult);

                                //Save to file history.txt
                                if (isExternalStorageWritable()){
                                    String fname = "history.txt";
                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fname);
                                    try {
                                        if (!file.exists()) file.createNewFile();
                                        FileOutputStream out = new FileOutputStream(file);
                                        out.write(downloadedResult.getBytes("utf-8"));
                                        out.close();
                                        tv.append("已存入文件" + fname);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else{
                                Toast.makeText(getApplicationContext() ,"没有符合条件的比赛记录",Toast.LENGTH_LONG).show();
                            }

                        } catch(JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonRequest);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        super.onSaveInstanceState(outState);
    }

    // get player's alias and name from shared preference
    // return the format as 'alias-name'
    private String getDetail(String id) {
        SharedPreferences sp = getApplicationContext().getSharedPreferences(DataSet.PREFERENCE_TEAM_DB, getApplicationContext().MODE_PRIVATE);
        String value = sp.getString(id, null);
        if (value != null){
            String[] fields = value.split(DataSet.FIELD_SEPERATOR_DASH);
            value = fields[1] + '-' + fields[0];
        } else {
            value = "";
        }
        return value;
    }
}
