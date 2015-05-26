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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends ActionBarActivity  implements OnDialogDoneListener{

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private boolean isDeletable = false;

    public static final String LOGTAG = "Main Activity";
    public static String ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG";
    public static String PROMPT_DIALOG_TAG_LOAD = "PROMPT_DIALOG_TAG_LOAD";
    public static String PROMPT_DIALOG_TAG_SAVE = "PROMPT_DIALOG_TAG_SAVE";
    private static int RECORD_FIELD_COUNT = 6;

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

        Log.v("Main Activity****", "Start called");

        super.onStart();
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
            case R.id.menu_team:
                startTeamActivity();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        FragmentTransaction pft = getFragmentManager().beginTransaction();
        PromptDialogFragment pdf = PromptDialogFragment.newInstance("请输入历史数据文件名");
        pdf.show(pft, PROMPT_DIALOG_TAG_LOAD);
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
                    String[] fields = record.split(",");
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
        for (int i = 0; i < DataSet.names.length; i++)
            matchResult = matchResult.replaceAll(DataSet.descriptions[i], DataSet.names[i]);

        TextView tv = (TextView)findViewById(R.id.textResult);
        tv.setText(matchResult);
    }

    private void clearHistoryData() {

        // 清空sharedPreference
        SharedPreferences sharedPref = getSharedPreferences("score", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        // Clear TextView Result
        TextView tv = (TextView)findViewById(R.id.textResult);
        tv.setText("");
     }

    private String getMatchResult() {
        SharedPreferences mySharedPreferences = getSharedPreferences("score", Activity.MODE_PRIVATE);
        Map<String, ?> results = mySharedPreferences.getAll();

        String record = "";
        for (Map.Entry<String, ?> entry:results.entrySet()){
            String keyResult = entry.getKey();
            String valueResult = entry.getValue().toString();
            String matchDate = keyResult.substring(0, 11);
            String matchResult = valueResult.replaceAll(" vs ", ",");
            matchResult = matchResult.replaceAll(" ", ",");
            matchResult = matchResult.replace('+', ',');
            for (int i = 0; i < DataSet.names.length; i++)
                matchResult = matchResult.replaceAll(DataSet.names[i], DataSet.descriptions[i]);
            record += keyResult.substring(0,11) + "," + matchResult + "\n";
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

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timeKey = df.format(date);

        boolean isValid = true;

        int slot = PlayerSlot.getNextSlot();
        if (slot < 4){
           toastMsg += "缺少队员！";
           isValid = false;
         } else {
            SparseArray<String> players = PlayerSlot.getSavedPlayer();
            String player1 = players.get(R.id.textPalyer1);
            String player2 = players.get(R.id.textPalyer2);
            String player3 = players.get(R.id.textPalyer3);
            String player4 = players.get(R.id.textPalyer4);

            if ((player1 == player2) || (player1 == player3) || (player1 == player4)
               || (player2 == player3) || (player2 == player4)
               ||  (player3 == player4)){
                toastMsg += "队员选择重复！";
                isValid = false;
            } else
                match = player1 + "+" + player2 + " vs " + player3 + "+" + player4;
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
               SharedPreferences mySharedPreferences = getSharedPreferences("score", Activity.MODE_PRIVATE);
               SharedPreferences.Editor editor = mySharedPreferences.edit();
               editor.putString(timeKey, match + " " + score);
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
    public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
        String s = tag + " responds with: " + message;
        String toastMsg = "";
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
            default:
                break;
        }

        Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
     }
}
