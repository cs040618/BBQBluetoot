package fmgtech.grillprobee.barbecue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import fmgtech.grillprobee.barbecue.utils.BarbecueParamer;
import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
import fmgtech.grillprobee.barbecue.utils.DataUtils;
import fmgtech.grillprobee.barbecue.utils.Hex;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TemperatureActivity extends Activity {
	ImageView title_bar_back, status;
	TextView item_logo,temperature_iv;
	TemperaturePanelView panView;
	Button start;
	int connectPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.temperature);
		connectPosition = getIntent().getIntExtra("position",0);
		init();
	}

	private boolean isInteger(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
																																																																																																																										
	private void init() {
		ImageView setting = (ImageView) findViewById(R.id.setting);
		setting.setClickable(true);
		temperature_iv = (TextView) findViewById(R.id.temperature);
		temperature_iv.setText(R.string.temperature);
		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TemperatureActivity.this,
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
		start = (Button)findViewById(R.id.start);
		start.setText(R.string.start);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String temp = panView.getText();
				int subPosition = 0;
				switch (DataUtils.temperatureUnit) {
				case 0:
					subPosition = temp.indexOf(DataUtils.centigrade);
					break;
				case 1:
					subPosition = temp.indexOf(DataUtils.fahrenhite);
					break;
				}
				String temperatureStr = temp.substring(0,subPosition);
				System.out.println(temperatureStr);
				if(!isInteger(temperatureStr)){
					return;
				}
				float temperatureValue = DataUtils.saveTmeperature(Float.parseFloat(temperatureStr));
				if(temperatureValue<=-41){
					return;
				}
				start.setClickable(false);
				BarbecueParamer paramer = BleConnectUtils.uiMap.get(connectPosition);
				if(paramer!=null){
					BleConnectUtils.clearData(connectPosition);
				}
				paramer = new BarbecueParamer();
				paramer.setStatus(3);
				paramer.setWorkStatus(1);
				//DecimalFormat decimalFormat=new DecimalFormat(".0");
				//temperatureValue = Float.parseFloat(decimalFormat.format(temperatureValue));
				byte[] bb = new byte[4];
				int data = (int)DataUtils.bleTmeperature(temperatureValue);
				System.out.println("Setting temp on "+ data);
				byte[] tempByte = Hex.shortToByteArray((short)data);
				bb[0] = (byte)12;
				bb[1] = tempByte[1];
				bb[2] = tempByte[0];
				BleConnectUtils.connectMap.get(connectPosition).writeLlsAlertLevel(bb);
				BleConnectUtils.connectMap.get(connectPosition).setTargetTemperature(temperatureValue);
				paramer.setTemperature(temperatureValue);
				BleConnectUtils.uiMap.put(connectPosition, paramer);
				Intent intent = new Intent("recipeUpdateUI");
				intent.putExtra("position", connectPosition);
				sendBroadcast(intent);
				sendBroadcast(new Intent("RecipeActivityFinish"));
				finish();
			}
		});

		panView = (TemperaturePanelView) findViewById(R.id.panView);
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
		item_logo.setText(R.string.temperature);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
