package fmgtech.grillprobee.barbecue;


import java.util.Locale;

import fmgtech.grillprobee.barbecue.utils.BlemeshLeScan;
import fmgtech.grillprobee.barbecue.utils.DataUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("NewApi")
public class SettingActivity extends Activity {
	private Context context;
	PopupWindow popupWindow;
	TextView item_logo;
	ImageView title_bar_back, status, celsius_status,
			fahrenheit_status;
	BlemeshLeScan mScan = new BlemeshLeScan();
	Button alarm_select;
	String[] alarmValue;
	String alarmV[] = null;
	String languageV[] = null;
	TextView dialogTitle,temperature_label,unit_label,alarm_label;
	ConnectedDeviceAdapter adapter;
	DeviceAdapter deviceAdapter;

	LinearLayout deutsch_layout,english_layout,chinese_layout,chinese_tw_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		context = this;
		alarmValue = getResources().getStringArray(R.array.alarm);
		init();
		initUI();
		alarmValue = getResources().getStringArray(R.array.alarm);
	}



	private void initUI() {
		switch (DataUtils.temperatureUnit) {
		case 0:
			celsius_status.setImageResource(R.drawable.celsius_connect);
			fahrenheit_status
					.setImageResource(R.drawable.fahrenheit_disconnect);
			break;

		case 1:
			celsius_status.setImageResource(R.drawable.celsius_disconnect);
			fahrenheit_status.setImageResource(R.drawable.fahrenheit_connect);
			break;
		}
		switch (DataUtils.alarmType) {
		case 0:
			alarm_select.setText(alarmValue[0]);
			break;
		case 1:
			alarm_select.setText(alarmValue[1]);
			break;
		case 2:
			alarm_select.setText(alarmValue[2]);
			break;
		}
		switch (DataUtils.languageType) {
			case 1:
				deutsch_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				english_layout.setBackgroundColor(Color.parseColor("#FFE177"));
				chinese_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_tw_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				break;
			case 2:
				deutsch_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				english_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_layout.setBackgroundColor(Color.parseColor("#FFE177"));
				chinese_tw_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				break;
			case 3:
				deutsch_layout.setBackgroundColor(Color.parseColor("#FFE177"));
				english_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_tw_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				break;
			case 4:
				deutsch_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				english_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_tw_layout.setBackgroundColor(Color.parseColor("#FFE177"));
				break;
		}
		chinese_tw_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deutsch_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				english_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_tw_layout.setBackgroundColor(Color.parseColor("#FFE177"));
				switchLanguage(Locale.TAIWAN);
				DataUtils.languageType = 4;
			}
		});
		deutsch_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deutsch_layout.setBackgroundColor(Color.parseColor("#FFE177"));
				english_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_tw_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				switchLanguage(Locale.GERMAN);
				DataUtils.languageType = 3;

			}
		});
		chinese_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deutsch_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				english_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_layout.setBackgroundColor(Color.parseColor("#FFE177"));
				chinese_tw_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				switchLanguage(Locale.CHINESE);
				DataUtils.languageType = 2;
			}
		});
		english_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deutsch_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				english_layout.setBackgroundColor(Color.parseColor("#FFE177"));
				chinese_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				chinese_tw_layout.setBackgroundColor(Color.parseColor("#67B29B"));
				switchLanguage(Locale.ENGLISH);
				DataUtils.languageType = 1;
			}
		});
	}



	private void init() {
		alarm_select = (Button) findViewById(R.id.alarm_select);
		temperature_label = (TextView) findViewById(R.id.temperature_label);
		unit_label = (TextView) findViewById(R.id.unit_label);
		alarm_label = (TextView) findViewById(R.id.alarm_label);
		deutsch_layout = (LinearLayout)findViewById(R.id.deutsch_layout);
		english_layout = (LinearLayout)findViewById(R.id.english_layout);
		chinese_layout = (LinearLayout)findViewById(R.id.chinese_layout);
		chinese_tw_layout = (LinearLayout)findViewById(R.id.chinese_tw_layout);
		temperature_label.setText(R.string.temperature);
		unit_label.setText(R.string.unit);
		alarm_label.setText(R.string.alarm_type);
		alarm_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(popupWindow!=null && popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				View view = LayoutInflater.from(context).inflate(R.layout.alarm_setting,null);//PopupWindow对象
				popupWindow=new PopupWindow(context);//初始化PopupWindow对象
		        popupWindow.setContentView(view);//设置PopupWindow布局文件
		        popupWindow.setBackgroundDrawable(null);
		        popupWindow.setWidth(alarm_select.getWidth());//设置PopupWindow宽
		        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);//设置PopupWindow高
		        popupWindow.setOutsideTouchable(true);
		        popupWindow.showAsDropDown(v);
		        ListView alarm_list = (ListView) view.findViewById(R.id.alarm_list);
		        switch (DataUtils.alarmType) {
					case 0:
						alarmV = new String[] { alarmValue[1],
								alarmValue[2]};
						break;
					case 1:
						alarmV = new String[] { alarmValue[0],
								alarmValue[2]};
						break;
					case 2:
						alarmV = new String[] { alarmValue[0], alarmValue[1]};
						break;
				}
				AlarmAdapter adapter = new AlarmAdapter(context,alarmV);
				alarm_list.setAdapter(adapter);
				alarm_list.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						for(int i = 0 ; i < 3 ; i++){
							if(alarmV[arg2].equals(alarmValue[i])){
								DataUtils.settingAlarm(context, i);
								break;
							}
						}
						alarm_select.setText(alarmV[arg2]);
						popupWindow.dismiss();
					}
				});
			}
		});
		item_logo = (TextView) findViewById(R.id.item_logo);
		item_logo.setText(R.string.setting);
		status = (ImageView) findViewById(R.id.status);
		title_bar_back = (ImageView) findViewById(R.id.title_bar_back);
		title_bar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		celsius_status = (ImageView) findViewById(R.id.celsius_status);
		fahrenheit_status = (ImageView) findViewById(R.id.fahrenheit_status);
		celsius_status.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(popupWindow!=null && popupWindow.isShowing()){
					popupWindow.dismiss();
			    }
				celsius_status.setImageResource(R.drawable.celsius_connect);
				fahrenheit_status
						.setImageResource(R.drawable.fahrenheit_disconnect);
				DataUtils.settingTemperatureUnit(context, 0);
				context.sendBroadcast(new Intent("temperatureUpdateUI"));
			}
		});
		fahrenheit_status.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(popupWindow!=null && popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				celsius_status.setImageResource(R.drawable.celsius_disconnect);
				fahrenheit_status
						.setImageResource(R.drawable.fahrenheit_connect);
				DataUtils.settingTemperatureUnit(context, 1);
				context.sendBroadcast(new Intent("temperatureUpdateUI"));
			}
		});
		status.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(popupWindow!=null && popupWindow.isShowing()){
			popupWindow.dismiss();
		}
		finish();
	}

	@Override
	protected void attachBaseContext(Context newBase){
		super.attachBaseContext(newBase);
	}



	public void switchLanguage(Locale locale) {
		Resources res = getResources();
		Configuration config = res.getConfiguration();
		DisplayMetrics dm = res.getDisplayMetrics();
		config.locale = locale;
		getResources().updateConfiguration(config, null);
		getResources().flushLayoutCache();
		res.updateConfiguration(config, dm);
		Intent intent = new Intent(this, ConnectActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}

}
