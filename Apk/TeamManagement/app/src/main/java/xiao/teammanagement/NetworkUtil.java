package xiao.teammanagement;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;

/**
 * Created by Xiao on 2015/5/26.
 */
public class NetworkUtil {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


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

    // Using volley imageLoader to fetch remote image and display in imageView control
    public static void displayImage(RequestQueue queue, final ImageView imageView, String photo, int width, int height, final RowItem rowItem, final Context context) {

        ImageLoader mImageLoader = new ImageLoader(queue, new BitmapCache());

      //  ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, R.drawable.new_member);

        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            public void onErrorResponse(VolleyError error) {
                    imageView.setImageResource(R.drawable.new_member);
            }

            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if(response.getBitmap() != null) {
                    Bitmap bitmap = response.getBitmap();
                    bitmap = ToolUtils.zoomImg(bitmap, 80, 80);
                    imageView.setImageBitmap(bitmap);
                    rowItem.setBitmap(bitmap);

                    // Save photo binary to shared preference to reduce network access
                    SharedPreferences mySharedPreferences = context.getSharedPreferences("team_photo", Activity.MODE_PRIVATE);
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
}
