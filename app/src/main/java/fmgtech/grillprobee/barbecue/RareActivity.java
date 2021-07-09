//package fmgtech.grillprobee.barbecue;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import fmgtech.grillprobee.barbecue.utils.BarbecueParamer;
//import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
//import fmgtech.grillprobee.barbecue.utils.DataUtils;
//import fmgtech.grillprobee.barbecue.utils.Hex;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.res.ColorStateList;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//
//public class RareActivity extends Activity {
//	ImageView title_bar_back, status;
//	TextView value_tv, status_tv, key_tv,item_logo;
//	RadioGroup radiogroup;
//	String tagValue;
//	int value = 0;
//	int connectPosition;
//	Drawable drawable;
//	Button start;
//	String temperature,temperatureStatus;
//	int leftTime;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.rare);
//		value = getIntent().getExtras().getInt("value");
//		tagValue  = (String) getIntent().getExtras().get("tagValue");
//		connectPosition = getIntent().getIntExtra("position",0);
//		init();
//		initUI();
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.temperature_ring);
//		int bitmapWidth = bitmap.getWidth();
//		if (bitmapWidth == 784) {
//			initRadioButton(value,200);
//		} else {
//			initRadioButton(value,220);
//		}
//		initTextViewValue();
//	}
//
//
//	private void initTextViewValue(){
//		start.setText(R.string.start);
//		status_tv.setText(tagValue);
//		item_logo.setText(R.string.cook_degree);
//	}
//	private void init() {
//		ImageView setting = (ImageView) findViewById(R.id.setting);
//		setting.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(RareActivity.this,
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
//		item_logo = (TextView) findViewById(R.id.item_logo);
//		status = (ImageView) findViewById(R.id.status);
//		status.setVisibility(View.GONE);
//		title_bar_back = (ImageView) findViewById(R.id.title_bar_back);
//		title_bar_back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
//		start = (Button)findViewById(R.id.start);
//		start.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				start.setClickable(false);
//				BarbecueParamer paramer = BleConnectUtils.uiMap.get(connectPosition);
//				if(paramer!=null){
//					BleConnectUtils.clearData(connectPosition);
//				}
//				byte[] bb = new byte[4];
//				float temperatureValue = DataUtils.saveTmeperature(temperature);
//				int data = (int)DataUtils.bleTmeperature(temperatureValue);
//				byte[] temp = Hex.shortToByteArray((short)data);
//				bb[0] = (byte)10;
//				bb[1] = temp[1];
//				bb[2] = temp[0];
//				bb[3] = (byte)value;
//				BleConnectUtils.connectMap.get(connectPosition).writeLlsAlertLevel(bb);
//				BleConnectUtils.connectMap.get(connectPosition).setTargetTemperature(temperatureValue);
//				paramer = new BarbecueParamer();
//				paramer.setTemperature(temperatureValue);
//				paramer.setDegree(temperatureStatus);
//				paramer.setType(value);
//				paramer.setStatus(1);
//				paramer.setWorkStatus(1);
//				paramer.setMin(leftTime/60);
//				paramer.setSecond(leftTime%60);
//				BleConnectUtils.uiMap.put(connectPosition, paramer);
//				Intent intent = new Intent("recipeUpdateUI");
//				intent.putExtra("position", connectPosition);
//				sendBroadcast(intent);
//				sendBroadcast(new Intent("RecipeActivityFinish"));
//				finish();
//			}
//		});
//		radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
//		key_tv = (TextView) findViewById(R.id.key_tv);
//		value_tv = (TextView) findViewById(R.id.value_tv);
//		status_tv = (TextView) findViewById(R.id.status_tv);
//		radiogroup.setOnCheckedChangeListener(mylistener);
//	}
//
//	private void initUI() {
//		switch(value){
//			case 0:
//			drawable = getResources().getDrawable(R.drawable.beef_status);
//			leftTime = Integer.parseInt(DataUtils.beef_map.get("leftTime"));
//			break;
//			case 1:
//			drawable = getResources().getDrawable(R.drawable.veal_status);
//			leftTime = Integer.parseInt(DataUtils.veal_map.get("leftTime"));
//			break;
//			case 2:
//			drawable = getResources().getDrawable(R.drawable.lamb_status);
//			leftTime = Integer.parseInt(DataUtils.lamb_map.get("leftTime"));
//			break;
//			case 3:
//			drawable = getResources().getDrawable(R.drawable.venison_status);
//			leftTime = Integer.parseInt(DataUtils.veal_map.get("leftTime"));
//			break;
//			case 4:
//			drawable = getResources().getDrawable(R.drawable.pork_status);
//			leftTime = Integer.parseInt(DataUtils.pork_map.get("leftTime"));
//			break;
//			case 5:
//			drawable = getResources().getDrawable(R.drawable.chicken_status);
//			leftTime = Integer.parseInt(DataUtils.chicken_map.get("leftTime"));
//			break;
//			case 6:
//			drawable = getResources().getDrawable(R.drawable.duck_status);
//			leftTime = Integer.parseInt(DataUtils.duck_map.get("leftTime"));
//			break;
//			case 7:
//			drawable = getResources().getDrawable(R.drawable.fish_status);
//			leftTime = Integer.parseInt(DataUtils.fish_map.get("leftTime"));
//			break;
//			case 8:
//			drawable = getResources().getDrawable(R.drawable.hamburger_status);
//			leftTime = Integer.parseInt(DataUtils.hamburger_map.get("leftTime"));
//			break;
//		}
//		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//				drawable.getMinimumHeight());
//		status_tv.setCompoundDrawables(null, drawable, null, null);
//
//	}
//
//	private void initRadioButton(int value,int h ){
//		LinkedHashMap<String, String> map = null;
//		switch (value) {
//		  case 0 :
//			map = DataUtils.beef_map;
//			break;
//		  case 1:
//			map = DataUtils.veal_map;
//			break;
//		  case 2:
//			map = DataUtils.lamb_map;
//			break;
//		  case 3:
//			map = DataUtils.venison_map;
//			break;
//		  case 4:
//			map = DataUtils.pork_map;
//			break;
//		  case 5:
//			map = DataUtils.chicken_map;
//			break;
//		  case 6:
//			map = DataUtils.duck_map;
//			break;
//		  case 7:
//			map = DataUtils.fish_map;
//			break;
//		  case 8:
//			map = DataUtils.hamburger_map;
//			break;
//		}
//		int leftMargins = 10;
//		int topMargins = 10;
//		RadioButton radioButton = null;
//		RadioGroup.LayoutParams layoutParams = null;
//		Iterator<Entry<String, String>> iterator= map.entrySet().iterator();
//		int count = 0;
//		boolean first = true;
//		while(iterator.hasNext())  {
//		    Entry entry = iterator.next();
//		    if(entry.getKey().equals("leftTime")){
//		    	continue;
//		    }
//		    radioButton = new RadioButton(this);
//			layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,h);
//			if(count++==0){
//				layoutParams.setMargins(leftMargins, 0, 0, 0);
//			}else{
//				layoutParams.setMargins(leftMargins, topMargins, 0, 0);
//			}
//			radioButton.setLayoutParams(layoutParams);
//			String temp  = ((String)entry.getValue());
//			int splitPosition = temp.lastIndexOf(" ");
//			radioButton.setText(temp.substring(splitPosition+1)+"\n"+temp.substring(0,splitPosition));
//			radioButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP,getResources().getDimension(R.dimen.rare_item_size));
//			radioButton.setGravity(Gravity.LEFT|Gravity.CENTER);
//			radioButton.setPadding(10, 10, 10, 10);
//			ColorStateList csl = createColorStateList();
//			radioButton.setTextColor(csl);//设置选中/未选中的文字颜色
//			radioButton.setButtonDrawable(R.drawable.radio_button_style);
//			radiogroup.addView(radioButton);//将单选按钮添加到RadioGroup中
//			if(first){
//				radioButton.setChecked(true);
//				first = false;
//			}
//		}
//
//	}
//
//    private ColorStateList createColorStateList() {
//        int[][] states = new int[][]{
//                new int[]{android.R.attr.state_checked},
//                new int[]{}
//        };
//        int[] colors = new int[]{
//                getResources().getColor(R.color.rare_select_text_color),
//                getResources().getColor(R.color.rare_text_color)
//        };
//        return new ColorStateList(states, colors);
//    }
//
//	RadioGroup.OnCheckedChangeListener mylistener = new RadioGroup.OnCheckedChangeListener() {
//		@Override
//		public void onCheckedChanged(RadioGroup Group, int Checkid) {
//			setText();
//
//		}
//	};
//
//	private void setText() {
//		RadioButton radioButton = (RadioButton) findViewById(radiogroup
//				.getCheckedRadioButtonId());
//		String[] temp = radioButton.getText().toString().split("\n");
//		key_tv.setText(temp[1]);
//		value_tv.setText(temp[0]);
//		temperature = temp[0];
//		temperatureStatus = temp[1];
//
//	}
//
//
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
//
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		finish();
//	}
//}
