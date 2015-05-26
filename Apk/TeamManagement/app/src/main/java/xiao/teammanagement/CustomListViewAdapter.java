package xiao.teammanagement;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
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
        holder.txtName.setText(rowItem.getName().substring(1));

         // holder.imageView.setImageResource(rowItem.getImageId());
        holder.imageView.setImageDrawable(new BitmapDrawable(rowItem.getBitmap()));
        return convertView;
    }
}


