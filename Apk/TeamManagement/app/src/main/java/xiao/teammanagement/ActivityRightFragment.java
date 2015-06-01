package xiao.teammanagement;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class ActivityRightFragment extends Fragment {

    public ActivityRightFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  Log.v("Right Frame", "onCreateView() is called");
        View v = inflater.inflate(R.layout.fragment_right, container, false);

        SparseArray<Integer> sa = PlayerSlot.getSavedPlayer();
        int key = R.id.textPalyer1;
        for (int i = 0; i < sa.size(); i++){
            key = sa.keyAt(i);
            int position = sa.get(key);
            TextView textPlayerView = (TextView) v.findViewById(key);
            textPlayerView.setText(DataSet.rowItems.get(position).getName());
        }

        String record = "";

        if (PlayerSlot.isShowHistoryData()){
            record = PlayerSlot.getHistoryData();
        }else{
            SharedPreferences mySharedPreferences = getActivity().getSharedPreferences(DataSet.PREFERENCE_SCORE_DB, Activity.MODE_PRIVATE);
            Map<String, ?> results = mySharedPreferences.getAll();

            for (Map.Entry<String, ?> entry:results.entrySet()) {
                String keyStr = entry.getKey();
                String value = entry.getValue().toString();
                String[] fields = value.split(DataSet.FIELD_SEPERATOR_COMMA);
                int player1_position = Integer.parseInt(fields[0]);
                int player2_position = Integer.parseInt(fields[1]);
                int player3_position = Integer.parseInt(fields[2]);
                int player4_position = Integer.parseInt(fields[3]);
                String score = fields[4];
                String player1 = DataSet.rowItems.get(player1_position).getName();
                String player2 = DataSet.rowItems.get(player2_position).getName();
                String player3 = DataSet.rowItems.get(player3_position).getName();
                String player4 = DataSet.rowItems.get(player4_position).getName();
                String matchDate = keyStr.substring(11);
                record += matchDate + " " + player1 + "+" + player2 + " vs " + player3 + "+" + player4 + " " + score;
                record += "\n";
            }
        }

        TextView textResultView = (TextView)v.findViewById(R.id.textResult);
        textResultView.setText(record);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

      //  Log.v("Right Frame", "onDestroyView() is called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
