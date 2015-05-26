package xiao.teammanagement;

import android.app.Activity;
import android.app.Fragment;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ActivityLeftFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView listView;
    List<RowItem> rowItems;

    public ActivityLeftFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Xiao**********", "Left onCreateView called");

        View v =  inflater.inflate(R.layout.fragment_left, container, false);
        fillRows();
        listView = (ListView)v.findViewById(R.id.list);
        CustomListViewAdapter adapter = new CustomListViewAdapter(this.getActivity(), R.layout.list_item, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String name = rowItems.get(position).getName();

        Fragment frmRight = getFragmentManager().findFragmentById(R.id.right);
        View frmRightView = frmRight.getView();
        int playerId = R.id.textPalyer1;
        int slot = PlayerSlot.getNextSlot();

        String toastMsg = "";
        switch (slot) {
            case 0: playerId = R.id.textPalyer1;
                    break;
            case 1: playerId = R.id.textPalyer2;
                    break;
            case 2: playerId = R.id.textPalyer3;
                    break;
            case 3: playerId = R.id.textPalyer4;
                    break;
            default:
                    break;
        }

        if (slot < 4){
            assert frmRightView != null;
            TextView txtPlayer = (TextView) frmRightView.findViewById(playerId);
            txtPlayer.setText(name);
            PlayerSlot.setSlot(slot);
            PlayerSlot.savePlayer(playerId, name);
            toastMsg = "Player " + (slot + 1) + ": " + rowItems.get(position).getName() + " is added";
        } else {
            toastMsg = "No more player needed";
        }

        Toast toast = Toast.makeText(this.getActivity().getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

    }


    private void fillRows() {

        rowItems = new ArrayList<RowItem>();

        Uri uri = TeamProviderMetaData.MemberTableMetaData.CONTENT_URI;
        Activity a = this.getActivity();

        CursorLoader cursorLoader = new CursorLoader(
                a,
                uri,
                null,
                null,
                null,
                null);

        Cursor c = cursorLoader.loadInBackground();

       /* Cursor c = a.managedQuery(uri,
                null, //projection
                null, //selection string
                null, //selection args array of strings
                null); //sort order
      */

        int iname = c.getColumnIndex(TeamProviderMetaData.MemberTableMetaData.MEMBER_NAME);
        int ialias = c.getColumnIndex(TeamProviderMetaData.MemberTableMetaData.MEMBER_ALIAS);
        int iage = c.getColumnIndex(TeamProviderMetaData.MemberTableMetaData.MEMBER_AGE);
        int isex = c.getColumnIndex(TeamProviderMetaData.MemberTableMetaData.MEMBER_SEX);
        int iphoto = c.getColumnIndex(TeamProviderMetaData.MemberTableMetaData.MEMBER_PHOTO);

            //walk through the rows based on indexes
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
            //Gather values
            int id = c.getInt(1);
            String name = c.getString(iname);
            String alias = c.getString(ialias);
            int age = c.getInt(iage);
            int sex = c.getInt(isex);

            byte[] in = c.getBlob(iphoto);
            Bitmap bmpout= BitmapFactory.decodeByteArray(in, 0, in.length);

            rowItems.add(new RowItem(id, name, alias,sex, age, bmpout));
        }

        //Report how many rows have been read
        int numberOfRecords = c.getCount();

        //Close the cursor
        //ideally this should be done in
        //a finally block.
        c.close();

        DataSet.rowItems = rowItems;
    }
}
