package xiao.teammanagement;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xiao on 2015/5/26.
 */
public class NetworkUtil {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private static String TAG = NetworkUtil.class.getSimpleName();

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    // Using volley imageLoader to fetch remote image file and display in imageView control
    public static void displayImage(RequestQueue queue, final ImageView imageView, String photo, final int width, final int height, final RowItem rowItem, final Context context) {

        ImageLoader mImageLoader = new ImageLoader(queue, new BitmapCache());

      //  ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, R.drawable.new_member);

        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            public void onErrorResponse(VolleyError error) {
                    imageView.setImageResource(R.drawable.new_member);
            }

            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if(response.getBitmap() != null) {
                    Bitmap bitmap = response.getBitmap();
                    bitmap = ToolUtils.zoomImg(bitmap, width, height);
                    imageView.setImageBitmap(bitmap);
                    rowItem.setBitmap(bitmap);

                    // Save photo binary to shared preference to reduce network access
                    SharedPreferences mySharedPreferences = context.getSharedPreferences(DataSet.PREFERENCE_TEAM_PHOTO_DB, Activity.MODE_PRIVATE);
                    String key = String.valueOf(rowItem.getId());

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    String encodedPhotoString = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putString(key, encodedPhotoString);
                    editor.apply();

                } else  {
                    imageView.setImageResource(android.R.drawable.ic_menu_rotate);
                }

            }
        };

        mImageLoader.get(photo, listener, width, height);

    }

    // get photo from remote mysql db blob field
    public static void fetchPhoto(RequestQueue requestQueue, final ImageView imageView, final int width, final int height, final RowItem rowItem, final Context context) {
        final String key = String.valueOf(rowItem.getId());
        Map<String, String> hMap = new HashMap<String, String>();
        hMap.put("id", key);
        Request<JSONObject> jsonRequest = new CustomRequest(Request.Method.POST, DataSet.ACTION_FETCH_PHOTO_URL, hMap,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "response -> " + response.toString());
                        try {
                            boolean success = response.getBoolean("success");
                            if (success){
                                String stream = response.getString("stream");

                                if (stream.isEmpty()){
                                    imageView.setImageResource(android.R.drawable.ic_menu_rotate);
                                } else {
                                    byte[] photoBinary = Base64.decode(stream, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoBinary, 0, photoBinary.length);
                                    bitmap = ToolUtils.zoomImg(bitmap, width, height);
                                    imageView.setImageBitmap(bitmap);
                                    rowItem.setBitmap(bitmap);

                                    // Saved photo binary to shared preference to sync up with Server
                                    SharedPreferences mySharedPreferences = context.getSharedPreferences(DataSet.PREFERENCE_TEAM_PHOTO_DB, Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                                    editor.putString(key, stream);
                                    editor.apply();
                                }

                            } else{
                                Log.d(TAG, "获取成员" + key + "的头像失败");
                                imageView.setImageResource(android.R.drawable.ic_menu_rotate);
                            }

                        } catch(JSONException e){
                            e.printStackTrace();

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
}
