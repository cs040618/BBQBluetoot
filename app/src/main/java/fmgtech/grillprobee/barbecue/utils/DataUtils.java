package fmgtech.grillprobee.barbecue.utils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import fmgtech.grillprobee.barbecue.R;


public class DataUtils {
	public static LinkedHashMap<String, String> activityMap = new LinkedHashMap<String, String>();
	public static final float highTemperature = 85;
	public static final float highGrillTemperature = 275;
	public static LinkedHashMap<String, String> beef_map = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> veal_map = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> hamburger_map = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> pork_map = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> lamb_map = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> chicken_map = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> duck_map = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> fish_map = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> venison_map = new LinkedHashMap<String, String>();
	public static int maxConnectedBleSize = 4;
	public static Vibrator vibrator = null;
	public static MediaPlayer mp = null;

	public final static String centigrade = "℃";
	public final static String fahrenhite = "℉";
	public static int temperatureUnit = 0;
	public static int alarmType = 0;
	public static int languageType = 1;
	public static int selectedChannelNumber = 1;
	public static int targetTemperature1 = -1, targetTemperature2 = -1,
			targetTemperature3 = -1, targetTemperature4 = -1;

	public static void initTemperatureUnit(Context context) {
		temperatureUnit = CacheDataUtils.getTemperatureUnit(context);
	}

	public static void settingTemperatureUnit(Context context, int status) {
		CacheDataUtils.temperatureUnitSetting(context, status);
		temperatureUnit = status;
		initGrillData(context.getResources());
	}

	public static void initAlarm(Context context) {
		alarmType = CacheDataUtils.getAlaramType(context);
	}

	public static void settingAlarm(Context context, int status) {
		CacheDataUtils.alarmSetting(context, status);
		alarmType = status;
	}

	public static String selectGrillType(int arg2, Context context) {
		Resources resources = context.getResources();
		String temp[] = resources.getStringArray(R.array.recipe_items);
		return temp[arg2];
	}

	public static void initGrillData(Resources resources) {
		String[] value = null;
		beef_map.put("leftTime", 720 + "");
		veal_map.put("leftTime", 680 + "");
		hamburger_map.put("leftTime", 860 + "");
		venison_map.put("leftTime", 1560 + "");
		pork_map.put("leftTime", 1120 + "");
		lamb_map.put("leftTime", 720 + "");
		chicken_map.put("leftTime", 1080 + "");
		duck_map.put("leftTime", 1060 + "");
		fish_map.put("leftTime", 980 + "");
		switch (temperatureUnit) {
		case 0:
			value = resources.getStringArray(R.array.beef_degree_items);
			beef_map.put("Rare", value[0] + " 52℃");
			beef_map.put("Medium Rare", value[1] + " 57℃");
			beef_map.put("Medium", value[2] + " 63℃");
			beef_map.put("Medium Done", value[3] + " 68℃");
			beef_map.put("Well Done", value[4] + " 74℃");
			value = resources.getStringArray(R.array.veal_degree_items);
			veal_map.put("Medium", value[0] + " 60℃");
			veal_map.put("Medium Done", value[1] + " 63℃");
			veal_map.put("Well Done", value[2] + " 71℃");
			value = resources.getStringArray(R.array.hamburger_degree_items);
			hamburger_map.put("Medium Done", value[0] + " 68℃");
			hamburger_map.put("Well Done", value[1] + " 74℃");
			value = resources.getStringArray(R.array.venison_degree_items);
			venison_map.put("Medium Rare", value[0] + " 57℃");
			venison_map.put("Medium", value[1] + " 63℃");
			venison_map.put("Medium Done", value[2] + " 68℃");
			venison_map.put("Well Done", value[3] + " 74℃");

			value = resources.getStringArray(R.array.pork_degree_items);
			pork_map.put("Medium", value[0] + " 63℃");
			pork_map.put("Medium Done", value[1] + " 66℃");
			pork_map.put("Well Done", value[2] + " 71℃");

			value = resources.getStringArray(R.array.lamb_degree_items);
			lamb_map.put("Medium Rare", value[0] + " 57℃");
			lamb_map.put("Medium", value[1] + " 63℃");
			lamb_map.put("Medium Done", value[2] + " 68℃");
			lamb_map.put("Well Done", value[3] + " 74℃");

			value = resources.getStringArray(R.array.chicken_degree_items);
			chicken_map.put("Medium Done", value[0] + " 74℃");
			chicken_map.put("Well Done", value[1] + " 77℃");

			value = resources.getStringArray(R.array.duck_degree_items);
			duck_map.put("Medium Done", value[0] + " 71℃");
			duck_map.put("Well Done", value[1] + " 74℃");

			value = resources.getStringArray(R.array.fish_degree_items);
			fish_map.put("Medium Done", value[0] + " 57℃");
			fish_map.put("Well Done", value[1] + " 63℃");

			break;
		case 1:
			value = resources.getStringArray(R.array.beef_degree_items);
			beef_map.put("Rare", value[0] + " 125.6℉");
			beef_map.put("Medium Rare", value[1] + " 134.6℉");
			beef_map.put("Medium", value[2] + " 145.4℉");
			beef_map.put("Medium Done", value[3] + " 154.4℉");
			beef_map.put("Well Done", value[4] + " 165.2℉");
			value = resources.getStringArray(R.array.veal_degree_items);
			veal_map.put("Medium", value[0] + " 140℉");
			veal_map.put("Medium Done", value[1] + " 145.4℉");
			veal_map.put("Well Done", value[2] + " 159.8℉");
			value = resources.getStringArray(R.array.hamburger_degree_items);
			hamburger_map.put("Medium Done", value[0] + " 154.4℉");
			hamburger_map.put("Well Done", value[1] + " 165.2℉");
			value = resources.getStringArray(R.array.venison_degree_items);
			venison_map.put("Medium Rare", value[0] + " 134.6℉");
			venison_map.put("Medium", value[1] + " 145.4℉");
			venison_map.put("Medium Done", value[2] + " 154.4℉");
			venison_map.put("Well Done", value[3] + " 165.2℉");
			value = resources.getStringArray(R.array.pork_degree_items);
			pork_map.put("Medium", value[0] + " 145.4℉");
			pork_map.put("Medium Done", value[1] + " 150.8℉");
			pork_map.put("Well Done", value[2] + " 159.8℉");
			value = resources.getStringArray(R.array.lamb_degree_items);
			lamb_map.put("Medium Rare", value[0] + " 134.6℉");
			lamb_map.put("Medium", value[1] + " 145.4℉");
			lamb_map.put("Medium Done", value[2] + " 154.4℉");
			lamb_map.put("Well Done", value[3] + " 165.2℉");
			value = resources.getStringArray(R.array.chicken_degree_items);
			chicken_map.put("Medium Done", value[0] + " 165.2℉");
			chicken_map.put("Well Done", value[1] + " 170.6℉");
			value = resources.getStringArray(R.array.duck_degree_items);
			duck_map.put("Medium Done", value[0] + " 159.8℉");
			duck_map.put("Well Done", value[1] + " 165.2℉");
			value = resources.getStringArray(R.array.fish_degree_items);
			fish_map.put("Medium Done", value[0] + " 134.6℉");
			fish_map.put("Well Done", value[1] + " 145.4℉");
			break;
		}
	}

	public static HashMap<String, Integer> getCentigrade() {
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		temp.put("125.6℉", 52);
		temp.put("134.6℉", 57);
		temp.put("140℉", 60);
		temp.put("145.4℉", 63);
		temp.put("150.8℉", 66);
		temp.put("154.4℉", 68);
		temp.put("159.8℉", 71);
		temp.put("165.2℉", 74);
		temp.put("170.6℉", 77);
		return temp;
	}

	public static String getDegree(int target, int type) {
		String value = "";
		switch (type) {
		case 0:
			if (target == 52) {
				value = "Rare";
			} else if (target == 57) {
				value = "Medium Rare";
			} else if (target == 63) {
				value = "Medium";
			} else if (target == 68) {
				value = "Medium Done";
			} else if (target == 74) {
				value = "Well Done";
			}
			break;

		case 1:
			if (target == 60) {
				value = "Medium";
			} else if (target == 63) {
				value = "Medium Done";
			} else if (target == 71) {
				value = "Well Done";
			}
			break;
		case 2:
			if (target == 57) {
				value = "Medium Rare";
			} else if (target == 63) {
				value = "Medium";
			} else if (target == 68) {
				value = "Medium Done";
			} else if (target == 74) {
				value = "Well Done";
			}
			break;
		case 3:
			if (target == 57) {
				value = "Medium Rare";
			} else if (target == 63) {
				value = "Medium";
			} else if (target == 68) {
				value = "Medium Done";
			} else if (target == 74) {
				value = "Well Done";
			}
			break;
		case 4:
			if (target == 63) {
				value = "Medium";
			} else if (target == 66) {
				value = "Medium Done";
			} else if (target == 71) {
				value = "Well Done";
			}
			break;

		case 5:
			if (target == 74) {
				value = "Medium Done";
			} else if (target == 77) {
				value = "Well Done";
			}
			break;
		case 6:
			if (target == 71) {
				value = "Medium Done";
			} else if (target == 74) {
				value = "Well Done";
			}
			break;
		case 7:
			if (target == 57) {
				value = "Medium Done";
			} else if (target == 63) {
				value = "Well Done";
			}
			break;
		case 8:
			if (target == 68) {
				value = "Medium Done";
			} else if (target == 74) {
				value = "Well Done";
			}
			break;
		}
		return value;

	}

	public static int centigrade2Fahrenhite(float degree) {
		float temperature = (float) (degree * 1.8 + 32);
		return (int)temperature;
	}

	public static int fahrenhite2Centigrade(float degree) {
		float temperature = (degree - 32) / 1.8f;
		return (int)temperature;
	}

	public static float trueTmeperature(float degree) {
		Log.e("degree",degree+"");
		return degree / 10 - 40;
	}

	public static int bleTmeperature(float degree) {
		int temperature = (int) ((degree + 40) * 10);
		return temperature;
	}

	public static float saveTmeperature(float degree) {
		float temperature = degree;
		switch (temperatureUnit) {
		case 1:
			temperature = fahrenhite2Centigrade(temperature);
			break;
		default:
			break;
		}
		return temperature;
	}

	public static float saveTmeperature(String degree) {
		float temperature = 0f;
		if (degree.contains(fahrenhite)) {
			HashMap<String, Integer> temp = getCentigrade();
			temperature = temp.get(degree);
		} else {
			temperature = Float.parseFloat(degree.replace(centigrade, ""));
		}
		return temperature;
	}

	public static String displayTmeperature(float degree) {
		float temperature = degree;
		switch (temperatureUnit) {
			case 1:
				temperature = centigrade2Fahrenhite(temperature);
				break;
			default:
				break;
		}
		//DecimalFormat decimalFormat=new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		//return decimalFormat.format(temperature);
		return (int)temperature+"";
	}

	public static float displayBleTmeperature(float degree) {
		float temperature = trueTmeperature(degree);
		switch (temperatureUnit) {
		case 1:
			temperature = centigrade2Fahrenhite(temperature);
			break;
		default:
			break;
		}
		return temperature;
	}

	// pagerStatus 1表示主页面 2表示all probes页面
	public static Drawable updateDrawable(int status, int value,
			Context context, int pagerStatus) {
		Drawable drawable = null;
		if (status == 1) {
			if (pagerStatus == 1) {
				switch (value) {
				case 0:
					drawable = context.getResources().getDrawable(
							R.drawable.beef_status);
					break;
				case 1:
					drawable = context.getResources().getDrawable(
							R.drawable.veal_status);
					break;
				case 2:
					drawable = context.getResources().getDrawable(
							R.drawable.lamb_status);
					break;
				case 3:
					drawable = context.getResources().getDrawable(
							R.drawable.venison_status);
					break;
				case 4:
					drawable = context.getResources().getDrawable(
							R.drawable.pork_status);
					break;
				case 5:
					drawable = context.getResources().getDrawable(
							R.drawable.chicken_status);
					break;
				case 6:
					drawable = context.getResources().getDrawable(
							R.drawable.duck_status);
					break;
				case 7:
					drawable = context.getResources().getDrawable(
							R.drawable.fish_status);
					break;
				case 8:
					drawable = context.getResources().getDrawable(
							R.drawable.hamburger_status);
					break;
				}
			} else if (pagerStatus == 2) {
				switch (value) {
				case 0:
					drawable = context.getResources().getDrawable(
							R.drawable.beef);
					break;
				case 1:
					drawable = context.getResources().getDrawable(
							R.drawable.veal);
					break;
				case 2:
					drawable = context.getResources().getDrawable(
							R.drawable.lamb);
					break;
				case 3:
					drawable = context.getResources().getDrawable(
							R.drawable.venison);
					break;
				case 4:
					drawable = context.getResources().getDrawable(
							R.drawable.pork);
					break;
				case 5:
					drawable = context.getResources().getDrawable(
							R.drawable.chicken);
					break;
				case 6:
					drawable = context.getResources().getDrawable(
							R.drawable.duck);
					break;
				case 7:
					drawable = context.getResources().getDrawable(
							R.drawable.fish);
					break;
				case 8:
					drawable = context.getResources().getDrawable(
							R.drawable.hamburger);
					break;
				}
			}
		} else if (status == 2) {
			if (pagerStatus == 1) {
				drawable = context.getResources().getDrawable(
						R.drawable.timer_status);
			} else if (pagerStatus == 2) {
				drawable = context.getResources().getDrawable(R.drawable.timer);
			}
		} else if (status == 3) {
			if (pagerStatus == 1) {
				drawable = context.getResources().getDrawable(
						R.drawable.temperature_status);
			} else if (pagerStatus == 2) {
				drawable = context.getResources().getDrawable(
						R.drawable.temperature);
			}
		}
		if (drawable != null) {
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
		}
		return drawable;
	}

	public static String getTemperatureUnit() {
		String unit = "";
		switch (temperatureUnit) {
		case 0:
			unit = centigrade;
			break;
		case 1:
			unit = fahrenhite;
			break;
		}
		return unit;
	}

	public static String getStringTime(int cnt) {
		if(cnt>=3600){
			return getStringTimeHM(cnt);
		}else if(cnt<60){
			return getStringTimeS(cnt);
		}else{
			return getStringTimeM(cnt);
		}
	}
	public static String getStringTimeHM(int cnt) {
		int hour = cnt / 3600;
		int min = cnt % 3600 / 60;
		return String.format(Locale.CHINA, "%d h %d min", hour, min);
	}

	public static String getStringTimeM(int cnt) {
		int min = cnt % 3600 / 60;
		return String.format(Locale.CHINA, " %d min",  min);
		//return String.format(Locale.CHINA, " %02d min",  min);
	}

	public static String getStringTimeS(int cnt) {
		int second = cnt % 60;
		return String.format(Locale.CHINA, " %d s",  second);
	}

	public static int leftTime(float target, float current, float last,
			int timeCount) {
		int value = (int) ((target - current) * timeCount / (current - last));
		Log.e("value", "value==" + value);
		return value;
	}


	public static int firstLeftTime(float target, float current) {
		int value = (int) (target - current) * 15;
		return value;
	}

	public static SpannableStringBuilder setTextStyle(String textValue,
			String splitValue, int size, int pushSize, int defaultColor,
			int pushColor) {
		int splitPosition = textValue.indexOf(splitValue);
		SpannableStringBuilder builder = new SpannableStringBuilder(textValue);
		ForegroundColorSpan redSpan = new ForegroundColorSpan(pushColor);
		ForegroundColorSpan yellowSpan = new ForegroundColorSpan(defaultColor);
		StyleSpan span = new StyleSpan(Typeface.BOLD);
		AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size, false);
		builder.setSpan(span, 0, splitPosition,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(yellowSpan, 0, splitPosition,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(sizeSpan, 0, splitPosition,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span = new StyleSpan(Typeface.BOLD);
		builder.setSpan(span, splitPosition, textValue.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(redSpan, splitPosition, textValue.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sizeSpan = new AbsoluteSizeSpan(pushSize, false);
		builder.setSpan(sizeSpan, splitPosition, textValue.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}

	/*
	 * public static void changeLanguage(Context context){ Resources resources =
	 * context.getResources(); DisplayMetrics dm =
	 * resources.getDisplayMetrics(); Configuration config =
	 * resources.getConfiguration(); switch (DataUtils.languageType) { case 0:
	 * config.locale = Locale.ENGLISH; break; default: config.locale =
	 * Locale.CHINESE; break; } resources.updateConfiguration(config, dm); }
	 */
}
