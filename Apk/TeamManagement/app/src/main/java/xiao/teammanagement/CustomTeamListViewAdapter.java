package xiao.teammanagement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Xiao on 2015/5/11.
 */
public class CustomTeamListViewAdapter extends ArrayAdapter<RowItem> {
    Context context;

    public CustomTeamListViewAdapter(Context context, int resourceId, List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder{
        ImageView imageView;
        TextView txtName;
        TextView txtAlias;
        TextView txtSex;
        TextView txtAge;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.team_list_item, null);
            holder = new ViewHolder();
            holder.txtName = (TextView)convertView.findViewById(R.id.field_name);
            holder.txtAlias = (TextView)convertView.findViewById(R.id.field_alias);
            holder.txtSex = (TextView)convertView.findViewById(R.id.field_sex);
            holder.txtAge = (TextView)convertView.findViewById(R.id.field_age);
            holder.imageView = (ImageView)convertView.findViewById(R.id.field_photo);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder)convertView.getTag();

        holder.txtName.setText(rowItem.getName());
        holder.txtAlias.setText(rowItem.getAlias());
        holder.txtSex.setText(rowItem.getSex() == 0 ? "Female" : "Male");
        holder.txtAge.setText(rowItem.getAge() + "年");


        switch (rowItem.getResourceLocation()){
            case DataSet.USING_LOCAL_ARRAY_RESOURCE:
                holder.imageView.setImageResource(rowItem.getImageId());
                break;
            case DataSet.USING_LOCAL_SQLITE_RESOURCE:
            case DataSet.USING_REMOTE_SERVER_RESOURCE:
                holder.imageView.setImageDrawable(new BitmapDrawable(rowItem.getBitmap()));
                break;

            default:
                break;
        }


        return convertView;
    }
}


