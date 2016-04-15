package ras.asu.com.letsmeet;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CustomListViewAdapterForPersonPlace extends ArrayAdapter<RowItem> {

    Context context;

    public CustomListViewAdapterForPersonPlace(Context context, int resourceId,
                                               List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }
    ViewGroup parent;
    int position1;
    public View getView(int position, View convertView, ViewGroup parent1) {
        ViewHolder holder = null;
        position1 =position;
        RowItem rowItem = getItem(position);
        parent =parent1;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_friends_place, null);
            holder = new ViewHolder();
            //holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        //holder.txtDesc.setText(rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());
        holder.imageView.setImageBitmap(rowItem.getImage());

        //holder.imageView.setImageResource(rowItem.getImageId());

        return convertView;
    }
}