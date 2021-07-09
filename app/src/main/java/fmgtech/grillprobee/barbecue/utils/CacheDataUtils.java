package fmgtech.grillprobee.barbecue.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CacheDataUtils {
	public final static String saveInfo = "SAVEINFO";
	public final static String temperatureInfo = "TEMPERATUREINFO";
	public final static String temperatureKey = "temperatureUnit";
	public final static String alarmInfo = "ALARMINFO";
	public final static String laguageInfo = "LANGUAGEINFO";
	public final static String alarmKey = "alarm";
	public final static String laguageKey = "laguage";
	public static void removeConnectInfo1(Context context, int position) {
		SharedPreferences mPrefs = context.getSharedPreferences(saveInfo,
				Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.remove(position + "");
		editor.commit();
	}

	public static void addConnectInfo1(Context context, int position,
			String productName, String mac) {
		SharedPreferences mPrefs = context.getSharedPreferences(saveInfo,
				Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putString(position + "", productName + "-" + mac);
		editor.commit();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<DeviceRecord> getConnectList1(
			Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(saveInfo,
				Context.MODE_PRIVATE);
		ArrayList<DeviceRecord> list = new ArrayList<DeviceRecord>();
		HashMap<String, String> data = (HashMap<String, String>) mPrefs
				.getAll();
		DeviceRecord record = null;
		Iterator<Entry<String, String>> iter = data.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			String val = (String) entry.getValue();
			String[] value = val.split("-");
			record = new DeviceRecord(value[0], value[1], Integer.parseInt((String) entry.getKey()));
			list.add(record);
		}
		return list;
	}

	public static boolean checkSameNameCache(ArrayList<DeviceRecord> list,
			String mac) {
		boolean flag = false;
		int size = list.size();
		DeviceRecord record;
		for (int i = 0; i < size; i++) {
			record = list.get(i);
			if(record.getMac().equals(mac)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	
	public static int checkAddPosition(Context context) {
		boolean oneStatus = false,twoStatus = false,threeStatus = false,fourStatus = false;
		SharedPreferences mPrefs = context.getSharedPreferences(saveInfo,
				Context.MODE_PRIVATE);
		HashMap<String, String> temp = (HashMap<String, String>) mPrefs.getAll();
		Iterator<Entry<String, String>> iter = temp.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			int key = Integer.parseInt((String) entry.getKey());
			switch (key) {
				case 1:
					oneStatus = true;
					break;
				case 2:
					twoStatus = true;
					break;
				case 3:
					threeStatus = true;
					break;
				case 4:
					fourStatus = true;
					break;
			}
			
		}
		if(!oneStatus){
			return 1;
		}else if(!twoStatus){
			return 2;
		}else if(!threeStatus){
			return 3;
		}else if(!fourStatus){
			return 4;
		}else {
			return 0;
		}
		
	}
	
	public static void replacePosition(Context context,int position,String productName,String mac) {
		SharedPreferences mPrefs = context.getSharedPreferences(saveInfo,
				Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putString(position + "", productName + "-" + mac);
		editor.commit();
	}
	
	public static void temperatureUnitSetting(Context context,int status) {
		SharedPreferences mPrefs = context.getSharedPreferences(temperatureInfo,
				Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putString(temperatureKey,status+"");
		editor.commit();
	}
	
	public static int getTemperatureUnit(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(temperatureInfo,
				Context.MODE_PRIVATE);
		HashMap<String,String> map = (HashMap<String, String>) mPrefs.getAll();
		if(map!=null && map.size()==1){
			return Integer.parseInt(map.get(temperatureKey));
		}else{
			return 0;
		}
	}
	
	public static void alarmSetting(Context context,int status) {
		SharedPreferences mPrefs = context.getSharedPreferences(alarmInfo,
				Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putString(alarmKey,status+"");
		editor.commit();
	}
	
	//0表示铃声    1表示振动   2表示铃声+振动
	public static int getAlaramType(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(alarmInfo,
				Context.MODE_PRIVATE);
		HashMap<String,String> map = (HashMap<String, String>) mPrefs.getAll();
		if(map!=null && map.size()==1){
			return Integer.parseInt(map.get(alarmKey));
		}else{
			return 0;
		}
	}


	public static void laguageSetting(Context context,int status) {
		SharedPreferences mPrefs = context.getSharedPreferences(laguageInfo,
				Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putString(laguageKey,status+"");
		editor.commit();
	}
	
	//0表示英语    1表示振动   2表示铃声+振动
	public static int getLaguageType(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(laguageInfo,
				Context.MODE_PRIVATE);
		HashMap<String,String> map = (HashMap<String, String>) mPrefs.getAll();
		if(map!=null && map.size()==1){
			return Integer.parseInt(map.get(laguageKey));
		}else{
			return 0;
		}
	}

	

}
