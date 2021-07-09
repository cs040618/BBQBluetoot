package fmgtech.grillprobee.barbecue;

import java.util.ArrayList;

import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
import fmgtech.grillprobee.barbecue.utils.DataUtils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<BluetoothDevice> mDevices;
    private final LayoutInflater mInflater;
    

    public DeviceAdapter(Context context,ArrayList<BluetoothDevice> mDevices) {
        mContext  = context;
        mInflater = LayoutInflater.from(context);
        this.mDevices  = mDevices;
    }

    public ArrayList<BluetoothDevice> getmDevices() {
		return mDevices;
	}
    
	public void setmDevices(ArrayList<BluetoothDevice> mDevices) {
		this.mDevices = mDevices;
	}




	public BluetoothDevice getDevice(int position) {
        if (position < mDevices.size())
            return mDevices.get(position);
        return null;
    }
    
    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView    deviceName;
        TextView    deviceAddr;
    }

    @SuppressLint("ResourceAsColor")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null ) {
            convertView = mInflater.inflate(R.layout.ble_listitem, null);
            holder = new ViewHolder();
            holder.deviceAddr = (TextView)    convertView.findViewById(R.id.device_addr);
            holder.deviceName = (TextView)    convertView.findViewById(R.id.device_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        System.out.println("getView   position == "+position);
        if(BleConnectUtils.connectList.size()== DataUtils.maxConnectedBleSize){
        	holder.deviceName.setTextColor(R.color.gray);
        }
        BluetoothDevice rec = mDevices.get(position);
        String bleName = rec.getName();
        String mac = rec.getAddress();
        holder.deviceName.setText(bleName);
        holder.deviceAddr.setText(mac);
        return convertView;
    }    
}
