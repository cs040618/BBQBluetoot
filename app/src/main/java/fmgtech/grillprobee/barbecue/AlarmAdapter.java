package fmgtech.grillprobee.barbecue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private String[] values;


    public AlarmAdapter(Context context,String[] values) {
        mContext  = context;
        this.values = values;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView    type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null ) {
            convertView = mInflater.inflate(R.layout.alarm_item, null);
            holder = new ViewHolder();
            holder.type = (TextView)    convertView.findViewById(R.id.alarm_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.type.setText(values[position]);
        return convertView;
    }
}
