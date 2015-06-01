package xiao.teammanagement;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Xiao on 2015/5/31.
 */
public class PromptDialogLoadDataFragment extends DialogFragment
        implements View.OnClickListener{

    private Bundle outState;
    private RadioGroup radGrp;

    public static PromptDialogLoadDataFragment newInstance(String prompt){
        PromptDialogLoadDataFragment pdf = new PromptDialogLoadDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("prompt", prompt);
        pdf.setArguments(bundle);

        return pdf;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.prompt_dialog_cloud, container, false);

        TextView tv = (TextView)v.findViewById(R.id.promptmessage);
        tv.setText(getArguments().getString("prompt"));

        radGrp = (RadioGroup)v.findViewById(R.id.radGrp);

        Button dismissBtn = (Button)v.findViewById(R.id.btn_dismiss);
        dismissBtn.setOnClickListener(this);

        Button saveBtn = (Button)v.findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(this);


        if(savedInstanceState != null){
            radGrp.check(savedInstanceState.getInt("Checked"));
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt("Checked", radGrp.getCheckedRadioButtonId());

        super.onPause();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.v(MainActivity.LOGTAG, "in onCancel() of PDF");
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.v(MainActivity.LOGTAG, "in onDismiss() of PDF");
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View v) {
        OnDialogDoneListener act = (OnDialogDoneListener)getActivity();
        if (v.getId() == R.id.btn_save){
            act.onDialogDone(this.getTag(), false, String.valueOf(radGrp.getCheckedRadioButtonId()));
            dismiss();
            return;
        }
        if (v.getId() == R.id.btn_dismiss){
            act.onDialogDone(this.getTag(), true, null);
            dismiss();
            return;
        }

    }
}
