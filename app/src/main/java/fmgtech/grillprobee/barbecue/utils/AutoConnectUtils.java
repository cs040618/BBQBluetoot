package fmgtech.grillprobee.barbecue.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;

public class AutoConnectUtils {

	public boolean autoConnectStatus = true;   //true表示需要自动连接, false表示手动弹出ble连接页面
	
	public int connectionStatus = 0;   //0表示初使化状态    1表示开始连接         2表示连接结束      如果在1的状状，那么禁止进行下一个ble连接
	
	public String autoConnectMac ;   //当彰自动连接的ble mac

	private static AutoConnectUtils instance;
	
	//自动连接的ble列表
    private HashMap<String,BleConnectInfo> targetList = new HashMap<String,BleConnectInfo> ();

	private AutoConnectUtils() {
	}

	public static AutoConnectUtils getInstance() {
		if (instance == null)
			instance = new AutoConnectUtils();
		return instance;
	}

	 public void cleartargetList(){
	    	targetList.clear();
	 }
	    
     public void removeTargetMac(String mac){
	    	targetList.remove(mac);
	 }

	
	public void addAutoConnectList(String mac, int position,boolean connectStatus) {
		BleConnectInfo info = new BleConnectInfo(position, connectStatus);
		targetList.put(mac, info);
	}
	
	
	//true表示需要自动重连
	public boolean targetListStatus(){
		Iterator<Entry<String,BleConnectInfo>> iter = targetList.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String,BleConnectInfo> entry = (Entry) iter.next();
			String key = entry.getKey();
			BleConnectInfo val = entry.getValue();
			if(!TextUtils.isEmpty(key)){
				if(!val.isConnectStatus()){
					return true;
				}
			}
		}
		return false;	
	}
	
	public HashMap<String,BleConnectInfo> getTargetList(){
	     return targetList;
	}
	
	public void updateTargetList(String mac ,int position,boolean connectStatus){
		if(targetList.get(mac)!=null){
			targetList.get(mac).setConnectStatus(connectStatus);
			if(targetList.get(mac).isConnectStatus()){
				System.out.println("===========1111111111111111111111");
			}else{
				System.out.println("=============22222222222222222222222");
			}
		}
		
	}
}
