package xiao.teammanagement;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;

import xiao.teammanagement.TeamProviderMetaData.MemberTableMetaData;


public class TeamActivity extends ActionBarActivity implements OnDialogDoneListener, OnItemClickListener {

    private boolean isUpdatable = false;
    private int curIdx = -1;
    ListView listView;
    List<RowItem> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        listView = (ListView)findViewById(R.id.listMember);


        CustomTeamListViewAdapter adapter = new CustomTeamListViewAdapter(this, R.layout.team_list_item, DataSet.rowItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_reset){
            batchImport();
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleButton(View target){
        switch(target.getId()){
            case R.id.teamBtn_add:
                addMember();
                break;
            case R.id.teamBtn_load:
                refreshParentActivity();
                break;
            case R.id.teamBtn_update:
                confirmToUpdate();
                break;
            case R.id.teamBtn_delete:
                confirmToDelete();
                break;
            case R.id.testPhoto:
                selectPicture();
                break;
            case R.id.btnSubmit:
                confirmToSave();
                break;
            case R.id.btnCancel:
                postSaveMember();
                break;
            default:
                break;
            }

    }

    private void confirmToSave() {
        FragmentTransaction aft = getFragmentManager().beginTransaction();
        AlertDialogFragment adf = AlertDialogFragment.newInstance("确认要添加新成员吗？");
        adf.show(aft, MainActivity.ALERT_DIALOG_TAG + "_SAVE");
    }

    private void confirmToDelete() {
        FragmentTransaction aft = getFragmentManager().beginTransaction();
        AlertDialogFragment adf = AlertDialogFragment.newInstance("确认要删除成员吗？");
        adf.show(aft, MainActivity.ALERT_DIALOG_TAG + "_DELETE");
    }

    private void confirmToUpdate() {
        FragmentTransaction aft = getFragmentManager().beginTransaction();
        AlertDialogFragment adf = AlertDialogFragment.newInstance("确认要更新成员信息吗？");
        adf.show(aft, MainActivity.ALERT_DIALOG_TAG + "_UPDATE");
    }

    private void batchImport() {
        for (int i = 0; i < DataSet.descriptions.length; i++){
            String[] fields = DataSet.descriptions[i].split("-");
            String name = fields[1];
            String alias = fields[0];
            int age = 0;
            int sex = 1;

            Resources res = getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(res, DataSet.images[i]);

            ContentValues cv = new ContentValues();
            cv.put(MemberTableMetaData.MEMBER_NAME, name);
            cv.put(MemberTableMetaData.MEMBER_ALIAS, alias);
            cv.put(MemberTableMetaData.MEMBER_AGE, age);
            cv.put(MemberTableMetaData.MEMBER_SEX, sex);

            bitmap = ToolUtils.zoomImg(bitmap, 80, 80);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            cv.put(MemberTableMetaData.MEMBER_PHOTO, os.toByteArray());
            String toastMsg;
            ContentResolver cr = getContentResolver();
            Uri uri = MemberTableMetaData.CONTENT_URI;
            try{
                Uri insertUri = cr.insert(uri, cv);
                }catch(SQLException e){
                e.printStackTrace();
            }
        }
        Toast toast = Toast.makeText(getApplicationContext(), "Batch Import Completed!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        refreshParentActivity();

    }

    private void removeMember(int i) {

        String name = DataSet.rowItems.get(i).getName();
        int id = DataSet.rowItems.get(i).getId();

        ContentResolver cr = this.getContentResolver();
        Uri uri = MemberTableMetaData.CONTENT_URI;
        Uri delUri = Uri.withAppendedPath(uri, Integer.toString(id));

        try {
            cr.delete(delUri, null, null);

            Toast toast = Toast.makeText(getApplicationContext(), "Member " + name + " is deleted", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            postSaveMember();
            refreshParentActivity();

        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }


    }

    private void refreshParentActivity() {

        //startActivity(getParentActivityIntent());

        finish();
     //   startActivity(getIntent());

    }

    private int getCount()
    {
        Uri uri = MemberTableMetaData.CONTENT_URI;
        Activity a = (Activity)this;
        CursorLoader cursorLoader = new CursorLoader(
                a,
                uri,
                null,
                null,
                null,
                null);

        Cursor c = cursorLoader.loadInBackground();
        int numberOfRecords = c.getCount();
        c.close();
        return numberOfRecords;
    }

    private void showMembers() {

    }

    private void addMember() {
        Button btn = (Button) findViewById(R.id.teamBtn_add);

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout2);
        layout.setVisibility(View.VISIBLE);
        layout = (LinearLayout) findViewById(R.id.linearLayout3);
        layout.setVisibility(View.VISIBLE);
        layout = (LinearLayout) findViewById(R.id.linearLayout4);
        layout.setVisibility(View.VISIBLE);

        Button btnAdd = (Button) findViewById(R.id.btnSubmit);
        btnAdd.setEnabled(true);

        }

    private void selectPicture() {
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                ImageView imageView = (ImageView) findViewById(R.id.testPhoto);
                /* 将Bitmap设定到ImageView */
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
        String s = tag + " responds with: " + message;
        String toastMsg = "";
        switch (tag) {
            case "ALERT_DIALOG_TAG_SAVE":
                if(cancelled){
                    toastMsg = "已撤销添加";
                } else {
                    SaveNewMember();
                    toastMsg = "已成功添加新成员";
                }
                break;
            case "ALERT_DIALOG_TAG_DELETE":
                if(cancelled){
                    toastMsg = "已撤销删除";
                } else {
                    if (curIdx != -1){
                        removeMember(curIdx);

                    }
                }
                break;
            case "ALERT_DIALOG_TAG_UPDATE":
                if(cancelled){
                    toastMsg = "已撤销更新";
                } else {
                    updateMember(curIdx);

                }
                break;
            default:
                break;
        }

    }

    private void updateMember(int i) {
        int id = DataSet.rowItems.get(i).getId();

        ContentValues cv = validateDbInput();

        if (cv != null){
            Long now = System.currentTimeMillis();
            cv.put(MemberTableMetaData.MODIFIED_DATE, now);

            ContentResolver cr = this.getContentResolver();
            Uri uri = MemberTableMetaData.CONTENT_URI;
            Uri updateUri = Uri.withAppendedPath(uri, Integer.toString(id));

            try {
                cr.update(updateUri, cv, null, null);

                Toast toast = Toast.makeText(getApplicationContext(), "已成功更新记录", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                postSaveMember();
                refreshParentActivity();

            }catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }

    }

    private ContentValues validateDbInput(){

        ContentValues cv = null;
        String toastMsg = "";
        boolean isValid = true;
        TextView tv = (TextView)findViewById(R.id.testName);
        String name = tv.getText().toString();
        if (name.isEmpty()){
            toastMsg += "姓名不能为空！";
            isValid = false;
        } else {
            toastMsg += name;
        }

        tv = (TextView)findViewById(R.id.testAlias);
        String alias = tv.getText().toString();
        if (alias.isEmpty()){
            toastMsg += "代码不能为空！";
            isValid = false;
        } else {
            toastMsg += " " + alias;
        }

        tv = (TextView)findViewById(R.id.textAge);
        int age = 0;
        try {
            age =  Integer.parseInt(tv.getText().toString());
            toastMsg +=  " " + age;
        } catch (NumberFormatException e){
            toastMsg += "球龄必须为数字！";
            isValid = false;
        }


        ToggleButton tb = (ToggleButton)findViewById(R.id.sexbtn);
        int sex = ( (tb.isChecked() ? 0 : 1 ) );
        toastMsg += " " + sex;

        ImageView iv = (ImageView)findViewById(R.id.testPhoto);
        Drawable photo = iv.getDrawable();
        Bitmap bitmap = null;
        if (photo == null){
            toastMsg += "图片不能为空！";
            isValid = false;
        } else {
            BitmapDrawable bd = (BitmapDrawable)iv.getDrawable();
            bitmap = bd.getBitmap();
        }

        if (isValid){
            cv = new ContentValues();
            cv.put(MemberTableMetaData.MEMBER_NAME, name);
            cv.put(MemberTableMetaData.MEMBER_ALIAS, alias);
            cv.put(MemberTableMetaData.MEMBER_AGE, age);
            cv.put(MemberTableMetaData.MEMBER_SEX, sex);

            bitmap = ToolUtils.zoomImg(bitmap, 80, 80);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            cv.put(MemberTableMetaData.MEMBER_PHOTO, os.toByteArray());
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }


        return cv;

    }

    private void SaveNewMember() {
        ContentValues cv = validateDbInput();
        if (cv != null ){
            String toastMsg;
            ContentResolver cr = getContentResolver();
            Uri uri = MemberTableMetaData.CONTENT_URI;
            try{
                Uri insertUri = cr.insert(uri, cv);

                toastMsg = "已成功加入新成员";
                Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                postSaveMember();
                refreshParentActivity();
            }catch(SQLException e){
                e.printStackTrace();
                toastMsg = "加入出现异常，失败";
            }
        }

    }

    private void postSaveMember() {
        EditText et = (EditText)findViewById(R.id.testAlias);
        et.setText("");

        et = (EditText)findViewById(R.id.textAge);
        et.setText("");

        et = (EditText)findViewById(R.id.testName);
        et.setText("");

        ImageView iv = (ImageView)findViewById(R.id.testPhoto);
        iv.setImageDrawable(null);

        Button btn = (Button)findViewById(R.id.teamBtn_add);
        btn.setText("新增");

        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout2);
        layout.setVisibility(View.GONE);
        layout = (LinearLayout)findViewById(R.id.linearLayout3);
        layout.setVisibility(View.GONE);
        layout = (LinearLayout)findViewById(R.id.linearLayout4);
        layout.setVisibility(View.GONE);

        Button btnAdd = (Button)findViewById(R.id.btnSubmit);
        btnAdd.setEnabled(false);

        Button btnUpdate = (Button)findViewById(R.id.teamBtn_update);
        btnUpdate.setEnabled(false);

        Button btnDelete = (Button)findViewById(R.id.teamBtn_delete);
        btnDelete.setEnabled(false);

        curIdx = -1;
        isUpdatable = false;


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (! isUpdatable){
            LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout2);
            layout.setVisibility(View.VISIBLE);
            layout = (LinearLayout) findViewById(R.id.linearLayout3);
            layout.setVisibility(View.VISIBLE);
            layout = (LinearLayout) findViewById(R.id.linearLayout4);
            layout.setVisibility(View.VISIBLE);

        }

        String name = DataSet.rowItems.get(position).getName();
        EditText et = (EditText)findViewById(R.id.testName);
        et.setText(name);

        String alias = DataSet.rowItems.get(position).getAlias();
        et = (EditText)findViewById(R.id.testAlias);
        et.setText(alias);


        int age = DataSet.rowItems.get(position).getAge();
        et = (EditText)findViewById(R.id.textAge);
        et.setText(String.valueOf(age));

        int sex = DataSet.rowItems.get(position).getSex();
        ToggleButton toggleBtn = (ToggleButton)findViewById(R.id.sexbtn);
        if (sex == 0){
            toggleBtn.setChecked(true);
        } else {
            toggleBtn.setChecked(false);
        }

        Bitmap bitmap = DataSet.rowItems.get(position).getBitmap();
        ImageView iv = (ImageView)findViewById(R.id.testPhoto);
        iv.setImageDrawable(new BitmapDrawable(bitmap));

        Button btnUpdate = (Button)findViewById(R.id.teamBtn_update);
        btnUpdate.setEnabled(true);

        Button btnDelete = (Button)findViewById(R.id.teamBtn_delete);
        btnDelete.setEnabled(true);

        curIdx = position;

        isUpdatable = true;

     }
}
