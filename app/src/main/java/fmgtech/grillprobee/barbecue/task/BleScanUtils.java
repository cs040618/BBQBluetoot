package fmgtech.grillprobee.barbecue.task;

import java.util.Timer;
import java.util.TimerTask;

import fmgtech.grillprobee.barbecue.utils.AutoConnectUtils;
import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
import fmgtech.grillprobee.barbecue.utils.BlemeshLeScan;


import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class BleScanUtils {
	Context context;
	BlemeshLeScan mScan;
	private static BleScanUtils instance;
	Timer timer;
	public boolean status = false;  //true表示正在运行      false表示没有工作
   
    public static BleScanUtils getInstance(BlemeshLeScan mScan, Context context) {
        if (instance == null) instance = new BleScanUtils(mScan,context);
        return instance;
    }
	
	private BleScanUtils(BlemeshLeScan mScan, Context context) {
		this.mScan = mScan;
		this.context = context;
	}

	public void run() {
		if(!status){
			BleScanTimerTask task = new BleScanTimerTask();
			timer = new Timer();
			timer.schedule(task, 0, 10000);
			status = true;
		}
		
	}
	
	public void stop(){
		if(status){
			timer.cancel();
			mScan.stopScan();
			status = false;
		}
	}

	class BleScanTimerTask extends TimerTask {

		public BleScanTimerTask() {
		}

		@Override
		public void run() {
			Log.e("scan task","=================");
			mScan.stopScan();
			SystemClock.sleep(20);
			if(AutoConnectUtils.getInstance().autoConnectStatus){
				mScan.startScan(true);
			}else{
				mScan.startScan(false);
			}
		}
	}
}
