package fmgtech.grillprobee.barbecue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import fmgtech.grillprobee.barbecue.task.CutTimerTask;
import fmgtech.grillprobee.barbecue.utils.BarbecueParamer;
import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
import fmgtech.grillprobee.barbecue.utils.BleGatt;
import fmgtech.grillprobee.barbecue.utils.BlemeshIntent;
import fmgtech.grillprobee.barbecue.utils.DataUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class AllProbesActivity extends Activity {
	FrameLayout ring1,ring2,ring3,ring4;
	Animation animation1,animation2,animation3,animation4,batteryLow1Animation, batteryLow2Animation, batteryLow3Animation, batteryLow4Animation,grill1Animation,current1Animation,grill2Animation,current2Animation,grill3Animation,current3Animation,grill4Animation,current4Animation;
	private int bitmapWidth, bitmapHeigh;
	private float percentage = 1;
	Dialog alarmDialog,disconnectAlarmDialog ,highGrillAlarmDialog,highAlarmDialog;
	private Context context;
	private int pagerStatus = 2;;
	ImageView title_bar_back, status, one_way, two_way, battery1, battery2,
			three_way, four_way, battery3, battery4;
	TextView barbecue_status1, target1, grill1, barbecue_status2, target2,
			grill2, barbecue_status3, target3, grill3, barbecue_status4,
			target4, grill4, item_logo, left_time_tv_1, left_time_tv_2,
			left_time_tv_3, left_time_tv_4, current_temperature_tv_1,
			current_temperature_tv_2, current_temperature_tv_3,
			current_temperature_tv_4,name_label_1,name_label_2,name_label_3,name_label_4;
	Drawable drawable1, drawable2, drawable3, drawable4;
	private int leftTime1, leftTime2, leftTime3, leftTime4;
	LinearLayout panView1_status_layout, panView2_status_layout,
			panView3_status_layout, panView4_status_layout;
	float grillTemperature, targetTemperature, currentTemperature;
	TextView alarmDialogTitle,disconnectAlarmDialogTitle,highGrillAlarmDialogTitle,highAlarmDialogTitle;
	String temperatureUnit;
	HashMap<Integer,String> alarmChannel = new HashMap<Integer,String>();

	ImageView grill_temperature_1, target_temperature_1, current_temperature_1,
			grill_temperature_2, target_temperature_2, current_temperature_2,
			grill_temperature_3, target_temperature_3, current_temperature_3,
			grill_temperature_4, target_temperature_4, current_temperature_4,
			setting;

	private final String TAG = "AllP";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.all_probes);
		context = this;
		IntentFilter filter = new IntentFilter();
		filter.addAction("CutTimerUpdate");
		filter.addAction("RaiseTimerUpdate");
		registerReceiver(timerUIReceiver, filter);
		filter.addAction(BlemeshIntent.ACTION_GATT_DISCONNECTED);
		filter.addAction(BlemeshIntent.ACTION_GATT_DISCONNECTED_ERROR);
		filter.addAction("connectedAllProbesPager");
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter();
		filter.addAction("showAlarmDialogInAllProbesPager");
		registerReceiver(alarmReceiver, filter);

		filter = new IntentFilter();
		filter.addAction("showHighAlarmDialogInAllProbesPager");
		registerReceiver(highAlarmReceiver, filter);
		filter = new IntentFilter();
		filter.addAction("showHighGrillAlarmDialogInAllProbesPager");
		registerReceiver(highGrillAlarmReceiver, filter);


		filter = new IntentFilter();
		filter.addAction("temperatureData");
		filter.addAction("leftTime");
		registerReceiver(bleDataReceiver, filter);
		filter = new IntentFilter();
		filter.addAction("showDisconnectAlarmDialogInAllProbesPager");
		registerReceiver(disconnectAlarmReceiver, filter);
		filter = new IntentFilter();
		filter.addAction("temperatureUpdateUI");
		registerReceiver(temperatureUpdateUIReceiver, filter);
		filter = new IntentFilter();
		filter.addAction("AllProbesBleWorkStatus");
		registerReceiver(bleWorkStatus, filter);
		filter = new IntentFilter();
		filter.addAction("autoConnectFinish");
		registerReceiver(autoConnectFinishReceiver, filter);
		filter = new IntentFilter();
		filter.addAction("batteryLow");
		registerReceiver(batteryLowReceiver, filter);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.source);
		bitmapWidth = bitmap.getWidth();
		bitmapHeigh = bitmap.getHeight();
		if (bitmapWidth == 443) {
			percentage = 1;
		} else {
			percentage = bitmapWidth / 443f;
		}
		init();
		initTextViewValue();
		showTargetTemperature();
		displayBatteryLow();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(DataUtils.activityMap.get("ConnectActivity")!=null){
			DataUtils.activityMap.remove("ConnectActivity");
		}
		DataUtils.activityMap.put("AllProbesActivity", "fmgtech.grillprobee.barbecue.AllProbesActivity");
	}

	// 显示通道状态
	private void showWayStatus(int way) {
		switch (way) {
			case 1:
				if (BleConnectUtils.oneStatus) {
					one_way.setImageResource(R.drawable.one_small_connect);
				} else {
					one_way.setImageResource(R.drawable.one_small_disconnect);
				}
				break;
			case 2:
				if (BleConnectUtils.twoStatus) {
					two_way.setImageResource(R.drawable.two_small_connect);
				} else {
					two_way.setImageResource(R.drawable.two_small_disconnect);
				}
				break;
			case 3:
				if (BleConnectUtils.threeStatus) {
					three_way.setImageResource(R.drawable.three_small_connect);
				} else {
					three_way.setImageResource(R.drawable.three_small_disconnect);
				}
				break;
			case 4:
				if (BleConnectUtils.fourStatus) {
					four_way.setImageResource(R.drawable.four_small_connect);
				} else {
					four_way.setImageResource(R.drawable.four_small_disconnect);
				}
				break;
		}
	}



	private void showTargetTemperature(int position){
		switch (position) {
			case 1:
				if(BleConnectUtils.oneStatus){
					temperatureHand(BleConnectUtils.current_temperature1,
							BleConnectUtils.grill_temperature1, 1);
				}
				break;

			case 2:
				if(BleConnectUtils.twoStatus){
					temperatureHand(BleConnectUtils.current_temperature2,
							BleConnectUtils.grill_temperature2, 2);
				}
				break;
			case 3:
				if(BleConnectUtils.threeStatus){
					temperatureHand(BleConnectUtils.current_temperature3,
							BleConnectUtils.grill_temperature3, 3);
				}
				break;
			case 4:
				if(BleConnectUtils.fourStatus){
					temperatureHand(BleConnectUtils.current_temperature4,
							BleConnectUtils.grill_temperature4, 4);
				}
				break;
		}
		BarbecueParamer barbecueParamer = BleConnectUtils.uiMap.get(position);
		if(barbecueParamer==null){
			System.out.println("barbecueParamer========================null");
			return;
		}
		int status = barbecueParamer.getStatus();
		int type = barbecueParamer.getType();
		String degree = barbecueParamer.getDegree();
		int targetTemperature = 0;
		Drawable drawable = DataUtils.updateDrawable(status, type, context,
				pagerStatus);
		String unit = (DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
				: DataUtils.fahrenhite;
		if (status > 0 && status != 2) {
			targetTemperature = barbecueParamer.getTemperature();
			float rotation = 210 + targetTemperature;
			if (rotation > 360) {
				rotation -= 360;
			}
			switch (DataUtils.temperatureUnit) {
				case 1:
					targetTemperature = DataUtils
							.centigrade2Fahrenhite(targetTemperature);
					break;
				default:
					break;
			}
			String text = getResources().getString(
					R.string.single_barbecue_target_temperature,
					targetTemperature+"", unit).replaceAll("\n", " ");
			int size = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
			int pushSize = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
			String splitValue = " ";
			int defaultColor = getResources().getColor(R.color.barbecue_status_text_color);
			int pushColor = Color.YELLOW;
			SpannableStringBuilder builder = DataUtils.setTextStyle(text,splitValue,size,pushSize,defaultColor,pushColor);
			switch (position) {
				case 1:
					if (target_temperature_1.getVisibility() == View.INVISIBLE) {
						target_temperature_1.setVisibility(View.VISIBLE);
					}
					if (target1.getVisibility() == View.INVISIBLE) {
						target1.setVisibility(View.VISIBLE);
					}
					target1.setText(builder);
					Log.i(TAG, "showTargetTemperature " + builder.toString()  );
					target_temperature_1
							.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
									.sin(rotation * Math.PI / 180)));
					target_temperature_1
							.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
									.cos(rotation * Math.PI / 180)));
					target_temperature_1.setRotation(rotation);
					if (barbecue_status1.getVisibility() == View.INVISIBLE) {
						barbecue_status1.setVisibility(View.VISIBLE);
					}
					barbecue_status1.setCompoundDrawables(drawable, null, null,
							null);
					if (status == 1) {
						barbecue_status1.setText(getResources().getString(
								R.string.single_barbecue_type, "", degree)
								.replace("\n", ""));
					} else {
						barbecue_status1.setText(R.string.temperature);
					}

					break;

				case 2:
					if (target_temperature_2.getVisibility() == View.INVISIBLE) {
						target_temperature_2.setVisibility(View.VISIBLE);
					}
					if (target2.getVisibility() == View.INVISIBLE) {
						target2.setVisibility(View.VISIBLE);
					}

					target2.setText(builder);

					target_temperature_2
							.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
									.sin(rotation * Math.PI / 180)));
					target_temperature_2
							.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
									.cos(rotation * Math.PI / 180)));
					target_temperature_2.setRotation(rotation);

					if (barbecue_status2.getVisibility() == View.INVISIBLE) {
						barbecue_status2.setVisibility(View.VISIBLE);
					}
					barbecue_status2.setCompoundDrawables(drawable, null, null,
							null);
					if (status == 1) {
						barbecue_status2.setText(getResources().getString(
								R.string.single_barbecue_type, "", degree)
								.replace("\n", ""));
					} else {
						barbecue_status2.setText(R.string.temperature);
					}
					break;

				case 3:
					if (target_temperature_3.getVisibility() == View.INVISIBLE) {
						target_temperature_3.setVisibility(View.VISIBLE);
					}
					if (target3.getVisibility() == View.INVISIBLE) {
						target3.setVisibility(View.VISIBLE);
					}

					target3.setText(builder);

					target_temperature_3
							.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
									.sin(rotation * Math.PI / 180)));
					target_temperature_3
							.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
									.cos(rotation * Math.PI / 180)));
					target_temperature_3.setRotation(rotation);

					if (barbecue_status3.getVisibility() == View.INVISIBLE) {
						barbecue_status3.setVisibility(View.VISIBLE);
					}
					barbecue_status3.setCompoundDrawables(drawable, null, null,
							null);
					if (status == 1) {
						barbecue_status3.setText(getResources().getString(
								R.string.single_barbecue_type, "", degree)
								.replace("\n", ""));
					} else {
						barbecue_status3.setText(R.string.temperature);
					}
					break;

				case 4:
					if (target_temperature_4.getVisibility() == View.INVISIBLE) {
						target_temperature_4.setVisibility(View.VISIBLE);
					}
					if (target4.getVisibility() == View.INVISIBLE) {
						target4.setVisibility(View.VISIBLE);
					}

					target4.setText(builder);

					target_temperature_4
							.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
									.sin(rotation * Math.PI / 180)));
					target_temperature_4
							.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
									.cos(rotation * Math.PI / 180)));
					target_temperature_4.setRotation(rotation);

					if (barbecue_status4.getVisibility() == View.INVISIBLE) {
						barbecue_status4.setVisibility(View.VISIBLE);
					}
					barbecue_status4.setCompoundDrawables(drawable, null, null,
							null);
					if (status == 1) {
						barbecue_status4.setText(getResources().getString(
								R.string.single_barbecue_type, "", degree)
								.replace("\n", ""));
					} else {
						barbecue_status4.setText(R.string.temperature);
					}
					break;
			}
		} else {
			switch (position) {
				case 1:
					if (barbecue_status1.getVisibility() == View.INVISIBLE) {
						barbecue_status1.setVisibility(View.VISIBLE);
					}
					barbecue_status1.setCompoundDrawables(drawable, null, null,
							null);
					barbecue_status1.setText(R.string.timer);
					if (target1.getVisibility() == View.VISIBLE) {
						target1.setVisibility(View.INVISIBLE);
					}
					break;
				case 2:
					if (barbecue_status2.getVisibility() == View.INVISIBLE) {
						barbecue_status2.setVisibility(View.VISIBLE);
					}
					barbecue_status2.setCompoundDrawables(drawable, null, null,
							null);
					barbecue_status2.setText(R.string.timer);
					if (target2.getVisibility() == View.VISIBLE) {
						target2.setVisibility(View.INVISIBLE);
					}
					break;
				case 3:
					if (barbecue_status3.getVisibility() == View.INVISIBLE) {
						barbecue_status3.setVisibility(View.VISIBLE);
					}
					barbecue_status3.setCompoundDrawables(drawable, null, null,
							null);
					barbecue_status3.setText(R.string.timer);
					if (target3.getVisibility() == View.VISIBLE) {
						target3.setVisibility(View.INVISIBLE);
					}
					break;
				case 4:
					if (barbecue_status4.getVisibility() == View.INVISIBLE) {
						barbecue_status4.setVisibility(View.VISIBLE);
					}
					barbecue_status4.setCompoundDrawables(drawable, null, null,
							null);
					barbecue_status4.setText(R.string.timer);
					if (target4.getVisibility() == View.VISIBLE) {
						target4.setVisibility(View.INVISIBLE);
					}
					break;
			}

		}



	}


	private void showTargetTemperature() {
		Iterator iter = BleConnectUtils.connectMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			int key = (int) entry.getKey();
			BleGatt bleGatt = (BleGatt)entry.getValue();
			showWayStatus(key);
			switch (key) {
				case 1:
					if(BleConnectUtils.oneStatus){
						if(bleGatt.startFlag) {
							if (left_time_tv_1.getVisibility() == View.INVISIBLE) {
								left_time_tv_1.setVisibility(View.VISIBLE);
							}
							if(bleGatt.calculateUtils.countDownFinish==1) {
								left_time_tv_1.setText(DataUtils.getStringTime(bleGatt.calculateUtils.lastLeftTime));
							}else{
								left_time_tv_1.setText(R.string.cooking);
							}
						}
					}
					break;

				case 2:
					if(BleConnectUtils.twoStatus){
						if(bleGatt.startFlag) {
							if (left_time_tv_2.getVisibility() == View.INVISIBLE) {
								left_time_tv_2.setVisibility(View.VISIBLE);
							}
							if(bleGatt.calculateUtils.countDownFinish==1) {
								left_time_tv_2.setText(DataUtils.getStringTime(bleGatt.calculateUtils.lastLeftTime));
							}else{
								left_time_tv_2.setText(R.string.cooking);
							}
						}
					}
					break;
				case 3:
					if(BleConnectUtils.threeStatus){
						if(bleGatt.startFlag) {
							if (left_time_tv_3.getVisibility() == View.INVISIBLE) {
								left_time_tv_3.setVisibility(View.VISIBLE);
							}
							if(bleGatt.calculateUtils.countDownFinish==1) {
								left_time_tv_3.setText(DataUtils.getStringTime(bleGatt.calculateUtils.lastLeftTime));
							}else{
								left_time_tv_3.setText(R.string.cooking);
							}
						}
					}
					break;
				case 4:
					if(BleConnectUtils.fourStatus){
						if(bleGatt.startFlag) {
							if (left_time_tv_4.getVisibility() == View.INVISIBLE) {
								left_time_tv_4.setVisibility(View.VISIBLE);
							}
							if(bleGatt.calculateUtils.countDownFinish==1) {
								left_time_tv_4.setText(DataUtils.getStringTime(bleGatt.calculateUtils.lastLeftTime));
							}else{
								left_time_tv_4.setText(R.string.cooking);
							}
						}
					}
					break;
			}
		}
		iter = BleConnectUtils.uiMap.entrySet().iterator();
		BarbecueParamer barbecueParamer = null;
		int targetTemperature = 0;
		String unit = (DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
				: DataUtils.fahrenhite;
		Drawable drawable = null;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			int key = (int) entry.getKey();
			barbecueParamer = (BarbecueParamer) entry.getValue();
			switch (key) {
				case 1:
					if(BleConnectUtils.oneStatus){
						temperatureHand(BleConnectUtils.current_temperature1,
								BleConnectUtils.grill_temperature1, 1);
					}
					break;

				case 2:
					if(BleConnectUtils.twoStatus){
						temperatureHand(BleConnectUtils.current_temperature2,
								BleConnectUtils.grill_temperature2, 2);
					}
					break;
				case 3:
					if(BleConnectUtils.threeStatus){
						temperatureHand(BleConnectUtils.current_temperature3,
								BleConnectUtils.grill_temperature3, 3);
					}
					break;
				case 4:
					if(BleConnectUtils.fourStatus){
						temperatureHand(BleConnectUtils.current_temperature4,
								BleConnectUtils.grill_temperature4, 4);
					}
					break;
			}
			int status = barbecueParamer.getStatus();
			int type = barbecueParamer.getType();
			String degree = barbecueParamer.getDegree();
			drawable = DataUtils.updateDrawable(status, type, context,
					pagerStatus);
			if (status > 0 && status != 2) {
				targetTemperature = barbecueParamer.getTemperature();
				float rotation = 210 + targetTemperature;
				if (rotation > 360) {
					rotation -= 360;
				}
				switch (DataUtils.temperatureUnit) {
					case 1:
						targetTemperature = DataUtils
								.centigrade2Fahrenhite(targetTemperature);
						break;
					default:
						break;
				}
				String text = getResources().getString(
						R.string.single_barbecue_target_temperature,
						targetTemperature+"", unit).replaceAll("\n", " ");
				int size = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
				int pushSize = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
				String splitValue = " ";
				int defaultColor = getResources().getColor(R.color.barbecue_status_text_color);
				int pushColor = Color.YELLOW;
				SpannableStringBuilder builder = DataUtils.setTextStyle(text,splitValue,size,pushSize,defaultColor,pushColor);
				switch (key) {
					case 1:
						if (target_temperature_1.getVisibility() == View.INVISIBLE) {
							target_temperature_1.setVisibility(View.VISIBLE);
						}
						if (target1.getVisibility() == View.INVISIBLE) {
							target1.setVisibility(View.VISIBLE);
						}
						target1.setText(builder);
						Log.i(TAG, "showTargetTemperature " + builder.toString()  );
						target_temperature_1
								.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
										.sin(rotation * Math.PI / 180)));
						target_temperature_1
								.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
										.cos(rotation * Math.PI / 180)));
						target_temperature_1.setRotation(rotation);
						if (barbecue_status1.getVisibility() == View.INVISIBLE) {
							barbecue_status1.setVisibility(View.VISIBLE);
						}
						barbecue_status1.setCompoundDrawables(drawable, null, null,
								null);
						if (status == 1) {
							barbecue_status1.setText(getResources().getString(
									R.string.single_barbecue_type, "", degree)
									.replace("\n", ""));
						} else {
							barbecue_status1.setText(R.string.temperature);
						}

						break;

					case 2:
						if (target_temperature_2.getVisibility() == View.INVISIBLE) {
							target_temperature_2.setVisibility(View.VISIBLE);
						}
						if (target2.getVisibility() == View.INVISIBLE) {
							target2.setVisibility(View.VISIBLE);
						}

						target2.setText(builder);

						target_temperature_2
								.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
										.sin(rotation * Math.PI / 180)));
						target_temperature_2
								.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
										.cos(rotation * Math.PI / 180)));
						target_temperature_2.setRotation(rotation);

						if (barbecue_status2.getVisibility() == View.INVISIBLE) {
							barbecue_status2.setVisibility(View.VISIBLE);
						}
						barbecue_status2.setCompoundDrawables(drawable, null, null,
								null);
						if (status == 1) {
							barbecue_status2.setText(getResources().getString(
									R.string.single_barbecue_type, "", degree)
									.replace("\n", ""));
						} else {
							barbecue_status2.setText(R.string.temperature);
						}
						break;

					case 3:
						if (target_temperature_3.getVisibility() == View.INVISIBLE) {
							target_temperature_3.setVisibility(View.VISIBLE);
						}
						if (target3.getVisibility() == View.INVISIBLE) {
							target3.setVisibility(View.VISIBLE);
						}

						target3.setText(builder);

						target_temperature_3
								.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
										.sin(rotation * Math.PI / 180)));
						target_temperature_3
								.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
										.cos(rotation * Math.PI / 180)));
						target_temperature_3.setRotation(rotation);

						if (barbecue_status3.getVisibility() == View.INVISIBLE) {
							barbecue_status3.setVisibility(View.VISIBLE);
						}
						barbecue_status3.setCompoundDrawables(drawable, null, null,
								null);
						if (status == 1) {
							barbecue_status3.setText(getResources().getString(
									R.string.single_barbecue_type, "", degree)
									.replace("\n", ""));
						} else {
							barbecue_status3.setText(R.string.temperature);
						}
						break;

					case 4:
						if (target_temperature_4.getVisibility() == View.INVISIBLE) {
							target_temperature_4.setVisibility(View.VISIBLE);
						}
						if (target4.getVisibility() == View.INVISIBLE) {
							target4.setVisibility(View.VISIBLE);
						}

						target4.setText(builder);

						target_temperature_4
								.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
										.sin(rotation * Math.PI / 180)));
						target_temperature_4
								.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
										.cos(rotation * Math.PI / 180)));
						target_temperature_4.setRotation(rotation);

						if (barbecue_status4.getVisibility() == View.INVISIBLE) {
							barbecue_status4.setVisibility(View.VISIBLE);
						}
						barbecue_status4.setCompoundDrawables(drawable, null, null,
								null);
						if (status == 1) {
							barbecue_status4.setText(getResources().getString(
									R.string.single_barbecue_type, "", degree)
									.replace("\n", ""));
						} else {
							barbecue_status4.setText(R.string.temperature);
						}
						break;
				}
			} else {
				switch (key) {
					case 1:
						if (barbecue_status1.getVisibility() == View.INVISIBLE) {
							barbecue_status1.setVisibility(View.VISIBLE);
						}
						barbecue_status1.setCompoundDrawables(drawable, null, null,
								null);
						barbecue_status1.setText(R.string.timer);
						if (target1.getVisibility() == View.VISIBLE) {
							target1.setVisibility(View.INVISIBLE);
						}
						break;
					case 2:
						if (barbecue_status2.getVisibility() == View.INVISIBLE) {
							barbecue_status2.setVisibility(View.VISIBLE);
						}
						barbecue_status2.setCompoundDrawables(drawable, null, null,
								null);
						barbecue_status2.setText(R.string.timer);
						if (target2.getVisibility() == View.VISIBLE) {
							target2.setVisibility(View.INVISIBLE);
						}
						break;
					case 3:
						if (barbecue_status3.getVisibility() == View.INVISIBLE) {
							barbecue_status3.setVisibility(View.VISIBLE);
						}
						barbecue_status3.setCompoundDrawables(drawable, null, null,
								null);
						barbecue_status3.setText(R.string.timer);
						if (target3.getVisibility() == View.VISIBLE) {
							target3.setVisibility(View.INVISIBLE);
						}
						break;
					case 4:
						if (barbecue_status4.getVisibility() == View.INVISIBLE) {
							barbecue_status4.setVisibility(View.VISIBLE);
						}
						barbecue_status4.setCompoundDrawables(drawable, null, null,
								null);
						barbecue_status4.setText(R.string.timer);
						if (target4.getVisibility() == View.VISIBLE) {
							target4.setVisibility(View.INVISIBLE);
						}
						break;
				}

			}

		}
	}

	// 收到数据时的温度指针显示判断
	private void temperatureHand(float currentTemperature,
								 float grillTemperature, int position) {
		float displayCT = currentTemperature;
		if(currentTemperature<-40f){
			currentTemperature = -40;
		}
		float rotation = 210 + currentTemperature;
		if (rotation >= 360) {
			rotation -= 360;
		}
		float grillRotation = 210 + grillTemperature;
		if (grillRotation >= 360) {
			grillRotation -= 360;
		}
		String temperatureUnit = ((DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
				: DataUtils.fahrenhite);
		String text = getResources().getString(
				R.string.barbecue_temperature, DataUtils.displayTmeperature(grillTemperature)+ "",
				temperatureUnit);
		int size = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
		int pushSize = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
		String splitValue = " ";
		int defaultColor = getResources().getColor(R.color.barbecue_status_text_color);
		int pushColor = Color.WHITE;
		SpannableStringBuilder builder = DataUtils.setTextStyle(text,splitValue,size,pushSize,defaultColor,pushColor);
		switch (position) {
			case 1:
				if (current_temperature_tv_1.getVisibility() == View.INVISIBLE) {
					current_temperature_tv_1.setVisibility(View.VISIBLE);
				}
				if (grill_temperature_1.getVisibility() == View.INVISIBLE) {
					grill_temperature_1.setVisibility(View.VISIBLE);
				}
				if (current_temperature_1.getVisibility() == View.INVISIBLE) {
					current_temperature_1.setVisibility(View.VISIBLE);
				}
				if (grill1.getVisibility() == View.INVISIBLE) {
					grill1.setVisibility(View.VISIBLE);
				}
				current_temperature_tv_1.setText( DataUtils.displayTmeperature(displayCT)+ temperatureUnit);
				if(currentTemperature>=DataUtils.highTemperature){
					setFlickerAnimation(current_temperature_tv_1);
				}else{
					clearAnimation(current_temperature_tv_1);
				}
				current_temperature_1
						.setTranslationX((float) ((bitmapWidth / 2 - 59 * percentage) * (float) Math
								.sin(rotation * Math.PI / 180)));
				current_temperature_1
						.setTranslationY((float) ((-bitmapHeigh / 2 + 59 * percentage) * (float) Math
								.cos(rotation * Math.PI / 180)));
				current_temperature_1.setRotation(rotation);
				grill_temperature_1
						.setTranslationX((float) ((bitmapWidth / 2 - 59 * percentage) * (float) Math
								.sin(grillRotation * Math.PI / 180)));
				grill_temperature_1
						.setTranslationY((float) ((-bitmapHeigh / 2 + 59 * percentage) * (float) Math
								.cos(grillRotation * Math.PI / 180)));
				grill_temperature_1.setRotation(grillRotation);
				grill1.setText(builder);
				if(grillTemperature>DataUtils.highGrillTemperature){
					setFlickerAnimation(grill1);
				}else{
					clearAnimation(grill1);
				}
				break;
			case 2:
				if (current_temperature_tv_2.getVisibility() == View.INVISIBLE) {
					current_temperature_tv_2.setVisibility(View.VISIBLE);
				}
				if (grill_temperature_2.getVisibility() == View.INVISIBLE) {
					grill_temperature_2.setVisibility(View.VISIBLE);
				}
				if (current_temperature_2.getVisibility() == View.INVISIBLE) {
					current_temperature_2.setVisibility(View.VISIBLE);
				}
				if (grill2.getVisibility() == View.INVISIBLE) {
					grill2.setVisibility(View.VISIBLE);
				}
				current_temperature_tv_2.setText(DataUtils.displayTmeperature(displayCT)+ temperatureUnit);
				if(currentTemperature>=DataUtils.highTemperature){
					setFlickerAnimation(current_temperature_tv_2);
				}else{
					clearAnimation(current_temperature_tv_2);
				}
				current_temperature_2
						.setTranslationX((float) ((bitmapWidth / 2 - 62 * percentage) * (float) Math
								.sin(rotation * Math.PI / 180)));
				current_temperature_2
						.setTranslationY((float) ((-bitmapHeigh / 2 + 62 * percentage) * (float) Math
								.cos(rotation * Math.PI / 180)));
				current_temperature_2.setRotation(rotation);
				grill_temperature_2
						.setTranslationX((float) ((bitmapWidth / 2 - 62 * percentage) * (float) Math
								.sin(grillRotation * Math.PI / 180)));
				grill_temperature_2
						.setTranslationY((float) ((-bitmapHeigh / 2 + 62 * percentage) * (float) Math
								.cos(grillRotation * Math.PI / 180)));
				grill_temperature_2.setRotation(grillRotation);
				grill2.setText(builder);
				if(grillTemperature>DataUtils.highGrillTemperature){
					setFlickerAnimation(grill2);
				}else{
					clearAnimation(grill2);
				}
				break;
			case 3:
				if (current_temperature_tv_3.getVisibility() == View.INVISIBLE) {
					current_temperature_tv_3.setVisibility(View.VISIBLE);
				}
				if (grill_temperature_3.getVisibility() == View.INVISIBLE) {
					grill_temperature_3.setVisibility(View.VISIBLE);
				}
				if (current_temperature_3.getVisibility() == View.INVISIBLE) {
					current_temperature_3.setVisibility(View.VISIBLE);
				}
				if (grill3.getVisibility() == View.INVISIBLE) {
					grill3.setVisibility(View.VISIBLE);
				}
				current_temperature_tv_3.setText(DataUtils.displayTmeperature(displayCT)+ temperatureUnit);
				if(currentTemperature>=DataUtils.highTemperature){
					setFlickerAnimation(current_temperature_tv_3);
				}else{
					clearAnimation(current_temperature_tv_3);
				}
				current_temperature_3
						.setTranslationX((float) ((bitmapWidth / 2 - 62 * percentage) * (float) Math
								.sin(rotation * Math.PI / 180)));
				current_temperature_3
						.setTranslationY((float) ((-bitmapHeigh / 2 + 62 * percentage) * (float) Math
								.cos(rotation * Math.PI / 180)));
				current_temperature_3.setRotation(rotation);
				grill_temperature_3
						.setTranslationX((float) ((bitmapWidth / 2 - 62 * percentage) * (float) Math
								.sin(grillRotation * Math.PI / 180)));
				grill_temperature_3
						.setTranslationY((float) ((-bitmapHeigh / 2 + 62 * percentage) * (float) Math
								.cos(grillRotation * Math.PI / 180)));
				grill_temperature_3.setRotation(grillRotation);
				grill3.setText(builder);
				if(grillTemperature>DataUtils.highGrillTemperature){
					setFlickerAnimation(grill3);
				}else{
					clearAnimation(grill3);
				}
				break;
			case 4:
				if (current_temperature_tv_4.getVisibility() == View.INVISIBLE) {
					current_temperature_tv_4.setVisibility(View.VISIBLE);
				}
				if (grill_temperature_4.getVisibility() == View.INVISIBLE) {
					grill_temperature_4.setVisibility(View.VISIBLE);
				}
				if (current_temperature_4.getVisibility() == View.INVISIBLE) {
					current_temperature_4.setVisibility(View.VISIBLE);
				}
				if (grill4.getVisibility() == View.INVISIBLE) {
					grill4.setVisibility(View.VISIBLE);
				}
				current_temperature_tv_4.setText(DataUtils.displayTmeperature(displayCT)+ temperatureUnit);
				if(currentTemperature>=DataUtils.highTemperature){
					setFlickerAnimation(current_temperature_tv_4);
				}else{
					clearAnimation(current_temperature_tv_4);
				}
				current_temperature_4
						.setTranslationX((float) ((bitmapWidth / 2 - 62 * percentage) * (float) Math
								.sin(rotation * Math.PI / 180)));
				current_temperature_4
						.setTranslationY((float) ((-bitmapHeigh / 2 + 62 * percentage) * (float) Math
								.cos(rotation * Math.PI / 180)));
				current_temperature_4.setRotation(rotation);
				grill_temperature_4
						.setTranslationX((float) ((bitmapWidth / 2 - 62 * percentage) * (float) Math
								.sin(grillRotation * Math.PI / 180)));
				grill_temperature_4
						.setTranslationY((float) ((-bitmapHeigh / 2 + 62 * percentage) * (float) Math
								.cos(grillRotation * Math.PI / 180)));
				grill_temperature_4.setRotation(grillRotation);
				grill4.setText(builder);
				if(grillTemperature>DataUtils.highGrillTemperature){
					setFlickerAnimation(grill4);
				}else{
					clearAnimation(grill4);
				}
				break;
		}
	}

	private void initTextViewValue(){
		item_logo.setText(R.string.all_probes);
		if (BleConnectUtils.connectList.size() ==0 )
			setting.setClickable(true);
		else
			setting.setClickable(false);
	}

	private void init() {
		ring1 = (FrameLayout)findViewById(R.id.ring1);
		ring2 = (FrameLayout)findViewById(R.id.ring2);
		ring3 = (FrameLayout)findViewById(R.id.ring3);
		ring4 = (FrameLayout)findViewById(R.id.ring4);
		ring1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(BleConnectUtils.oneStatus){
					Intent intent = new Intent("ringAction");
					intent.putExtra("position", 1);
					sendBroadcast(intent);
					onBackPressed();
				}
			}
		});
		ring2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(BleConnectUtils.twoStatus){
					Intent intent = new Intent("ringAction");
					intent.putExtra("position", 2);
					sendBroadcast(intent);
					onBackPressed();
				}
			}
		});
		ring3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(BleConnectUtils.threeStatus){
					Intent intent = new Intent("ringAction");
					intent.putExtra("position", 3);
					sendBroadcast(intent);
					onBackPressed();
				}
			}
		});
		ring4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(BleConnectUtils.fourStatus){
					Intent intent = new Intent("ringAction");
					intent.putExtra("position", 4);
					sendBroadcast(intent);
					onBackPressed();
				}
			}
		});
		setting = (ImageView) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AllProbesActivity.this,
						SettingActivity.class);
				ArrayList<Integer> list = new ArrayList<Integer>();
				if (BleConnectUtils.oneStatus) {
					list.add(1);
				}
				if (BleConnectUtils.twoStatus) {
					list.add(2);
				}
				if (BleConnectUtils.threeStatus) {
					list.add(3);
				}
				if (BleConnectUtils.fourStatus) {
					list.add(4);
				}
				intent.putIntegerArrayListExtra("connectStatus", list);
				startActivity(intent);
			}
		});
		grill_temperature_1 = (ImageView) findViewById(R.id.grill_temperature_1);
		target_temperature_1 = (ImageView) findViewById(R.id.target_temperature_1);
		current_temperature_1 = (ImageView) findViewById(R.id.current_temperature_1);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.small_target_temperature);
		target_temperature_1.setImageBitmap(adjustPhotoRotation(bitmap, 180));

		left_time_tv_1 = (TextView) findViewById(R.id.left_time_tv_1);
		left_time_tv_2 = (TextView) findViewById(R.id.left_time_tv_2);
		left_time_tv_3 = (TextView) findViewById(R.id.left_time_tv_3);
		left_time_tv_4 = (TextView) findViewById(R.id.left_time_tv_4);
		current_temperature_tv_1 = (TextView) findViewById(R.id.current_temperature_tv_1);
		current_temperature_tv_2 = (TextView) findViewById(R.id.current_temperature_tv_2);
		current_temperature_tv_3 = (TextView) findViewById(R.id.current_temperature_tv_3);
		current_temperature_tv_4 = (TextView) findViewById(R.id.current_temperature_tv_4);
		name_label_1 = (TextView) findViewById(R.id.name_label_1);
		name_label_2 = (TextView) findViewById(R.id.name_label_2);
		name_label_3 = (TextView) findViewById(R.id.name_label_3);
		name_label_4 = (TextView) findViewById(R.id.name_label_4);
		one_way = (ImageView) findViewById(R.id.one_way);
		target1 = (TextView) findViewById(R.id.target1);
		target2 = (TextView) findViewById(R.id.target2);
		target3 = (TextView) findViewById(R.id.target3);
		target4 = (TextView) findViewById(R.id.target4);
		barbecue_status1 = (TextView) findViewById(R.id.barbecue_status1);
		barbecue_status2 = (TextView) findViewById(R.id.barbecue_status2);
		barbecue_status3 = (TextView) findViewById(R.id.barbecue_status3);
		barbecue_status4 = (TextView) findViewById(R.id.barbecue_status4);
		two_way = (ImageView) findViewById(R.id.two_way);
		battery1 = (ImageView) findViewById(R.id.battery1);
		battery2 = (ImageView) findViewById(R.id.battery2);
		three_way = (ImageView) findViewById(R.id.three_way);
		four_way = (ImageView) findViewById(R.id.four_way);
		battery3 = (ImageView) findViewById(R.id.battery3);
		battery4 = (ImageView) findViewById(R.id.battery4);
		grill_temperature_2 = (ImageView) findViewById(R.id.grill_temperature_2);
		target_temperature_2 = (ImageView) findViewById(R.id.target_temperature_2);
		target_temperature_2.setImageBitmap(adjustPhotoRotation(bitmap, 180));
		current_temperature_2 = (ImageView) findViewById(R.id.current_temperature_2);
		grill_temperature_3 = (ImageView) findViewById(R.id.grill_temperature_3);
		target_temperature_3 = (ImageView) findViewById(R.id.target_temperature_3);
		target_temperature_3.setImageBitmap(adjustPhotoRotation(bitmap, 180));
		current_temperature_3 = (ImageView) findViewById(R.id.current_temperature_3);
		grill_temperature_4 = (ImageView) findViewById(R.id.grill_temperature_4);
		target_temperature_4 = (ImageView) findViewById(R.id.target_temperature_4);
		target_temperature_4.setImageBitmap(adjustPhotoRotation(bitmap, 180));
		current_temperature_4 = (ImageView) findViewById(R.id.current_temperature_4);
		grill1 = (TextView) findViewById(R.id.grill1);
		grill2 = (TextView) findViewById(R.id.grill2);
		grill3 = (TextView) findViewById(R.id.grill3);
		grill4 = (TextView) findViewById(R.id.grill4);
		item_logo = (TextView) findViewById(R.id.item_logo);
		status = (ImageView) findViewById(R.id.status);
		status.setVisibility(View.GONE);
		title_bar_back = (ImageView) findViewById(R.id.title_bar_back);
		title_bar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	public Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		try {
			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);
			return bm1;
		} catch (OutOfMemoryError ex) {
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		unregisterReceiver(timerUIReceiver);
		unregisterReceiver(alarmReceiver);
		unregisterReceiver(bleDataReceiver);
		unregisterReceiver(temperatureUpdateUIReceiver);
		unregisterReceiver(disconnectAlarmReceiver);
		unregisterReceiver(bleWorkStatus);
		unregisterReceiver(highAlarmReceiver);
		unregisterReceiver(highGrillAlarmReceiver);
		unregisterReceiver(autoConnectFinishReceiver);
		unregisterReceiver(batteryLowReceiver);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void bleDisconnectUI(int removePosition) {
		switch (removePosition) {
			case 1:
				left_time_tv_1.setText("");
				if (left_time_tv_1.getVisibility() == View.VISIBLE) {
					left_time_tv_1.setVisibility(View.INVISIBLE);
				}
				if (battery1.getVisibility() == View.VISIBLE) {
					battery1.setVisibility(View.INVISIBLE);
				}
				current_temperature_tv_1.setText("");
				if (current_temperature_tv_1.getVisibility() == View.VISIBLE) {
					current_temperature_tv_1.setVisibility(View.INVISIBLE);
				}
				one_way.setImageResource(R.drawable.one_small_disconnect);
				grill_temperature_1.setVisibility(View.INVISIBLE);
				target_temperature_1.setVisibility(View.INVISIBLE);
				current_temperature_1.setVisibility(View.INVISIBLE);
				barbecue_status1.setVisibility(View.INVISIBLE);
				target1.setVisibility(View.INVISIBLE);
				grill1.setVisibility(View.INVISIBLE);
				clearAnimation(grill1);
				break;
			case 2:
				left_time_tv_2.setText("");
				if (left_time_tv_2.getVisibility() == View.VISIBLE) {
					left_time_tv_2.setVisibility(View.INVISIBLE);
				}
				if (battery2.getVisibility() == View.VISIBLE) {
					battery2.setVisibility(View.INVISIBLE);
				}
				current_temperature_tv_2.setText("");
				if (current_temperature_tv_2.getVisibility() == View.VISIBLE) {
					current_temperature_tv_2.setVisibility(View.INVISIBLE);
				}
				two_way.setImageResource(R.drawable.two_small_disconnect);
				grill_temperature_2.setVisibility(View.INVISIBLE);
				target_temperature_2.setVisibility(View.INVISIBLE);
				current_temperature_2.setVisibility(View.INVISIBLE);
				barbecue_status2.setVisibility(View.INVISIBLE);
				target2.setVisibility(View.INVISIBLE);
				grill2.setVisibility(View.INVISIBLE);
				clearAnimation(grill2);
				break;
			case 3:
				left_time_tv_3.setText("");
				if (left_time_tv_3.getVisibility() == View.VISIBLE) {
					left_time_tv_3.setVisibility(View.INVISIBLE);
				}
				if (battery3.getVisibility() == View.VISIBLE) {
					battery3.setVisibility(View.INVISIBLE);
				}
				current_temperature_tv_3.setText("");
				if (current_temperature_tv_3.getVisibility() == View.VISIBLE) {
					current_temperature_tv_3.setVisibility(View.INVISIBLE);
				}
				three_way.setImageResource(R.drawable.three_small_disconnect);
				grill_temperature_3.setVisibility(View.INVISIBLE);
				target_temperature_3.setVisibility(View.INVISIBLE);
				current_temperature_3.setVisibility(View.INVISIBLE);
				barbecue_status3.setVisibility(View.INVISIBLE);
				target3.setVisibility(View.INVISIBLE);
				grill3.setVisibility(View.INVISIBLE);
				clearAnimation(grill3);
				break;
			case 4:
				left_time_tv_4.setText("");
				if (left_time_tv_4.getVisibility() == View.VISIBLE) {
					left_time_tv_4.setVisibility(View.INVISIBLE);
				}
				if (battery4.getVisibility() == View.VISIBLE) {
					battery4.setVisibility(View.INVISIBLE);
				}
				current_temperature_tv_4.setText("");
				if (current_temperature_tv_4.getVisibility() == View.VISIBLE) {
					current_temperature_tv_4.setVisibility(View.INVISIBLE);
				}
				four_way.setImageResource(R.drawable.four_small_disconnect);
				grill_temperature_4.setVisibility(View.INVISIBLE);
				target_temperature_4.setVisibility(View.INVISIBLE);
				current_temperature_4.setVisibility(View.INVISIBLE);
				barbecue_status4.setVisibility(View.INVISIBLE);
				target4.setVisibility(View.INVISIBLE);
				grill4.setVisibility(View.INVISIBLE);
				clearAnimation(grill4);
				break;

		}

	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BlemeshIntent.ACTION_GATT_DISCONNECTED)||action.equals(BlemeshIntent.ACTION_GATT_DISCONNECTED_ERROR)) {
				Bundle bundle = intent.getExtras();
				int removePosition = bundle.getInt("removePosition");
				bleDisconnectUI(removePosition);
				switch (removePosition){
					case 1:
						clearAnimation(battery1);
						break;
					case 2:
						clearAnimation(battery2);
						break;
					case 3:
						clearAnimation(battery3);
						break;
					case 4:
						clearAnimation(battery4);
						break;
				}
			}else if(action.equals("connectedAllProbesPager")){
				Bundle bundle = intent.getExtras();
				int connectPosition = bundle.getInt("position");
				showWayStatus(connectPosition);
				showTargetTemperature(connectPosition);
			}
		}
	};

	private final BroadcastReceiver batteryLowReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int position = intent.getIntExtra("position", 0);
			batteryLowChange(position);
		}
	};

	private void displayBatteryLow(){
		if(BleConnectUtils.batteryLow1==1){
			battery1.setVisibility(View.VISIBLE);
			setFlickerAnimation(battery1);
		}
		if(BleConnectUtils.batteryLow2==1){
			battery2.setVisibility(View.VISIBLE);
			setFlickerAnimation(battery2);
		}
		if(BleConnectUtils.batteryLow3==1){
			battery3.setVisibility(View.VISIBLE);
			setFlickerAnimation(battery3);
		}
		if(BleConnectUtils.batteryLow4==1){
			battery4.setVisibility(View.VISIBLE);
			setFlickerAnimation(battery4);
		}
	}

	// 收到低电数据
	private void batteryLowChange(int position) {
		switch (position){
			case 1:
				if(BleConnectUtils.batteryLow1==1) {
					battery1.setVisibility(View.VISIBLE);
					setFlickerAnimation(battery1);
				}else{
					clearAnimation(battery1);
				}
				break;
			case 2:
				if(BleConnectUtils.batteryLow2==1) {
					battery2.setVisibility(View.VISIBLE);
					setFlickerAnimation(battery2);
				}else{
					clearAnimation(battery2);
				}
				break;
			case 3:
				if(BleConnectUtils.batteryLow3==1) {
					battery3.setVisibility(View.VISIBLE);
					setFlickerAnimation(battery3);
				}else{
					clearAnimation(battery3);
				}
				break;
			case 4:
				if(BleConnectUtils.batteryLow4==1) {
					battery4.setVisibility(View.VISIBLE);
					setFlickerAnimation(battery4);
				}else{
					clearAnimation(battery4);
				}
				break;
		}
	}


	private BroadcastReceiver autoConnectFinishReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(disconnectAlarmDialog!=null && disconnectAlarmDialog.isShowing()){
				switch (DataUtils.alarmType) {
					case 0:
						if (DataUtils.mp != null) {
							DataUtils.mp.stop();
							DataUtils.mp = null;
						}
						break;
					case 1:
						DataUtils.vibrator.cancel();
						break;
					case 2:
						if (DataUtils.mp != null) {
							DataUtils.mp.stop();
							DataUtils.mp = null;
						}
						DataUtils.vibrator.cancel();
						break;
				}
				Iterator it = alarmChannel.keySet().iterator();
				while(it.hasNext()) {
					int position = (Integer)it.next();
					updateAlarmBackUI(position);
				}
				alarmChannel.clear();
				disconnectAlarmDialog.dismiss();
			}
		}
	};

	private final BroadcastReceiver timerUIReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			int position = bundle.getInt("position");
			switch (position) {
				case 1:
					if(BleConnectUtils.oneStatus){
						leftTime1 = bundle.getInt("leftTime");
						if(leftTime1<=0){
							Log.e("22222leftTime","======================"+leftTime1+",position=="+position);
						}
						if (left_time_tv_1.getVisibility() == View.INVISIBLE) {
							left_time_tv_1.setVisibility(View.VISIBLE);
						}
						left_time_tv_1.setText(DataUtils.getStringTime(leftTime1));
					}
					break;
				case 2:
					if(BleConnectUtils.twoStatus){
						leftTime2 = bundle.getInt("leftTime");
						if (left_time_tv_2.getVisibility() == View.INVISIBLE) {
							left_time_tv_2.setVisibility(View.VISIBLE);
						}
						left_time_tv_2.setText(DataUtils.getStringTime(leftTime2));
					}
					break;
				case 3:
					if(BleConnectUtils.threeStatus){
						leftTime3 = bundle.getInt("leftTime");
						if (left_time_tv_3.getVisibility() == View.INVISIBLE) {
							left_time_tv_3.setVisibility(View.VISIBLE);
						}
						left_time_tv_3.setText(DataUtils.getStringTime(leftTime3));
					}
					break;
				case 4:
					if(BleConnectUtils.fourStatus){
						leftTime4 = bundle.getInt("leftTime");
						if (left_time_tv_4.getVisibility() == View.INVISIBLE) {
							left_time_tv_4.setVisibility(View.VISIBLE);
						}
						left_time_tv_4.setText(DataUtils.getStringTime(leftTime4));
					}
					break;
			}
		}
	};

	public void updateAlarmUI(int position) {
		ImageView view = null;
		switch (position) {
			case 1:
				view = one_way;
				one_way.setImageResource(R.drawable.one_small_alarm);
				break;
			case 2:
				view = two_way;
				two_way.setImageResource(R.drawable.two_small_alarm);
				break;
			case 3:
				view = three_way;
				three_way.setImageResource(R.drawable.three_small_alarm);
				break;
			case 4:
				view = four_way;
				four_way.setImageResource(R.drawable.four_small_alarm);
				break;
		}
		setFlickerAnimation(view, position);
	}


	private void setFlickerAnimation(ImageView iv, int position) {
		switch (position) {
			case 1:
				animation1 = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				animation1.setDuration(500); // duration - half a second
				animation1.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				animation1.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				animation1.setRepeatMode(Animation.REVERSE); //
				iv.setAnimation(animation1);
				break;
			case 2:
				animation2 = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				animation2.setDuration(500); // duration - half a second
				animation2.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				animation2.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				animation2.setRepeatMode(Animation.REVERSE); //
				iv.setAnimation(animation2);
				break;
			case 3:
				animation3 = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				animation3.setDuration(500); // duration - half a second
				animation3.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				animation3.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				animation3.setRepeatMode(Animation.REVERSE); //
				iv.setAnimation(animation3);
				break;
			case 4:
				animation4 = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				animation4.setDuration(500); // duration - half a second
				animation4.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				animation4.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				animation4.setRepeatMode(Animation.REVERSE); //
				iv.setAnimation(animation4);
				break;

		}
	}


	private void setFlickerAnimation(View tv) {
		if(tv.getTag().equals("grill1")){
			if(grill1Animation==null){
				grill1Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				grill1Animation.setDuration(500); // duration - half a second
				grill1Animation.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				grill1Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				grill1Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(grill1Animation);
			}
		}else if(tv.getTag().equals("grill2")){
			if(grill2Animation==null){
				grill2Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				grill2Animation.setDuration(500); // duration - half a second
				grill2Animation.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				grill2Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				grill2Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(grill2Animation);
			}
		}else if(tv.getTag().equals("grill3")){
			if(grill3Animation==null){
				grill3Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				grill3Animation.setDuration(500); // duration - half a second
				grill3Animation.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				grill3Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				grill3Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(grill3Animation);
			}
		}else if(tv.getTag().equals("grill4")){
			if(grill4Animation==null){
				grill4Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				grill4Animation.setDuration(500); // duration - half a second
				grill4Animation.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				grill4Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				grill4Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(grill4Animation);
			}
		}else if(tv.getTag().equals("current1")){
			if(current1Animation==null){
				current1Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				current1Animation.setDuration(500); // duration - half a second
				current1Animation.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				current1Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				current1Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(current1Animation);
			}
		}else if(tv.getTag().equals("current2")){
			if(current2Animation==null){
				current2Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				current2Animation.setDuration(500); // duration - half a second
				current2Animation.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				current2Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				current2Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(current2Animation);
			}
		}else if(tv.getTag().equals("current3")){
			if(current3Animation==null){
				current3Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				current3Animation.setDuration(500); // duration - half a second
				current3Animation.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				current3Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				current3Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(current3Animation);
			}
		}else if(tv.getTag().equals("current4")){
			if(current4Animation==null){
				current4Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				// visible to invisible
				current4Animation.setDuration(500); // duration - half a second
				current4Animation.setInterpolator(new LinearInterpolator()); // do not
				// alter
				// animation
				// rate
				current4Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				// infinitely
				current4Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(current4Animation);
			}
		}else if(tv.getTag().equals("battery1")){
			if(batteryLow1Animation==null){
				batteryLow1Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				batteryLow1Animation.setDuration(500); // duration - half a second
				batteryLow1Animation.setInterpolator(new LinearInterpolator()); // do not
				batteryLow1Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				batteryLow1Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(batteryLow1Animation);
			}
		}else if(tv.getTag().equals("battery2")){
			if(batteryLow2Animation==null){
				batteryLow2Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				batteryLow2Animation.setDuration(500); // duration - half a second
				batteryLow2Animation.setInterpolator(new LinearInterpolator()); // do not
				batteryLow2Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				batteryLow2Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(batteryLow2Animation);
			}
		}else if(tv.getTag().equals("battery3")){
			if(batteryLow3Animation==null){
				batteryLow3Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				batteryLow3Animation.setDuration(500); // duration - half a second
				batteryLow3Animation.setInterpolator(new LinearInterpolator()); // do not
				batteryLow3Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				batteryLow3Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(batteryLow3Animation);
			}
		}else if(tv.getTag().equals("battery4")){
			if(batteryLow4Animation==null){
				batteryLow4Animation = new AlphaAnimation(1, 0); // Change alpha from fully
				batteryLow4Animation.setDuration(500); // duration - half a second
				batteryLow4Animation.setInterpolator(new LinearInterpolator()); // do not
				batteryLow4Animation.setRepeatCount(Animation.INFINITE); // Repeat animation
				batteryLow4Animation.setRepeatMode(Animation.REVERSE); //
				tv.setAnimation(batteryLow4Animation);
			}
		}

	}


	private void clearAnimation(View tv) {
		if(tv.getTag().equals("grill1")){
			if(grill1Animation!=null){
				tv.clearAnimation();
				grill1Animation = null;

			}
		}else if(tv.getTag().equals("grill2")){
			if(grill2Animation!=null){
				tv.clearAnimation();
				grill2Animation = null;
			}
		}if(tv.getTag().equals("grill3")){
			if(grill3Animation!=null){
				tv.clearAnimation();
				grill3Animation = null;
			}
		}if(tv.getTag().equals("grill4")){
			if(grill4Animation!=null){
				tv.clearAnimation();
				grill4Animation = null;
			}
		}else if(tv.getTag().equals("current1")){
			if(current1Animation!=null){
				tv.clearAnimation();
				current1Animation = null;
			}

		}else if(tv.getTag().equals("current2")){
			if(current2Animation!=null){
				tv.clearAnimation();
				current2Animation = null;
			}

		}else if(tv.getTag().equals("current3")){
			if(current3Animation!=null){
				tv.clearAnimation();
				current3Animation = null;
			}

		}else if(tv.getTag().equals("current4")){
			if(current4Animation!=null){
				tv.clearAnimation();
				current4Animation = null;
			}

		}else if(tv.getTag().equals("battery1")){
			if(batteryLow1Animation!=null){
				tv.clearAnimation();
				tv.setVisibility(View.GONE);
				batteryLow1Animation = null;
			}

		}else if(tv.getTag().equals("battery2")){
			if(batteryLow2Animation!=null){
				tv.clearAnimation();
				tv.setVisibility(View.GONE);
				batteryLow2Animation = null;
			}

		}else if(tv.getTag().equals("battery3")){
			if(batteryLow3Animation!=null){
				tv.clearAnimation();
				tv.setVisibility(View.GONE);
				batteryLow3Animation = null;
			}

		}else if(tv.getTag().equals("battery4")){
			if(batteryLow4Animation!=null){
				tv.clearAnimation();
				tv.setVisibility(View.GONE);
				batteryLow4Animation = null;
			}

		}

	}

	public void updateAlarmBackUI(int position) {
		ImageView view = null;
		switch (position) {
			case 1:
				view = one_way;
				if (BleConnectUtils.oneStatus)  {
					one_way.setImageResource(R.drawable.one_small_connect);
				} else {
					one_way.setImageResource(R.drawable.one_small_disconnect);
				}
				break;
			case 2:
				view = two_way;
				if (BleConnectUtils.twoStatus)  {
					two_way.setImageResource(R.drawable.two_small_connect);
				} else {
					two_way.setImageResource(R.drawable.two_small_disconnect);
				}
				break;
			case 3:
				view = three_way;
				if (BleConnectUtils.threeStatus) {
					three_way.setImageResource(R.drawable.three_small_connect);
				} else {
					three_way.setImageResource(R.drawable.three_small_disconnect);
				}
				break;
			case 4:
				view = four_way;
				if (BleConnectUtils.fourStatus) {
					four_way.setImageResource(R.drawable.four_small_connect);
				} else {
					four_way.setImageResource(R.drawable.four_small_disconnect);
				}
				break;
		}
		view.clearAnimation();

	}

	private final BroadcastReceiver bleDataReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			if (actionStr.equals("temperatureData")) {
				float currentTemperature = intent.getFloatExtra("current", 0);
				float grillTemperature = intent.getFloatExtra("grill", 0);
				int position = intent.getIntExtra("position", 0);
				String thePoeName = intent.getStringExtra("pon");

				BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
				switch(position){
					case 1:

						if (position == 1) {
							name_label_1.setText(thePoeName);
							left_time_tv_1.setVisibility(View.VISIBLE);
							left_time_tv_1.setText(R.string.cooking);
							//Log.i("TEST", " Channel " + DataUtils.selectedChannelNumber + "  The POE Name : "+thePoeName);
						}
						break;
					case 2:

						if (position == 2) {
							name_label_2.setText(thePoeName);
							left_time_tv_2.setVisibility(View.VISIBLE);
							left_time_tv_2.setText(R.string.cooking);
							//Log.i("TEST", " Channel " + DataUtils.selectedChannelNumber + " Name : "+thePoeName);
						}
						break;
					case 3:

						if (position == 3) {
							name_label_3.setText(thePoeName);
							left_time_tv_3.setVisibility(View.VISIBLE);
							left_time_tv_3.setText(R.string.cooking);
							//Log.i("TEST", " Channel " + DataUtils.selectedChannelNumber + " Thoe POE Name : " + thePoeName);
						}
						break;
					case 4:

						if (position == 4){
							name_label_4.setText(thePoeName);
							left_time_tv_4.setVisibility(View.VISIBLE);
							//Log.i("TEST", " Channel " + DataUtils.selectedChannelNumber + " Thoe POE Name : " + thePoeName);
							left_time_tv_4.setText(R.string.cooking);
						}



						break;
				}
				temperatureHand(currentTemperature, grillTemperature, position);
			}else if(actionStr.equals("leftTime")){
				int countDownFinish = intent.getIntExtra("countDownFinish", 0);
				int position = intent.getIntExtra("position", 0);
				int leftTime =  intent.getIntExtra("leftTime", 0);
				BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
				if(paramer!=null && countDownFinish==1) {
					switch (position) {
						case 1:
							if(BleConnectUtils.oneStatus){
								if (left_time_tv_1.getVisibility() == View.INVISIBLE) {
									left_time_tv_1.setVisibility(View.VISIBLE);
								}
								left_time_tv_1.setText(DataUtils.getStringTime(leftTime));
							}
							break;
						case 2:
							if(BleConnectUtils.twoStatus){
								if (left_time_tv_2.getVisibility() == View.INVISIBLE) {
									left_time_tv_2.setVisibility(View.VISIBLE);
								}
								left_time_tv_2.setText(DataUtils.getStringTime(leftTime));
							}
							break;
						case 3:
							if(BleConnectUtils.threeStatus){
								if (left_time_tv_3.getVisibility() == View.INVISIBLE) {
									left_time_tv_3.setVisibility(View.VISIBLE);
								}
								left_time_tv_3.setText(DataUtils.getStringTime(leftTime));
							}
							break;
						case 4:
							if(BleConnectUtils.fourStatus){
								if (left_time_tv_4.getVisibility() == View.INVISIBLE) {
									left_time_tv_4.setVisibility(View.VISIBLE);
								}
								left_time_tv_4.setText(DataUtils.getStringTime(leftTime));
							}
							break;
					}
				}
			}
		}
	};

	// 收到修改温度单位后的处理
	private void updateTemperatureUnit(int position) {
		String temperatureUnit = ((DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
				: DataUtils.fahrenhite);
		String text = getResources().getString(
				R.string.barbecue_temperature, DataUtils.displayTmeperature(grillTemperature) + "",
				temperatureUnit);
		int size = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
		int pushSize = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
		String splitValue = " ";
		int defaultColor = getResources().getColor(R.color.barbecue_status_text_color);
		int pushColor = Color.WHITE;
		SpannableStringBuilder builder = DataUtils.setTextStyle(text,splitValue,size,pushSize,defaultColor,pushColor);
		switch (position) {
			case 1:
				if (current_temperature_tv_1.getVisibility() == View.INVISIBLE) {
					current_temperature_tv_1.setVisibility(View.VISIBLE);
				}
				if (grill_temperature_1.getVisibility() == View.INVISIBLE) {
					grill_temperature_1.setVisibility(View.VISIBLE);
				}
				if (current_temperature_1.getVisibility() == View.INVISIBLE) {
					current_temperature_1.setVisibility(View.VISIBLE);
				}
				if (grill1.getVisibility() == View.INVISIBLE) {
					grill1.setVisibility(View.VISIBLE);
				}
				current_temperature_tv_1.setText(DataUtils.displayTmeperature(currentTemperature)+ temperatureUnit);
				if(currentTemperature>=DataUtils.highTemperature){
					setFlickerAnimation(current_temperature_tv_1);
				}else{
					clearAnimation(current_temperature_tv_1);
				}
				grill1.setText(builder);
				if(grillTemperature>DataUtils.highGrillTemperature){
					setFlickerAnimation(grill1);
				}else{
					clearAnimation(grill1);
				}
				break;
			case 2:
				if (current_temperature_tv_2.getVisibility() == View.INVISIBLE) {
					current_temperature_tv_2.setVisibility(View.VISIBLE);
				}
				if (grill_temperature_2.getVisibility() == View.INVISIBLE) {
					grill_temperature_2.setVisibility(View.VISIBLE);
				}
				if (current_temperature_2.getVisibility() == View.INVISIBLE) {
					current_temperature_2.setVisibility(View.VISIBLE);
				}
				if (grill2.getVisibility() == View.INVISIBLE) {
					grill2.setVisibility(View.VISIBLE);
				}
				current_temperature_tv_2.setText(DataUtils.displayTmeperature(currentTemperature)+ temperatureUnit);
				if(currentTemperature>=DataUtils.highTemperature){
					setFlickerAnimation(current_temperature_tv_2);
				}else{
					clearAnimation(current_temperature_tv_2);
				}
				grill2.setText(builder);
				if(grillTemperature>DataUtils.highGrillTemperature){
					setFlickerAnimation(grill2);
				}else{
					clearAnimation(grill2);
				}
				break;
			case 3:
				if (current_temperature_tv_3.getVisibility() == View.INVISIBLE) {
					current_temperature_tv_3.setVisibility(View.VISIBLE);
				}
				if (grill_temperature_3.getVisibility() == View.INVISIBLE) {
					grill_temperature_3.setVisibility(View.VISIBLE);
				}
				if (current_temperature_3.getVisibility() == View.INVISIBLE) {
					current_temperature_3.setVisibility(View.VISIBLE);
				}
				if (grill3.getVisibility() == View.INVISIBLE) {
					grill3.setVisibility(View.VISIBLE);
				}
				current_temperature_tv_3.setText(DataUtils.displayTmeperature(currentTemperature)+ temperatureUnit);
				if(currentTemperature>=DataUtils.highTemperature){
					setFlickerAnimation(current_temperature_tv_3);
				}else{
					clearAnimation(current_temperature_tv_3);
				}
				grill3.setText(builder);
				if(grillTemperature>DataUtils.highGrillTemperature){
					setFlickerAnimation(grill3);
				}else{
					clearAnimation(grill3);
				}
				break;
			case 4:
				if (current_temperature_tv_4.getVisibility() == View.INVISIBLE) {
					current_temperature_tv_4.setVisibility(View.VISIBLE);
				}
				if (grill_temperature_4.getVisibility() == View.INVISIBLE) {
					grill_temperature_4.setVisibility(View.VISIBLE);
				}
				if (current_temperature_4.getVisibility() == View.INVISIBLE) {
					current_temperature_4.setVisibility(View.VISIBLE);
				}
				if (grill4.getVisibility() == View.INVISIBLE) {
					grill4.setVisibility(View.VISIBLE);
				}
				current_temperature_tv_4.setText(DataUtils.displayTmeperature(currentTemperature)+ temperatureUnit);
				if(currentTemperature>=DataUtils.highTemperature){
					setFlickerAnimation(current_temperature_tv_4);
				}else{
					clearAnimation(current_temperature_tv_4);
				}
				grill4.setText(builder);
				if(grillTemperature>DataUtils.highGrillTemperature){
					setFlickerAnimation(grill4);
				}else{
					clearAnimation(grill4);
				}
				break;
		}

	}


	private final BroadcastReceiver temperatureUpdateUIReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Iterator iter = BleConnectUtils.uiMap.entrySet().iterator();
			BarbecueParamer barbecueParamer = null;
			int targetTemperature = 0;
			String unit = (DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
					: DataUtils.fahrenhite;
			Drawable drawable = null;
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				int key = (int) entry.getKey();
				barbecueParamer = (BarbecueParamer) entry.getValue();
				int status = barbecueParamer.getStatus();
				int type = barbecueParamer.getType();
				String degree = barbecueParamer.getDegree();
				drawable = DataUtils.updateDrawable(status, type, context,
						pagerStatus);
				if (status > 0 && status != 2) {
					targetTemperature = barbecueParamer.getTemperature();
					float rotation = 210 + targetTemperature;
					if (rotation > 360) {
						rotation -= 360;
					}
					switch (DataUtils.temperatureUnit) {
						case 1:
							targetTemperature = DataUtils
									.centigrade2Fahrenhite(targetTemperature);
							break;
						default:
							break;
					}
					String text = getResources().getString(
							R.string.single_barbecue_target_temperature,
							targetTemperature+"", unit).replaceAll("\n", " ");
					int size = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
					int pushSize = getResources().getDimensionPixelSize(R.dimen.barbecue_status_text_word_size);
					String splitValue = " ";
					int defaultColor = getResources().getColor(R.color.barbecue_status_text_color);
					int pushColor = Color.YELLOW;
					SpannableStringBuilder builder = DataUtils.setTextStyle(text,splitValue,size,pushSize,defaultColor,pushColor);
					switch (key) {
						case 1:
							if (target_temperature_1.getVisibility() == View.INVISIBLE) {
								target_temperature_1.setVisibility(View.VISIBLE);
							}
							if (target1.getVisibility() == View.INVISIBLE) {
								target1.setVisibility(View.VISIBLE);
							}
							target1.setText(builder);
							Log.i(TAG, "temperatureUpdateUIReceiver " + builder.toString()  );
							target_temperature_1
									.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
											.sin(rotation * Math.PI / 180)));
							target_temperature_1
									.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
											.cos(rotation * Math.PI / 180)));
							target_temperature_1.setRotation(rotation);
							if (barbecue_status1.getVisibility() == View.INVISIBLE) {
								barbecue_status1.setVisibility(View.VISIBLE);
							}
							barbecue_status1.setCompoundDrawables(drawable, null, null,
									null);
							if (status == 1) {
								barbecue_status1.setText(getResources().getString(
										R.string.single_barbecue_type, "", degree)
										.replace("\n", ""));
							} else {
								barbecue_status1.setText(R.string.temperature);
							}
							updateTemperatureUnit(1);
							break;

						case 2:
							if (target_temperature_2.getVisibility() == View.INVISIBLE) {
								target_temperature_2.setVisibility(View.VISIBLE);
							}
							if (target2.getVisibility() == View.INVISIBLE) {
								target2.setVisibility(View.VISIBLE);
							}

							target2.setText(builder);
							//System.out.println(" All P target2 " + builder  );
							target_temperature_2
									.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
											.sin(rotation * Math.PI / 180)));
							target_temperature_2
									.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
											.cos(rotation * Math.PI / 180)));
							target_temperature_2.setRotation(rotation);

							if (barbecue_status2.getVisibility() == View.INVISIBLE) {
								barbecue_status2.setVisibility(View.VISIBLE);
							}
							barbecue_status2.setCompoundDrawables(drawable, null, null,
									null);
							if (status == 1) {
								barbecue_status2.setText(getResources().getString(
										R.string.single_barbecue_type, "", degree)
										.replace("\n", ""));
							} else {
								barbecue_status2.setText(R.string.temperature);
							}
							updateTemperatureUnit(2);
							break;

						case 3:

							if (target_temperature_3.getVisibility() == View.INVISIBLE) {
								target_temperature_3.setVisibility(View.VISIBLE);
							}
							if (target3.getVisibility() == View.INVISIBLE) {
								target3.setVisibility(View.VISIBLE);
							}

							target3.setText(builder);
							//System.out.println(" All P target3 " + builder  );
							target_temperature_3
									.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
											.sin(rotation * Math.PI / 180)));
							target_temperature_3
									.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
											.cos(rotation * Math.PI / 180)));
							target_temperature_3.setRotation(rotation);

							if (barbecue_status3.getVisibility() == View.INVISIBLE) {
								barbecue_status3.setVisibility(View.VISIBLE);
							}
							barbecue_status3.setCompoundDrawables(drawable, null, null,
									null);
							if (status == 1) {
								barbecue_status3.setText(getResources().getString(
										R.string.single_barbecue_type,"", degree)
										.replace("\n", ""));
							} else {
								barbecue_status3.setText(R.string.temperature);
							}
							updateTemperatureUnit(3);
							break;

						case 4:
							if (target_temperature_4.getVisibility() == View.INVISIBLE) {
								target_temperature_4.setVisibility(View.VISIBLE);
							}
							if (target4.getVisibility() == View.INVISIBLE) {
								target4.setVisibility(View.VISIBLE);
							}

							target4.setText(builder);
							//System.out.println(" All P target4 " + builder  );
							target_temperature_4
									.setTranslationX((float) ((bitmapWidth / 2 - 22 * percentage) * (float) Math
											.sin(rotation * Math.PI / 180)));
							target_temperature_4
									.setTranslationY((float) ((-bitmapHeigh / 2 + 22 * percentage) * (float) Math
											.cos(rotation * Math.PI / 180)));
							target_temperature_4.setRotation(rotation);

							if (barbecue_status4.getVisibility() == View.INVISIBLE) {
								barbecue_status4.setVisibility(View.VISIBLE);
							}
							barbecue_status4.setCompoundDrawables(drawable, null, null,
									null);
							if (status == 1) {
								barbecue_status4.setText(getResources().getString(
										R.string.single_barbecue_type, "", degree)
										.replace("\n", ""));
							} else {
								barbecue_status4.setText(R.string.temperature);
							}
							updateTemperatureUnit(4);
							break;
					}
				} else {
					System.out.println("Status = 2");
					switch (key) {
						case 1:
							if (barbecue_status1.getVisibility() == View.INVISIBLE) {
								barbecue_status1.setVisibility(View.VISIBLE);
							}
							barbecue_status1.setCompoundDrawables(drawable, null, null,
									null);
							barbecue_status1.setText(R.string.timer);
							if (target1.getVisibility() == View.VISIBLE) {
								target1.setVisibility(View.INVISIBLE);
							}
							updateTemperatureUnit(1);
							break;
						case 2:
							if (barbecue_status2.getVisibility() == View.INVISIBLE) {
								barbecue_status2.setVisibility(View.VISIBLE);
							}
							barbecue_status2.setCompoundDrawables(drawable, null, null,
									null);
							barbecue_status2.setText(R.string.timer);
							if (target2.getVisibility() == View.VISIBLE) {
								target2.setVisibility(View.INVISIBLE);
							}
							updateTemperatureUnit(2);
							break;
						case 3:
							if (barbecue_status3.getVisibility() == View.INVISIBLE) {
								barbecue_status3.setVisibility(View.VISIBLE);
							}
							barbecue_status3.setCompoundDrawables(drawable, null, null,
									null);
							barbecue_status3.setText(R.string.timer);
							if (target3.getVisibility() == View.VISIBLE) {
								target3.setVisibility(View.INVISIBLE);
							}
							updateTemperatureUnit(3);
							break;
						case 4:
							if (barbecue_status4.getVisibility() == View.INVISIBLE) {
								barbecue_status4.setVisibility(View.VISIBLE);
							}
							barbecue_status4.setCompoundDrawables(drawable, null, null,
									null);
							barbecue_status4.setText(R.string.timer);
							if (target4.getVisibility() == View.VISIBLE) {
								target4.setVisibility(View.INVISIBLE);
							}
							updateTemperatureUnit(4);
							break;
					}

				}

			}
		}
	};

	private final BroadcastReceiver highAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int position = intent.getIntExtra("position", 0);
			if(!alarmChannel.containsKey(position)){
				alarmChannel.put(position,"");
				updateAlarmUI(position);
				if (highAlarmDialog != null && highAlarmDialog.isShowing()) {
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < alarmChannel.size(); i++) {
						sb.append(alarmChannel.get(i)).append(",");
					}
					highAlarmDialogTitle.setText(getResources().getString(
							R.string.dialog_title, sb.toString()));
					return;
				}
			}else{
				if (highAlarmDialog != null && highAlarmDialog.isShowing()) {
					return;
				}
			}
			long[] pattern = { 100, 400, 100, 400 }; // 振动 开启
			switch (DataUtils.alarmType) {
				case 0:
					if (DataUtils.mp == null) {
						DataUtils.mp = new MediaPlayer();
						try {
							DataUtils.mp
									.setDataSource(
											context,
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							DataUtils.mp.prepare();
							DataUtils.mp.setLooping(true);
							DataUtils.mp.start();
						} catch (Exception e) {
							e.printStackTrace();
							Log.e("hehe","DataUtils.mp.start() error");
						}
					}
					break;
				case 1:
					if (DataUtils.vibrator == null) {
						DataUtils.vibrator = (Vibrator) context
								.getSystemService(Context.VIBRATOR_SERVICE);
						// 振动 开启
						DataUtils.vibrator.vibrate(pattern, 2); // 重复两次上面的pattern
						// 如果只想震动一次，index设为-1

						// vibrator.cancel(); //振动停止
					}
					break;
				case 2:
					if (DataUtils.vibrator == null) {
						DataUtils.vibrator = (Vibrator) context
								.getSystemService(Context.VIBRATOR_SERVICE);
						DataUtils.vibrator.vibrate(pattern, 2);
					}
					if (DataUtils.mp == null) {
						DataUtils.mp = new MediaPlayer();
						try {
							DataUtils.mp
									.setDataSource(
											context,
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							DataUtils.mp.prepare();
							DataUtils.mp.setLooping(true);
							DataUtils.mp.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
			}
			highAlarmDialog = new Dialog(context, R.style.dialog);
			highAlarmDialog.setContentView(R.layout.alarm_start_dialog);
			highAlarmDialogTitle = (TextView) highAlarmDialog.findViewById(R.id.title);
			highAlarmDialogTitle.setText(getResources().getString(
					R.string.dialog_title, position + ""));
			TextView msg = (TextView) highAlarmDialog
					.findViewById(R.id.msg);
			msg.setText(R.string.high_alarm_msg);
			FrameLayout ok = (FrameLayout) highAlarmDialog.findViewById(R.id.ok);
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					highAlarmDialog.dismiss();
					Intent intent = new Intent("allProbesAlarmClick");
					ArrayList<Integer> list = new ArrayList<Integer>();
					Iterator it = alarmChannel.keySet().iterator();
					while(it.hasNext()) {
						int position = (Integer)it.next();
						updateAlarmBackUI(position);
						list.add(position);
					}
					intent.putIntegerArrayListExtra("alarmChannel", list);
					sendBroadcast(intent);
					switch (DataUtils.alarmType) {
						case 0:
							if (DataUtils.mp != null) {
								DataUtils.mp.stop();
								DataUtils.mp = null;
							}
							break;
						case 1:
							DataUtils.vibrator.cancel();
							break;
						case 2:
							if (DataUtils.mp != null) {
								DataUtils.mp.stop();
								DataUtils.mp = null;
							}
							DataUtils.vibrator.cancel();
							break;
					}
					alarmChannel.clear();
				}
			});
			highAlarmDialog.setCancelable(false);
			highAlarmDialog.show();

		}

	};

	private final BroadcastReceiver highGrillAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int position = intent.getIntExtra("position", 0);
			if(!alarmChannel.containsKey(position)){
				alarmChannel.put(position,"");
				updateAlarmUI(position);
				if (highGrillAlarmDialog != null && highGrillAlarmDialog.isShowing()) {
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < alarmChannel.size(); i++) {
						sb.append(alarmChannel.get(i)).append(",");
					}
					highGrillAlarmDialogTitle.setText(getResources().getString(
							R.string.dialog_title, sb.toString()));
					return;
				}
			}else{
				if (highGrillAlarmDialog != null && highGrillAlarmDialog.isShowing()) {
					return;
				}
			}
			long[] pattern = { 100, 400, 100, 400 }; // 振动 开启
			switch (DataUtils.alarmType) {
				case 0:
					if (DataUtils.mp == null) {
						DataUtils.mp = new MediaPlayer();
						try {
							DataUtils.mp
									.setDataSource(
											context,
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							DataUtils.mp.prepare();
							DataUtils.mp.setLooping(true);
							DataUtils.mp.start();
						} catch (Exception e) {
							e.printStackTrace();
							Log.e("hehe","DataUtils.mp.start() error");
						}
					}
					break;
				case 1:
					if (DataUtils.vibrator == null) {
						DataUtils.vibrator = (Vibrator) context
								.getSystemService(Context.VIBRATOR_SERVICE);
						// 振动 开启
						DataUtils.vibrator.vibrate(pattern, 2); // 重复两次上面的pattern
						// 如果只想震动一次，index设为-1

						// vibrator.cancel(); //振动停止
					}
					break;
				case 2:
					if (DataUtils.vibrator == null) {
						DataUtils.vibrator = (Vibrator) context
								.getSystemService(Context.VIBRATOR_SERVICE);
						DataUtils.vibrator.vibrate(pattern, 2);
					}
					if (DataUtils.mp == null) {
						DataUtils.mp = new MediaPlayer();
						try {
							DataUtils.mp
									.setDataSource(
											context,
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							DataUtils.mp.prepare();
							DataUtils.mp.setLooping(true);
							DataUtils.mp.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
			}
			highGrillAlarmDialog = new Dialog(context, R.style.dialog);
			highGrillAlarmDialog.setContentView(R.layout.alarm_start_dialog);
			highGrillAlarmDialogTitle = (TextView) highGrillAlarmDialog.findViewById(R.id.title);
			highGrillAlarmDialogTitle.setText(getResources().getString(
					R.string.dialog_title, position + ""));
			TextView msg = (TextView) highGrillAlarmDialog
					.findViewById(R.id.msg);
			msg.setText(R.string.grill_high_alarm_msg);
			FrameLayout ok = (FrameLayout) highGrillAlarmDialog.findViewById(R.id.ok);
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					highGrillAlarmDialog.dismiss();
					Intent intent = new Intent("allProbesAlarmClick");
					ArrayList<Integer> list = new ArrayList<Integer>();
					Iterator it = alarmChannel.keySet().iterator();
					while(it.hasNext()) {
						int position = (Integer)it.next();
						updateAlarmBackUI(position);
						list.add(position);
					}
					intent.putIntegerArrayListExtra("alarmChannel", list);
					sendBroadcast(intent);
					switch (DataUtils.alarmType) {
						case 0:
							if (DataUtils.mp != null) {
								DataUtils.mp.stop();
								DataUtils.mp = null;
							}
							break;
						case 1:
							DataUtils.vibrator.cancel();
							break;
						case 2:
							if (DataUtils.mp != null) {
								DataUtils.mp.stop();
								DataUtils.mp = null;
							}
							DataUtils.vibrator.cancel();
							break;
					}
					alarmChannel.clear();
				}
			});
			highGrillAlarmDialog.setCancelable(false);
			highGrillAlarmDialog.show();

		}

	};


	private final BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int position = intent.getIntExtra("position", 0);
			if(!alarmChannel.containsKey(position)){
				alarmChannel.put(position,"");
				updateAlarmUI(position);
				if (alarmDialog != null && alarmDialog.isShowing()) {
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < alarmChannel.size(); i++) {
						sb.append(alarmChannel.get(i)).append(",");
					}
					alarmDialogTitle.setText(getResources().getString(
							R.string.dialog_title, sb.toString()));
					return;
				}
			}else{
				if (alarmDialog != null && alarmDialog.isShowing()) {
					return;
				}
			}
			long[] pattern = { 100, 400, 100, 400 }; // 振动 开启
			switch (DataUtils.alarmType) {
				case 0:
					if (DataUtils.mp == null) {
						DataUtils.mp = new MediaPlayer();
						try {
							DataUtils.mp
									.setDataSource(
											context,
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							DataUtils.mp.prepare();
							DataUtils.mp.setLooping(true);
							DataUtils.mp.start();
						} catch (Exception e) {
							e.printStackTrace();
							Log.e("hehe","DataUtils.mp.start() error");
						}
					}
					break;
				case 1:
					if (DataUtils.vibrator == null) {
						DataUtils.vibrator = (Vibrator) context
								.getSystemService(Context.VIBRATOR_SERVICE);
						// 振动 开启
						DataUtils.vibrator.vibrate(pattern, 2); // 重复两次上面的pattern
						// 如果只想震动一次，index设为-1

						// vibrator.cancel(); //振动停止
					}
					break;
				case 2:
					if (DataUtils.vibrator == null) {
						DataUtils.vibrator = (Vibrator) context
								.getSystemService(Context.VIBRATOR_SERVICE);
						DataUtils.vibrator.vibrate(pattern, 2);
					}
					if (DataUtils.mp == null) {
						DataUtils.mp = new MediaPlayer();
						try {
							DataUtils.mp
									.setDataSource(
											context,
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							DataUtils.mp.prepare();
							DataUtils.mp.setLooping(true);
							DataUtils.mp.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
			}
			alarmDialog = new Dialog(context, R.style.dialog);
			alarmDialog.setContentView(R.layout.alarm_start_dialog);
			alarmDialogTitle = (TextView) alarmDialog.findViewById(R.id.title);
			alarmDialogTitle.setText(getResources().getString(
					R.string.dialog_title, position + ""));
			TextView msg = (TextView) alarmDialog
					.findViewById(R.id.msg);
			msg.setText(R.string.alarm_msg);
			FrameLayout ok = (FrameLayout) alarmDialog.findViewById(R.id.ok);
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alarmDialog.dismiss();
					Intent intent = new Intent("allProbesAlarmClick");
					ArrayList<Integer> list = new ArrayList<Integer>();
					Iterator it = alarmChannel.keySet().iterator();
					while(it.hasNext()) {
						int position = (Integer)it.next();
						updateAlarmBackUI(position);
						list.add(position);
					}
					intent.putIntegerArrayListExtra("alarmChannel", list);
					sendBroadcast(intent);
					switch (DataUtils.alarmType) {
						case 0:
							if (DataUtils.mp != null) {
								DataUtils.mp.stop();
								DataUtils.mp = null;
							}
							break;
						case 1:
							DataUtils.vibrator.cancel();
							break;
						case 2:
							if (DataUtils.mp != null) {
								DataUtils.mp.stop();
								DataUtils.mp = null;
							}
							DataUtils.vibrator.cancel();
							break;
					}
					alarmChannel.clear();
				}
			});
			alarmDialog.setCancelable(false);
			alarmDialog.show();

		}

	};

	private final BroadcastReceiver disconnectAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int position = intent.getIntExtra("position", 0);
			if(!alarmChannel.containsKey(position)){
				alarmChannel.put(position,"");
				updateAlarmUI(position);
				if (disconnectAlarmDialog != null && disconnectAlarmDialog.isShowing()) {
					StringBuffer sb = new StringBuffer();
					Iterator it = alarmChannel.keySet().iterator();
					while(it.hasNext()) {
						int pos = (Integer)it.next();
						sb.append(pos).append(",");
					}
					disconnectAlarmDialogTitle.setText(getResources().getString(
							R.string.dialog_title, sb.toString().substring(0, sb.toString().length()-1)));
					return;
				}
			}else{
				if (disconnectAlarmDialog != null && disconnectAlarmDialog.isShowing()) {
					return;
				}
			}
			long[] pattern = { 100, 400, 100, 400 }; // 振动 开启
			switch (DataUtils.alarmType) {
				case 0:
					if (DataUtils.mp == null) {
						DataUtils.mp = new MediaPlayer();
						try {
							DataUtils.mp
									.setDataSource(
											context,
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							DataUtils.mp.prepare();
							DataUtils.mp.setLooping(true);
							DataUtils.mp.start();
						} catch (Exception e) {
							e.printStackTrace();
							Log.e("hehe","DataUtils.mp.start() error");
						}
					}
					break;
				case 1:
					if (DataUtils.vibrator == null) {
						DataUtils.vibrator = (Vibrator) context
								.getSystemService(Context.VIBRATOR_SERVICE);
						// 振动 开启
						DataUtils.vibrator.vibrate(pattern, 2); // 重复两次上面的pattern
						// 如果只想震动一次，index设为-1

						// vibrator.cancel(); //振动停止
					}
					break;
				case 2:
					if (DataUtils.vibrator == null) {
						DataUtils.vibrator = (Vibrator) context
								.getSystemService(Context.VIBRATOR_SERVICE);
						DataUtils.vibrator.vibrate(pattern, 2);
					}
					if (DataUtils.mp == null) {
						DataUtils.mp = new MediaPlayer();
						try {
							DataUtils.mp
									.setDataSource(
											context,
											RingtoneManager
													.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							DataUtils.mp.prepare();
							DataUtils.mp.setLooping(true);
							DataUtils.mp.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
			}
			disconnectAlarmDialog = new Dialog(context, R.style.dialog);
			disconnectAlarmDialog.setContentView(R.layout.alarm_start_dialog);
			disconnectAlarmDialogTitle = (TextView) disconnectAlarmDialog.findViewById(R.id.title);
			disconnectAlarmDialogTitle.setText(getResources().getString(
					R.string.dialog_title, position + ""));
			TextView msg = (TextView) disconnectAlarmDialog
					.findViewById(R.id.msg);
			msg.setText(R.string.disconnect_alarm_msg);
			FrameLayout ok = (FrameLayout) disconnectAlarmDialog.findViewById(R.id.ok);
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					disconnectAlarmDialog.dismiss();
					switch (DataUtils.alarmType) {
						case 0:
							if (DataUtils.mp != null) {
								DataUtils.mp.stop();
								DataUtils.mp = null;
							}
							break;
						case 1:
							DataUtils.vibrator.cancel();
							break;
						case 2:
							if (DataUtils.mp != null) {
								DataUtils.mp.stop();
								DataUtils.mp = null;
							}
							DataUtils.vibrator.cancel();
							break;
					}
					Iterator it = alarmChannel.keySet().iterator();
					while(it.hasNext()) {
						int position = (Integer)it.next();
						updateAlarmBackUI(position);

					}
					alarmChannel.clear();
				}
			});
			disconnectAlarmDialog.setCancelable(false);
			disconnectAlarmDialog.show();

		}

	};

	private String getResources(String key) {
		Resources resources = getResources();
		int indentify = resources.getIdentifier(context.getPackageName()
				+ ":string/" + key, null, null);
		if (indentify > 0) {
			return resources.getString(indentify);
		}
		return null;
	}

	private final BroadcastReceiver bleWorkStatus = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int position = intent.getIntExtra("position", 1);
			int workStatus = intent.getIntExtra("workStatus", 0);
			int batteryStatus = intent.getIntExtra("batteryStatus", 0);
			float targetTemperature = intent.getFloatExtra("targetTemperature", 0);
			int type = intent.getIntExtra("type", 0);
			BarbecueParamer paramer = new BarbecueParamer();
			paramer.setWorkStatus(1);
			switch (workStatus) {
				case 1:
					paramer.setStatus(1);
					paramer.setTemperature(targetTemperature);
					paramer.setType(type);
					paramer.setDegree(getResources(DataUtils.getDegree(
							(int)targetTemperature, type).replace(" ", "_")));
					BleConnectUtils.uiMap.put(position, paramer);
					break;
				case 2:
					paramer.setStatus(2);
					Log.e("time", targetTemperature + "");
					int hour = (int)targetTemperature / 3600;
					int min = (int)targetTemperature % 3600 / 60;
					int second = (int)targetTemperature % 60;
					paramer.setHour(hour);
					paramer.setMin(min);
					paramer.setSecond(second);
					Log.e("time", hour + ":" + min + ":" + second);
					BleConnectUtils.uiMap.put(position, paramer);
					break;
				case 3:
					paramer.setStatus(3);
					paramer.setTemperature(targetTemperature);
					BleConnectUtils.uiMap.put(position, paramer);
					break;
			}
			BarbecueParamer barbecueParamer = BleConnectUtils.uiMap.get(position);
			if(barbecueParamer==null){
				System.out.println("barbecueParamer========================null,position=="+position);
			}
			showTargetTemperature(position);
		}
	};

}
