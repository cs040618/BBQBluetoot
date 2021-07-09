package fmgtech.grillprobee.barbecue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fmgtech.grillprobee.barbecue.task.CompstateClearTask;
import fmgtech.grillprobee.barbecue.utils.AutoConnectUtils;
import fmgtech.grillprobee.barbecue.utils.BarbecueParamer;
import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
import fmgtech.grillprobee.barbecue.utils.BlemeshIntent;
import fmgtech.grillprobee.barbecue.utils.CrashHandler;
import fmgtech.grillprobee.barbecue.utils.DataUtils;
import fmgtech.grillprobee.barbecue.utils.DeviceRecord;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

@SuppressLint("NewApi")
public class GrillApplication extends Application {
	private Context mContext;
	private Dialog dialog;
	Dialog alarmDialog;
	TextView alarmDialogTitle;
	public static HashMap<String,CompstateClearTask> handlerRun = new HashMap<>();
	//public ArrayList bbqn = new ArrayList<String>();
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		CrashHandler mCustomCrashHandler = CrashHandler.getInstance();
		mCustomCrashHandler.setCustomCrashHanler(getApplicationContext());
		getLanguageType();
		DataUtils.initAlarm(this);
		DataUtils.initTemperatureUnit(this);
		DataUtils.initGrillData(mContext.getResources());
		IntentFilter filter = new IntentFilter();
		filter.addAction("timerAlarmRun");
		filter.addAction("highTemperatureAlarm");
		filter.addAction("highGrillTemperatureAlarm");
		registerReceiver(alarmReceiver, filter);
		filter = new IntentFilter();
		filter.addAction(BlemeshIntent.ACTION_GATT_CONNECTED);
		filter.addAction(BlemeshIntent.ACTION_GATT_DISCONNECTED);
		filter.addAction(BlemeshIntent.ACTION_GATT_SEND_STATUS);
		filter.addAction(BlemeshIntent.ACTION_GATT_CONNECTION_ERROR);
		filter.addAction(BlemeshIntent.ACTION_GATT_DISCONNECTED_ERROR);
		registerReceiver(mReceiver, filter);

		filter = new IntentFilter();
		// 屏幕灭屏广播
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		// 屏幕亮屏广播
		filter.addAction(Intent.ACTION_SCREEN_ON);
		// 屏幕解锁广播
		filter.addAction(Intent.ACTION_USER_PRESENT);
		BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				String action = intent.getAction();
				Log.e(action,"================");
				if (Intent.ACTION_SCREEN_ON.equals(action)) {
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					BarbecueParamer paramer;
					boolean status = false;
					for(int i = 1;i<=4;i++){
						if(BleConnectUtils.uiMap.containsKey(i)){
							paramer = BleConnectUtils.uiMap.get(i);
							if(paramer.getWorkStatus()==1){
								status = true;
								break;
							}
						}
					}
					if(status) {
						PowerManager pm = (PowerManager) context.getSystemService(Activity.POWER_SERVICE);
						PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
						wl.acquire();//亮屏
						String activityName = "";
						if (isApplicationBroughtToBackground(getApplicationContext())) {
							// 遍历map中的键
							for (String key : DataUtils.activityMap.keySet()) {
								activityName = DataUtils.activityMap.get(key);
							}
							try {
								Intent intent1 = new Intent(context, Class.forName(activityName));
								intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent1);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
				} else if (Intent.ACTION_USER_PRESENT.equals(action)) {

				} else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
				}
			}
		};

		registerReceiver(mBatInfoReceiver, filter);
	}

	private void getLanguageType(){
		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if(language.contains("en")){
			DataUtils.languageType = 1;
		}else if(language.contains("cn")){
			DataUtils.languageType = 2;
		}else if(language.contains("de")){
			DataUtils.languageType = 3;
		}else if(language.contains("zh")){
			DataUtils.languageType = 4;
		}
	}

	Handler handler = new Handler();
	private String getCurrentActivityName(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		return componentInfo.getClassName();
	}


	private final BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String activityName = "";
			Intent alarm = null;
			String action = intent.getAction();
			if (isApplicationBroughtToBackground(getApplicationContext())) {
				// 遍历map中的键
				for (String key : DataUtils.activityMap.keySet()) {
					activityName = DataUtils.activityMap.get(key);
				}
				try {
					Intent intent1 = new Intent(context, Class.forName(activityName));
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent1);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				if (action.equals("timerAlarmRun")) {
					if (activityName.contains("ConnectActivity")) {
						alarm = new Intent("showAlarmDialogInMainPager");
					} else if (activityName.contains("AllProbesActivity")) {
						alarm = new Intent("showAlarmDialogInAllProbesPager");
					}
				} else if (action.equals("highTemperatureAlarm")) {
					if (activityName.contains("ConnectActivity")) {
						alarm = new Intent("showHighAlarmDialogInMainPager");
					} else if (activityName.contains("AllProbesActivity")) {
						alarm = new Intent(
								"showHighAlarmDialogInAllProbesPager");
					}
				}else if (action.equals("highGrillTemperatureAlarm")) {
					if (activityName.contains("ConnectActivity")) {
						alarm = new Intent("showHighGrillAlarmDialogInMainPager");
					} else if (activityName.contains("AllProbesActivity")) {
						alarm = new Intent(
								"showHighGrillAlarmDialogInAllProbesPager");
					}
				}
			} else {
				String name = getCurrentActivityName(mContext);
				if (action.equals("timerAlarmRun")) {
					if (name.contains("ConnectActivity")) {
						alarm = new Intent("showAlarmDialogInMainPager");
					} else if (name.contains("AllProbesActivity")) {
						alarm = new Intent("showAlarmDialogInAllProbesPager");
					}
				} else if (action.equals("highTemperatureAlarm")) {
					if (name.contains("ConnectActivity")) {
						alarm = new Intent("showHighAlarmDialogInMainPager");
					} else if (name.contains("AllProbesActivity")) {
						alarm = new Intent(
								"showHighAlarmDialogInAllProbesPager");
					}
				}else if (action.equals("highGrillTemperatureAlarm")) {
					if (name.contains("ConnectActivity")) {
						alarm = new Intent("showHighGrillAlarmDialogInMainPager");
					} else if (name.contains("AllProbesActivity")) {
						alarm = new Intent(
								"showHighGrillAlarmDialogInAllProbesPager");
					}
				}
			}
			final int position = intent.getIntExtra("position", 0);
			if (alarm != null) {
				alarm.putExtra("position", position);
				sendBroadcast(alarm);
			}
		}

	};

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String name = getCurrentActivityName(mContext);
			if (action.equals(BlemeshIntent.ACTION_GATT_CONNECTED)) {
				Bundle bundle = intent.getExtras();
				int scanPosition = bundle.getInt("position");
				int connectPosition = bundle.getInt("connectPosition");
				String mac = bundle.getString("mac");
				if(AutoConnectUtils.getInstance().autoConnectStatus){
					AutoConnectUtils.getInstance().addAutoConnectList(mac,
							connectPosition, true);
				}
				String bleName = bundle.getString("name");
				if(mac.equals(AutoConnectUtils.getInstance().autoConnectMac)){
					AutoConnectUtils.getInstance().connectionStatus = 2;
					AutoConnectUtils.getInstance().autoConnectMac = "";
				}
				switch (connectPosition) {
				case 1:
					if (BleConnectUtils.channelMac1 != null) {
						if (!mac.equals(BleConnectUtils.channelMac1)) {
							BleConnectUtils.uiMap.remove(connectPosition);
							BleConnectUtils.channelMac1 = mac;
							BleConnectUtils.channelName1 = bleName;
						}
					} else {
						BleConnectUtils.channelMac1 = mac;
						BleConnectUtils.channelName1 = bleName;
					}

					break;
				case 2:
					if (BleConnectUtils.channelMac2 != null) {
						if (!mac.equals(BleConnectUtils.channelMac2)) {
							BleConnectUtils.uiMap.remove(connectPosition);
							BleConnectUtils.channelMac2 = mac;
							BleConnectUtils.channelName2 = bleName;
						}
					} else {
						BleConnectUtils.channelMac2 = mac;
						BleConnectUtils.channelName2 = bleName;
					}
					break;
				case 3:
					if (BleConnectUtils.channelMac3 != null) {
						if (!mac.equals(BleConnectUtils.channelMac3)) {
							BleConnectUtils.uiMap.remove(connectPosition);
							BleConnectUtils.channelMac3 = mac;
							BleConnectUtils.channelName3 = bleName;
						}
					} else {
						BleConnectUtils.channelMac3 = mac;
						BleConnectUtils.channelName3 = bleName;
					}
					break;
				case 4:
					if (BleConnectUtils.channelMac4 != null) {
						if (!mac.equals(BleConnectUtils.channelMac4)) {
							BleConnectUtils.uiMap.remove(connectPosition);
							BleConnectUtils.channelMac4 = mac;
							BleConnectUtils.channelName4 = bleName;
						}
					} else {
						BleConnectUtils.channelMac2 = mac;
						BleConnectUtils.channelName2 = bleName;
					}
					break;
				}
				if(BleConnectUtils.compensateMap.get(mac)!=null){
					handler.removeCallbacks(handlerRun.get(mac));
					handlerRun.remove(mac);
				}
				BleConnectUtils.connectList.add(new DeviceRecord(bleName, mac,
						connectPosition));
				Bundle bundle1 = new Bundle();
				bundle1.putInt("position", connectPosition);
				bundle1.putInt("scanPosition", scanPosition);
				Intent connectIntent = new Intent("connectedMainPager");
				connectIntent.putExtras(bundle1);
				sendBroadcast(connectIntent);
				connectIntent = new Intent("connectedAllProbesPager");
				connectIntent.putExtras(bundle1);
				sendBroadcast(connectIntent);
				/*
				 * connectIntent = new Intent("connectedSettingPager");
				 * connectIntent.putExtras(bundle1);
				 * sendBroadcast(connectIntent);
				 */
			} else if (action.equals(BlemeshIntent.ACTION_GATT_DISCONNECTED)||action.equals(BlemeshIntent.ACTION_GATT_DISCONNECTED_ERROR)) {
				Bundle bundle = intent.getExtras();
				final String mac = bundle.getString("mac");
				final int removePosition = bundle.getInt("removePosition");
				if(BleConnectUtils.compensateMap.get(mac)!=null) {
					CompstateClearTask runnable = new CompstateClearTask(mac);
					handler.postDelayed(runnable, 60 * 1000);
					handlerRun.put(mac,runnable);
				}
				BleConnectUtils.uiMap.remove(removePosition);
				int size = BleConnectUtils.connectList.size();
				DeviceRecord record = null;
				for (int i = 0; i < size; i++) {
					record = BleConnectUtils.connectList.get(i);
					if (record.getPosition() == removePosition) {
						BleConnectUtils.connectList.remove(i);
						if(AutoConnectUtils.getInstance().autoConnectStatus){
							AutoConnectUtils.getInstance().addAutoConnectList(mac,
									removePosition, false);
						}
						record.mac = mac;
						break;
					}
				}
				try {
					BleConnectUtils.connectMap.get(removePosition).stopMyTimeTask();
				}catch (Exception e){
					e.printStackTrace();
				}
				Intent disConnectIntent = new Intent("disConnectedMainPager");
				disConnectIntent.putExtra("removePosition", removePosition);
				disConnectIntent.putExtra("mac", mac);
				sendBroadcast(disConnectIntent);
				disConnectIntent = new Intent("disConnectedAllProbesPager");
				disConnectIntent.putExtra("removePosition", removePosition);
				sendBroadcast(disConnectIntent);
				disConnectIntent = new Intent("disConnectedSettingPager");
				disConnectIntent.putExtra("removePosition", removePosition);
				sendBroadcast(disConnectIntent);
				Intent alarm = null;
				if(AutoConnectUtils.getInstance().autoConnectStatus){
					if (name.contains("ConnectActivity")) {
						alarm = new Intent("showDisconnectAlarmDialogInMainPager");
					} else if (name.contains("AllProbesActivity")) {
						alarm = new Intent(
								"showDisconnectAlarmDialogInAllProbesPager");
					}
					if (alarm != null) {
						alarm.putExtra("position", removePosition);
						context.sendBroadcast(alarm);
					}
				}
			} else if (action.equals(BlemeshIntent.ACTION_GATT_SEND_STATUS)) {
				int status = intent.getIntExtra(BlemeshIntent.EXTRA_STATUS, 0);
			}else if(action.equals(BlemeshIntent.ACTION_GATT_CONNECTION_ERROR)){
				Bundle bundle = intent.getExtras();
				int removePosition = bundle.getInt("removePosition");
				String mac = null;
				int size = BleConnectUtils.connectList.size();
				DeviceRecord record = null;
				for (int i = 0; i < size; i++) {
					record = BleConnectUtils.connectList.get(i);
					if (record.getPosition() == removePosition) {
						BleConnectUtils.connectList.remove(i);
						if(AutoConnectUtils.getInstance().autoConnectStatus){
							AutoConnectUtils.getInstance().addAutoConnectList(mac,
									removePosition, false);
						}
						record.mac = mac;
						break;
					}
				}
				Intent disConnectIntent = new Intent("connectedErrorMainPager");
				disConnectIntent.putExtra("removePosition", removePosition);
				disConnectIntent.putExtra("mac", mac);
				if(BleConnectUtils.compensateMap.get(mac)!=null) {
					CompstateClearTask runnable = new CompstateClearTask(mac);
					handler.postDelayed(runnable, 60 * 1000);
					handlerRun.put(mac,runnable);
				}
				sendBroadcast(disConnectIntent);
				disConnectIntent = new Intent("connectedErrorAllProbesPager");
				disConnectIntent.putExtra("removePosition", removePosition);
				sendBroadcast(disConnectIntent);
			}
		}
	};


		@Override
	public void onTerminate() {
		super.onTerminate();
		unregisterReceiver(alarmReceiver);
		unregisterReceiver(mReceiver);
	}

	/**
	 * 判断当前应用程序处于前台还是后台
	 */
	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}


}
