package fmgtech.grillprobee.barbecue.task;

import java.util.TimerTask;

import fmgtech.grillprobee.barbecue.utils.BarbecueParamer;
import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
import fmgtech.grillprobee.barbecue.utils.DataUtils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.util.Log;

public class CutTimerTask extends TimerTask {
	private int position, leftTime;
	Context context;

	public CutTimerTask(int position, int leftTime, Context context) {
		this.position = position;
		this.leftTime = leftTime;
		this.context = context;
	}
	

	public void setLeftTime(int leftTime) {
		this.leftTime = leftTime;
	}



	@Override
	public void run() {
		leftTime--;
		Log.e("leftTime","======================"+leftTime+",position=="+position);
		if (leftTime <= 0) {
			cancel();
			Intent alarm = new Intent("timerAlarmRun");
			alarm.putExtra("position", position);
			context.sendBroadcast(alarm);
			Intent intent = new Intent("CutTimerUpdate");
			intent.putExtra("leftTime", 0);
			intent.putExtra("position", position);
			context.sendBroadcast(intent);
		}else{
			Intent intent = new Intent("CutTimerUpdate");
			if(leftTime==1){
				BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
				if(paramer!=null){
					if(paramer.getStatus()!=2){
						leftTime = 30;
					}
				}
			}
			intent.putExtra("leftTime", leftTime);
			intent.putExtra("position", position);
			context.sendBroadcast(intent);
		}

	}
}
