//package fmgtech.grillprobee.barbecue;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import fmgtech.grillprobee.barbecue.utils.DataUtils;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.SimpleAdapter;
//import android.widget.TextView;
//
//public class RecipeActivity extends Activity {
//	ImageView title_bar_back, status,setting;
//	TextView item_logo;
//	private Context context;
//	GridView gview;
//	ArrayList data_list;
//	int connectPosition;
//    private SimpleAdapter sim_adapter;
//	// 图片封装为一个数组
//	int[] icon = { R.drawable.beef_yellow, R.drawable.veal_yellow,
//			R.drawable.lamb_yellow, R.drawable.venison_yellow,
//			R.drawable.pork_yellow, R.drawable.chicken_yellow,
//			R.drawable.duck_yellow, R.drawable.fish_yellow,
//			R.drawable.hamburger_yellow, R.drawable.timer_yellow,
//			R.drawable.temperature_yellow };
//	String[] iconName ;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.recipe);
//		connectPosition = getIntent().getIntExtra("position",0);
//		context = this;
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("RecipeActivityFinish");
//		registerReceiver(mReceiver, filter);
//		iconName = getResources().getStringArray(R.array.recipe_items);
//		init();
//		initTextViewValue();
//	}
//
//	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			finish();
//		}
//	};
//
//	private void initTextViewValue(){
//		item_logo.setText(R.string.recipe);
//	}
//
//	private void init() {
//		item_logo = (TextView) findViewById(R.id.item_logo);
//		status = (ImageView) findViewById(R.id.status);
//		status.setVisibility(View.GONE);
//		setting = (ImageView) findViewById(R.id.setting);
//		setting.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(context,SettingActivity.class));
//			}
//		});
//		title_bar_back = (ImageView) findViewById(R.id.title_bar_back);
//		title_bar_back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
//		gview = (GridView) findViewById(R.id.gridview);
//		// 新建List
//		data_list = new ArrayList<Map<String, Object>>();
//		// 获取数据
//		getData();
//		// 新建适配器
//		String[] from = { "image", "text" };
//		int[] to = { R.id.image, R.id.text };
//		sim_adapter = new SimpleAdapter(this, data_list, R.layout.item, from,
//				to);
//		// 配置适配器
//		gview.setAdapter(sim_adapter);
//		gview.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
//					long arg3) {
//				Intent intent = null;
//				if(arg2==9){
//					intent = new Intent(RecipeActivity.this,TimerActivity.class);
//				}else if(arg2==10){
//					intent = new Intent(RecipeActivity.this,TemperatureActivity.class);
//				}else{
//					intent = new Intent(RecipeActivity.this,RareActivity.class);
//					intent.putExtra("value", arg2);
//				}
//				intent.putExtra("tagValue", ((TextView) view.findViewById(R.id.text)).getText().toString());
//				intent.putExtra("position", connectPosition);
//				startActivity(intent);
//			}
//		});
//	}
//
//	public List<Map<String, Object>> getData() {
//		// cion和iconName的长度是相同的，这里任选其一都可以
//		for (int i = 0; i < icon.length; i++) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("image", icon[i]);
//			map.put("text", iconName[i]);
//			data_list.add(map);
//		}
//
//		return data_list;
//	}
//
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		finish();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		unregisterReceiver(mReceiver);
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//
//}
