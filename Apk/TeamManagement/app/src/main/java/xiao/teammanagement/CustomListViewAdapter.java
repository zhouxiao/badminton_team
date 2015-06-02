package xiao.teammanagement;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by Xiao on 2015/5/11.
 */
public class CustomListViewAdapter extends ArrayAdapter<RowItem> {
    Context context;

    public CustomListViewAdapter(Context context, int resourceId, List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder{
        ImageView imageView;
        TextView txtName;
        TextView txtDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView)convertView.findViewById(R.id.desc);
            holder.txtName = (TextView)convertView.findViewById(R.id.name);
            holder.imageView = (ImageView)convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder)convertView.getTag();

        holder.txtDesc.setText(rowItem.getAlias());
        String name = rowItem.getName();

        if(name.length() > 2){
            holder.txtName.setText(name.substring(1));
        } else {
            holder.txtName.setText(name);
        }

        switch (rowItem.getResourceLocation()){
            case DataSet.USING_LOCAL_ARRAY_RESOURCE:
                holder.imageView.setImageResource(rowItem.getImageId());
                break;
            case DataSet.USING_LOCAL_SQLITE_RESOURCE:
                holder.imageView.setImageDrawable(new BitmapDrawable(rowItem.getBitmap()));
                break;
            case DataSet.USING_REMOTE_SERVER_RESOURCE:
                String urlPhoto = DataSet.SERVER_IMAGE_PATH + rowItem.getPhoto();

                SharedPreferences mySharedPreferences = context.getSharedPreferences(DataSet.PREFERENCE_TEAM_PHOTO_DB, Activity.MODE_PRIVATE);
                String key = String.valueOf(rowItem.getId());
                if (mySharedPreferences.contains(key)){   // Photo binary available in Shared Preference, get it locally
                    String encodedPhotoString = mySharedPreferences.getString(key, null);
                        if (encodedPhotoString != null){
                         byte[] photoBinary = Base64.decode(encodedPhotoString, Base64.DEFAULT);
                         Bitmap bitmap = BitmapFactory.decodeByteArray(photoBinary, 0, photoBinary.length);
                         holder.imageView.setImageDrawable(new BitmapDrawable(bitmap));
                         rowItem.setBitmap(bitmap);
                        } else {
                            holder.imageView.setImageResource(android.R.drawable.ic_menu_rotate);
                            BitmapDrawable bd = (BitmapDrawable)holder.imageView.getDrawable();
                            Bitmap bitmap = bd.getBitmap();
                            bitmap = ToolUtils.zoomImg(bitmap, 80, 80);
                            rowItem.setBitmap(bitmap);

                        }
                } else{  // fetch it from remote server

                   // NetworkUtil.fetchPhoto(AppController.getInstance().getRequestQueue(),holder.imageView, 80, 80, rowItem, context);

                    NetworkUtil.displayImage(                           // Using volley to fetch remote image
                            AppController.getInstance().getRequestQueue(),
                            holder.imageView,
                            urlPhoto,
                            80,
                            80,
                            rowItem,
                            context
                    );

                }
                break;
            default:
                break;
        }

        return convertView;
    }


}


