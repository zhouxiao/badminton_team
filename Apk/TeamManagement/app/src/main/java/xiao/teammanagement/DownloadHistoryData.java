package xiao.teammanagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xiao on 2015/5/31.
 */
public class DownloadHistoryData extends AsyncTask<String, Integer, Integer> implements DialogInterface.OnCancelListener {
    private Context mContext;
    private ProgressDialog pd = null;
    int progress = -1;

    private final String TAG = DownloadHistoryData.class.getSimpleName();

    public DownloadHistoryData(Context context){
        mContext = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        return downloadData(params);
    }

    private Integer downloadData(String[] params) {
        String url = params[0];
        String condition = params[1];

        return null;
    }


    @Override
    protected void onPreExecute() {
        // We could do some setup work here before doInBackground() runs
        pd = new ProgressDialog(mContext);
        pd.setTitle("正在下载数据");
        pd.setMessage("In Progress...");
        pd.setCancelable(true);
        pd.setOnCancelListener(this);
        pd.setIndeterminate(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        pd.show();

    }

    protected void onProgressUpdate(Integer... progress) {
        pd.setProgress(progress[0]);
    }

    protected void onPostExecute(Integer result) {
        Toast.makeText(mContext, "数据下载完成", Toast.LENGTH_LONG).show();
        pd.cancel();
    }

    private void setProgress(int progress) {
        this.progress = progress;
        publishProgress(this.progress);
    }

    private void sleepFor(long msecs) {
        try {
            Thread.sleep(msecs);
        } catch (InterruptedException e) {
            Log.v("sleep", "interrupted");
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.cancel(true);
    }
}
