package xiao.teammanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Xiao on 2015/5/14.
 */
public class AlertDialogFragment extends DialogFragment
implements DialogInterface.OnClickListener{

    public static AlertDialogFragment newInstance(String message){
        AlertDialogFragment adf = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alert-message", message);
        adf.setArguments(bundle);

        return adf;
    }

    @Override
    public void onAttach(Activity activity) {
        try{
            OnDialogDoneListener test = (OnDialogDoneListener)activity;
        }catch(ClassCastException cce){
            Log.e(MainActivity.LOGTAG, "Activity is not listening");
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle("警告")
                .setPositiveButton("确认", this)
                .setNegativeButton("撤销", this)
                .setMessage(this.getArguments().getString("alert-message"));

        return b.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        OnDialogDoneListener act = (OnDialogDoneListener)getActivity();
        boolean cancelled = false;
        if (which == AlertDialog.BUTTON_NEGATIVE) {
            cancelled = true;
        }
        act.onDialogDone(getTag(), cancelled, "Alert dismissed");

    }
}
