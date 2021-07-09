package fmgtech.grillprobee.barbecue;

import java.util.ArrayList;

import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
import fmgtech.grillprobee.barbecue.utils.DeviceRecord;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConnectedDeviceAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;
    

    public ConnectedDeviceAdapter(Context context) {
        mContext  = context;
        mInflater = LayoutInflater.from(context);
    }

   




	public DeviceRecord getDevice(int position) {
        if (position < BleConnectUtils.connectList.size())
            return BleConnectUtils.connectList.get(position);
        return null;
    }
    
    @Override
    public int getCount() {
    	if(BleConnectUtils.connectList==null){
    		return 0;
    	}
        return BleConnectUtils.connectList.size();
    }

    @Override
    public Object getItem(int position) {
    	if(BleConnectUtils.connectList==null){
    		return null;
    	}
        return BleConnectUtils.connectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView    deviceName;
        TextView    deviceAddr;
        TextView    devicePosition;
        ImageView   position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null ) {
            convertView = mInflater.inflate(R.layout.connected_ble_item, null);
            holder = new ViewHolder();
            holder.deviceAddr = (TextView)    convertView.findViewById(R.id.device_addr);
            holder.deviceName = (TextView)    convertView.findViewById(R.id.device_name);
            holder.position = (ImageView)     convertView.findViewById(R.id.position);
            holder.devicePosition = (TextView)     convertView.findViewById(R.id.tv_position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        System.out.println("ConnectedDeviceAdapter getView  position =="+position);
        DeviceRecord rec = BleConnectUtils.connectList.get(position);
        String bleName = rec.getName();
        String mac = rec.getMac();
        holder.deviceName.setText(bleName);
        holder.deviceAddr.setText(mac);
        holder.devicePosition.setText(rec.getPosition()+"");
        switch (rec.getPosition()) {
			case 1:
				 holder.position.setImageResource(R.drawable.one_way);
				break;
			case 2:
				 holder.position.setImageResource(R.drawable.two_way);
				break;
			case 3:
				 holder.position.setImageResource(R.drawable.three_way);
				break;
			case 4:
				 holder.position.setImageResource(R.drawable.four_way);
				break;
		}
        return convertView;
    }    
}
