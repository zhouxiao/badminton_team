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

        SparseArray<String> sa = PlayerSlot.getSavedPlayer();
        int key = R.id.textPalyer1;
        for (int i = 0; i < sa.size(); i++){
            key = sa.keyAt(i);
            String name = sa.get(key);
            TextView textPlayerView = (TextView) v.findViewById(key);
            textPlayerView.setText(name);
        }

        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("score", Activity.MODE_PRIVATE);
        Map<String, ?> results = mySharedPreferences.getAll();

        String record = "";
        for (Map.Entry<String, ?> entry:results.entrySet()){
            String keyResult = entry.getKey();
            String valueResult = entry.getValue().toString();
            record += keyResult.substring(11) + " " + valueResult + "\n";
        }

        TextView textResultView = (TextView)v.findViewById(R.id.textResult);
        textResultView.setText(record);

        return v;
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
