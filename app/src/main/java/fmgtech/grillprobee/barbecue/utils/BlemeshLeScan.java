package fmgtech.grillprobee.barbecue.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BlemeshLeScan implements BluetoothAdapter.LeScanCallback {
	public ArrayList<BluetoothDevice> addresses = new ArrayList<BluetoothDevice>();
	public ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    private Activity         mActivity = null;
    private BluetoothAdapter mBluetoothAdapter = null;
  
    private boolean scanTargetMacStatus = false;
    /**
     * Initialize the scanning object
     */
    public void init(Activity activity, BluetoothAdapter bluetoothAdapter) {
        mActivity         = activity;
        mBluetoothAdapter = bluetoothAdapter;
    }

	public boolean isScanTargetMacStatus() {
		return scanTargetMacStatus;
	}
	
	public void setScanTargetMacStatus(boolean scanTargetMacStatus) {
		this.scanTargetMacStatus = scanTargetMacStatus;
	}
    
    public void clear(){
    	addresses.clear();
        devices.clear();
    }
    
    
    public void startScan(boolean isClear) {
		Log.e("执行startScan00","============"+((mBluetoothAdapter == null)?"true":"false"));
        if (mBluetoothAdapter == null) {
            return;
        }
        if(isClear){
        	clear();
        }
        Log.e("执行startScan","============");
		mBluetoothAdapter.startLeScan(this);
    }
    

    /**
     * Stop LE scanning
     */
    public void stopScan() {
        if (mBluetoothAdapter == null) {
            return;
        }
		mBluetoothAdapter.stopLeScan(this);
		
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRec) {
    	//System.out.println("onLeScan========AutoConnectUtils.getInstance().autoConnectStatus=="+AutoConnectUtils.getInstance().autoConnectStatus);
    	if(AutoConnectUtils.getInstance().autoConnectStatus){
    		String mac = device.getAddress();
    		HashMap<String, BleConnectInfo> targetList = AutoConnectUtils.getInstance().getTargetList();
			if(targetList.containsKey(mac)){
				if(AutoConnectUtils.getInstance().connectionStatus == 1){
					Log.e("需要自动连接的mac=="+mac,"有连接任务正在执行，需要等等===============================================");
				}else{
					Log.e("自动连接的mac=="+mac,"===============================================");
					AutoConnectUtils.getInstance().autoConnectMac = mac;
					AutoConnectUtils.getInstance().connectionStatus = 1;
					Intent intent = new Intent("targetBleMac");
					intent.putExtra("mac", mac);
					intent.putExtra("position", targetList.get(mac).getPosition());
					mActivity.sendBroadcast(intent);
				}
				return;
			}
    	}else{
	        if (addresses.contains(device)) {
				return;
			} else {
				String mac = device.getAddress();
				addresses.add(device);
				Log.e("mac",mac+"===========");
				//String temp = Hex.encodeHexStr(scanRec);
				if(Hex.encodeHexStr(scanRec).contains("0a1800fb")){
					devices.add(device);
					mActivity.sendBroadcast(new Intent("addBle"));
				}
			}
    	}
    }
}
