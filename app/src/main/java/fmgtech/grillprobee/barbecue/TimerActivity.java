//package fmgtech.grillprobee.barbecue;
//import java.util.ArrayList;
//
//import fmgtech.grillprobee.barbecue.utils.BarbecueParamer;
//import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
//import fmgtech.grillprobee.barbecue.utils.DataUtils;
//import fmgtech.grillprobee.barbecue.utils.Hex;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//public class TimerActivity extends Activity {
//	ImageView title_bar_back,status;
//	TextView item_logo,timer_iv;
//	Button start;
//	String temperature,temperatureStatus;
//	TimePanelView panView;
//	int connectPosition;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.timer);
//		connectPosition = getIntent().getIntExtra("position",0);
//		init();
//	}
//
//	private void init(){
//		ImageView setting = (ImageView) findViewById(R.id.setting);
//		timer_iv = (TextView) findViewById(R.id.timer);
//		timer_iv.setText(R.string.timer);
//		setting.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(TimerActivity.this,
//						SettingActivity.class);
//				ArrayList<Integer> list = new ArrayList<Integer>();
//				if (BleConnectUtils.oneStatus) {
//					list.add(1);
//				}
//				if (BleConnectUtils.twoStatus) {
//					list.add(2);
//				}
//				if (BleConnectUtils.threeStatus) {
//					list.add(3);
//				}
//				if (BleConnectUtils.fourStatus) {
//					list.add(4);
//				}
//				intent.putIntegerArrayListExtra("connectStatus", list);
//				startActivity(intent);
//			}
//		});
//		panView = (TimePanelView)findViewById(R.id.panView);
//		item_logo = (TextView)findViewById(R.id.item_logo);
//		status = (ImageView)findViewById(R.id.status);
//		status.setVisibility(View.GONE);
//		title_bar_back  =  (ImageView)findViewById(R.id.title_bar_back);
//		title_bar_back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
//		item_logo.setText(R.string.timer);
//		start = (Button)findViewById(R.id.start);
//		start.setText(R.string.start);
//		start.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				start.setClickable(false);
//				BarbecueParamer paramer = BleConnectUtils.uiMap.get(connectPosition);
//				if(paramer!=null){
//					BleConnectUtils.clearData(connectPosition);
//				}
//				BleConnectUtils.connectMap.get(connectPosition).targetTemperature = 0;
//				paramer = new BarbecueParamer();
//				paramer.setStatus(2);
//				paramer.setWorkStatus(1);
//			    String[] time = panView.getText().split(":");
//			    int hour = Integer.parseInt(time[0]);
//			    int min = Integer.parseInt(time[1]);
//				paramer.setHour(hour);
//				paramer.setMin(min);
//				byte[] bb = new byte[4];
//				byte[] temp = Hex.shortToByteArray((short)(hour*3600+min*60));
//				bb[0] = (byte)11;
//				bb[1] = temp[1];
//				bb[2] = temp[0];
//				BleConnectUtils.connectMap.get(connectPosition).writeLlsAlertLevel(bb);
//				BleConnectUtils.uiMap.put(connectPosition, paramer);
//				Intent intent = new Intent("recipeUpdateUI");
//				intent.putExtra("position", connectPosition);
//				sendBroadcast(intent);
//				sendBroadcast(new Intent("RecipeActivityFinish"));
//				finish();
//			}
//		});
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		finish();
//	}
//}
