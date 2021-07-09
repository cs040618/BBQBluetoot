package fmgtech.grillprobee.barbecue.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import fmgtech.grillprobee.barbecue.task.CutTimerTask;

public class BleConnectUtils {
	public static HashMap<Integer, BleGatt> connectMap = new HashMap<Integer, BleGatt>();
	public static HashMap<Integer, BarbecueParamer> uiMap = new HashMap<Integer, BarbecueParamer>();
	public static Timer timer1, timer2, timer3, timer4;
	public static CutTimerTask cutTimerTask1, cutTimerTask2, cutTimerTask3,
			cutTimerTask4;
	public static float grill_temperature1 = -1, grill_temperature2 = -1,
			grill_temperature3 = -1, grill_temperature4 = -1;
	public static float current_temperature1 = -1, current_temperature2 = -1,
			current_temperature3 = -1, current_temperature4 = -1;
	public static boolean batteryStatus1, batteryStatus2, batteryStatus3,
			batteryStatus4;
	public static boolean oneStatus, twoStatus, threeStatus, fourStatus;
	public static ArrayList<DeviceRecord> connectList = new ArrayList<DeviceRecord>();
	public static String channelMac1, channelMac2, channelMac3, channelMac4;
	public static String channelName1, channelName2, channelName3,
			channelName4;
	public static int batteryLow1, batteryLow2, batteryLow3,
			batteryLow4;
	public static HashMap<String,Boolean> compensateMap = new HashMap<>();  //断开时的补偿状态

	ArrayList<Integer> alarmChannel = new ArrayList<Integer>();

	public static void clearData(int position) {
		BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
		boolean isStop = false;
		if(paramer!=null ){
			paramer.setWorkStatus(2);
			if(paramer.getStatus()!=2){
				isStop = true;
			}
		}
		switch (position) {
		case 1:
			if(isStop){
				if (cutTimerTask1 != null) {
					cutTimerTask1.cancel();
					cutTimerTask1 = null;
				}
				if (timer1 != null)
					timer1.cancel();
			}
			break;
		case 2:
			if(isStop){
				if (cutTimerTask2 != null) {
					cutTimerTask2.cancel();
					cutTimerTask2 = null;
				}
				if (timer2 != null)
					timer2.cancel();
			}
			break;
		case 3:
			if(isStop){
				if (cutTimerTask3 != null) {
					cutTimerTask3.cancel();
					cutTimerTask3 = null;
				}
				if (timer3 != null)
					timer3.cancel();
			}
			break;
		case 4:
			if(isStop){
				if (cutTimerTask4 != null) {
					cutTimerTask4.cancel();
					cutTimerTask4 = null;
				}
	
				if (timer4 != null)
					timer4.cancel();
			}
			break;
		}
	}

}
