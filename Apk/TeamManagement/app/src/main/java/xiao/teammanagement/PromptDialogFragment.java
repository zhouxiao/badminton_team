package xiao.teammanagement;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Xiao on 2015/5/14.
 */
public class PromptDialogFragment extends DialogFragment
implements View.OnClickListener{

    private EditText et;
    private Bundle outState;

    public static PromptDialogFragment newInstance(String prompt){
        PromptDialogFragment pdf = new PromptDialogFragment();
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
        View v = inflater.inflate(R.layout.prompt_dialog, container, false);

        TextView tv = (TextView)v.findViewById(R.id.promptmessage);
        tv.setText(getArguments().getString("prompt"));

        Button dismissBtn = (Button)v.findViewById(R.id.btn_dismiss);
        dismissBtn.setOnClickListener(this);

        Button saveBtn = (Button)v.findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(this);

        et = (EditText)v.findViewById(R.id.inputtext);

        if(savedInstanceState != null){
            et.setText(savedInstanceState.getCharSequence("input"));
        } else {
            if (this.getTag().equals("PROMPT_DIALOG_TAG_SAVE")){
                Date date = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String defaultFile = df.format(date) + "_score" + ".txt";
                et.setText(defaultFile);
            }
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("input", et.getText());
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
            TextView tv = (TextView)getView().findViewById(R.id.inputtext);
            act.onDialogDone(this.getTag(), false, tv.getText());
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
