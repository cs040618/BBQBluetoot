package fmgtech.grillprobee.barbecue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fmgtech.grillprobee.barbecue.task.BleScanUtils;
import fmgtech.grillprobee.barbecue.task.CutTimerTask;
import fmgtech.grillprobee.barbecue.utils.AutoConnectUtils;
import fmgtech.grillprobee.barbecue.utils.BarbecueParamer;
import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;
import fmgtech.grillprobee.barbecue.utils.BleGatt;
import fmgtech.grillprobee.barbecue.utils.BlemeshLeScan;
import fmgtech.grillprobee.barbecue.utils.DataUtils;
import fmgtech.grillprobee.barbecue.utils.DeviceRecord;
import fmgtech.grillprobee.barbecue.utils.Hex;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.provider.Settings;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.core.app.ActivityCompat;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

@SuppressLint("NewApi")
public class ConnectActivity extends Activity {
    BleGatt gatt;
    private LinearLayout loading;
    private Bitmap targetBitmap;

    private int bitmapWidth, bitmapHeigh;
    private float percentage = 1;
    Animation animation1, animation2, animation3, animation4, grillAnimation,
            currentAnimation, batteryAnimation;
    BluetoothManager bluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    private Context context;
    Dialog dialog;
    Dialog alarmDialog, disconnectAlarmDialog, highAlarmDialog, highGrillAlarmDialog;
    int connectPosition1 = 1;
    int handDisconnectPosition = -1;
    BlemeshLeScan mScan = new BlemeshLeScan();
    SettingMenuImageView menu_type;
    LinearLayout all_product_layout;
    ImageView setting, connect_status, one, two, three, four, lan_status;
    TextView dialogTitle, alarmDialogTitle, start_temperature, end_temperature,
            left_time_tv, current_temperature_tv, name_label,
            disconnectAlarmDialogTitle, highAlarmDialogTitle, highGrillAlarmDialogTitle, left_time_label,
            current_temperature_label;
    ListView dialog_ble_list, dialog_connected_list;
    ConnectedDeviceAdapter adapter;
    DeviceAdapter deviceAdapter;
    TextView barbecue_status, target, grill;
    Drawable drawable;
    private int leftTime1, leftTime2, leftTime3, leftTime4;
    Button action, webView_data;
    LinearLayout status_layout;
    ArrayList<Integer> alarmChannel = new ArrayList<Integer>();
    ArrayList<Integer> disconnectAlarmChannel = new ArrayList<Integer>();
    ArrayList<Integer> highAlarmChannel = new ArrayList<Integer>();
    ArrayList<Integer> highGrillAlarmChannel = new ArrayList<Integer>();
    ImageView target_temperature, grill_temperature, current_temperature;
    ImageView battery;
    public static int screenWidth = 0, screenHeight = 0;
    private PowerManager.WakeLock mWakeLock;
    String postServerUrl = "";
    Boolean isDataSent = false;
    String BbqName = "";
    String netIp = "127.0.0.1";
    String mDeviceIMEI = "";

    //private ArrayList bbqn = new ArrayList<String>();
    private void releaseWakeLock() {
        if (null != mWakeLock && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.connect);
        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        askUserPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        context = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction("connectedMainPager");
        filter.addAction("disConnectedMainPager");
        filter.addAction("connectedErrorMainPager");
        filter.addAction("disConnectErrorMainPager");
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("recipeUpdateUI");
        DataUtils.activityMap.remove("ConnectActivity");
        registerReceiver(recipeUpdateUIReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("CutTimerUpdate");
        filter.addAction("RaiseTimerUpdate");
        registerReceiver(timerUIReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("temperatureUpdateUI");
        registerReceiver(temperatureUpdateUIReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("showAlarmDialogInMainPager");
        filter.addAction("showDisconnectAlarmDialogInMainPager");
        filter.addAction("showHighAlarmDialogInMainPager");
        filter.addAction("showHighGrillAlarmDialogInMainPager");
        registerReceiver(alarmReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("temperatureData");
        filter.addAction("leftTime");
        registerReceiver(bleDataReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("addBle");
        registerReceiver(addBLEReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("batteryLow");
        registerReceiver(batteryLowReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("ringAction");
        registerReceiver(updateRingReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("BleWorkStatus");
        registerReceiver(bleWorkStatus, filter);
        filter = new IntentFilter();
        filter.addAction("targetBleMac");
        registerReceiver(targetBleMacReceiver, filter);
        filter = new IntentFilter();
        filter.addAction("allProbesAlarmClick");
        registerReceiver(allProbesAlarmClickReceiver, filter);

        filter = new IntentFilter();
        filter.addAction("closePage");
        registerReceiver(allProbesAlarmClickReceiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.temperature_ring);
        bitmapWidth = bitmap.getWidth();
        bitmapHeigh = bitmap.getHeight();
        if (bitmapWidth == 784) {
            percentage = 1;
        } else {
            percentage = bitmapWidth / 784f;
        }
        init();
        initTextViewValue();
        if (BleConnectUtils.connectMap.size() > 0) {
            int count = BleConnectUtils.connectMap.size();
            if (count > 1) {
                all_product_layout.setClickable(true);
                all_product_layout.setBackgroundResource(R.drawable.button_able_bg);
            }

            connect_status.setImageResource(R.drawable.ble_status);
            Iterator iter = BleConnectUtils.connectMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                int key = (int) entry.getKey();
                if (key == DataUtils.selectedChannelNumber) {
                    menu_type.update(1);
                    switch (key) {
                        case 1:
                            BleConnectUtils.oneStatus = true;
                            one.setBackgroundResource(R.drawable.one_big_connect);
                            break;
                        case 2:
                            BleConnectUtils.twoStatus = true;
                            two.setBackgroundResource(R.drawable.two_big_connect);
                            break;
                        case 3:
                            BleConnectUtils.threeStatus = true;
                            three.setBackgroundResource(R.drawable.three_big_connect);
                            break;
                        case 4:
                            BleConnectUtils.fourStatus = true;
                            four.setBackgroundResource(R.drawable.four_big_connect);
                            break;
                    }
                } else {
                    switch (key) {
                        case 1:
                            BleConnectUtils.oneStatus = true;
                            one.setBackgroundResource(R.drawable.one_small_connect);
                            break;
                        case 2:
                            BleConnectUtils.twoStatus = true;
                            two.setBackgroundResource(R.drawable.two_small_connect);
                            break;
                        case 3:
                            BleConnectUtils.threeStatus = true;
                            three.setBackgroundResource(R.drawable.three_small_connect);
                            break;
                        case 4:
                            BleConnectUtils.fourStatus = true;
                            four.setBackgroundResource(R.drawable.four_small_connect);
                            break;
                    }
                }
            }

        }
        /*PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, getClass().getCanonicalName());
        if (null != mWakeLock) {
            mWakeLock.acquire();

        */
        if (isNetworkAvailable())
            netIp = getNetWorkIp();
        else
            netIp = "127.0.0.1";
        mDeviceIMEI = BluetoothAdapter.getDefaultAdapter().getName();
        Log.i("Test", "IMEI : " + mDeviceIMEI + "  IP : " + netIp);
    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("app", "Network connectivity change");
            if (isNetworkAvailable()) {
                Log.i("Test", "Testing server connection");
                http_server_OK();
            } else
                lan_status.setImageResource(R.drawable.button_red);
        }
    };

    /**
     * 需要申请的权限，一个组内的权限通过了一个，其他的也默认通过
     */
    public String mNeed_Permission = "";

    public static final int PERMISSION_RESULT = 0;


    public boolean isPermission = true;

    /**
     * 权限申请
     */
    public void askUserPermission(String permission) {
        mNeed_Permission = permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, mNeed_Permission) != PackageManager.PERMISSION_GRANTED) {
                /**
                 *  第一次安装直接展示对话框，如果用户拒绝了，就shouldShowRequestPermissionRationale==true,
                 *  如果用户选择了nerver ask again,那么就不再弹出对话框,同一组的权限，允许了一个，其他的默认允许
                 */
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, mNeed_Permission)) {
                    ActivityCompat.requestPermissions(this, new String[]{mNeed_Permission}, PERMISSION_RESULT);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{mNeed_Permission}, PERMISSION_RESULT);
                }
            } else {
                //LogcatHelper.getInstance(context).start();
            }
        } else {
            //LogcatHelper.getInstance(context).start();
        }
    }


    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    //請求權限結果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { //請求權限結果
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_RESULT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermission = true;
                    LogcatHelper.getInstance(context).start();
                } else {
                    isPermission = false;
                }
                break;
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission();
                    connect_status.setClickable(false);
                    AutoConnectUtils.getInstance().autoConnectStatus = false;
                    AutoConnectUtils.getInstance().cleartargetList();
                    if (AutoConnectUtils.getInstance().connectionStatus == 1) {
                        new WaitAutoConnectTask().execute();
                    } else {
                        if (BleConnectUtils.oneStatus || BleConnectUtils.twoStatus
                                || BleConnectUtils.threeStatus
                                || BleConnectUtils.fourStatus) {
                            connect_status.setImageResource(R.drawable.ble_status);
                        } else {
                            connect_status.setImageResource(R.drawable.disconnect);
                        }
                        showBleConnectDialog();
                    }
                } else {
                    //permission denied, boo! Disable the functionality that depends on this permission.
                    //这里进行权限被拒绝的处理
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //System.out.println(" " + BleConnectUtils.connectList.size());
        //if (BleConnectUtils.connectList.size() <=0 )
        //    left_time_tv.setText("");
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {//廣播接收器
        @Override
        public void onReceive(Context context, Intent intent) { //接收
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (action.equals("connectedMainPager")) {
                int scanPosition = bundle.getInt("scanPosition");
                int connectPosition = bundle.getInt("position");
                if (gatt != null) {
                    BleConnectUtils.connectMap.put(connectPosition, gatt);
                    gatt = null;
                    if (AutoConnectUtils.getInstance().autoConnectStatus) {
                        if (!AutoConnectUtils.getInstance().targetListStatus()) {
                            BleScanUtils.getInstance(mScan, context).stop();
                            if (timerTask != null) {
                                timerTask.cancel();
                                counter = 0;
                            }
                            sendBroadcast(new Intent("autoConnectFinish"));
                        } else {
                            System.out.println("还有没有连接的设备");
                        }
                    }
                }
                if (disconnectAlarmDialog != null
                        && disconnectAlarmDialog.isShowing()) {
                    disconnectAlarmDialog.dismiss();
                    switch (DataUtils.alarmType) {
                        case 0:
                            if (DataUtils.mp != null) {
                                DataUtils.mp.stop();
                                DataUtils.mp = null;
                            }

                            break;
                        case 1:
                            if (DataUtils.vibrator != null) {
                                DataUtils.vibrator.cancel();
                                DataUtils.vibrator = null;
                            }
                            break;
                        case 2:
                            if (DataUtils.mp != null) {
                                DataUtils.mp.stop();
                                DataUtils.mp = null;
                            }
                            if (DataUtils.vibrator != null) {
                                DataUtils.vibrator.cancel();
                                DataUtils.vibrator = null;
                            }
                            break;
                    }
                    for (int i = 0; i < disconnectAlarmChannel.size(); i++) {
                        updateAlarmBackUI(disconnectAlarmChannel.get(i));
                    }
                    disconnectAlarmChannel.clear();
                }
                updateUI(connectPosition, scanPosition);
            } else if (action.equals("disConnectedMainPager")) {
                int removePosition = bundle.getInt("removePosition");
                if (dialog != null && dialog.isShowing()) {
                    mScan.stopScan();
                    mScan.startScan(true);
                }
                String mac = bundle.getString("mac");
                updateRemoveUI(removePosition);
                endTimer(removePosition);
                int count = 0;
                if (BleConnectUtils.oneStatus) {
                    count++;
                }
                if (BleConnectUtils.twoStatus) {
                    count++;
                }
                if (BleConnectUtils.threeStatus) {
                    count++;
                }
                if (BleConnectUtils.fourStatus) {
                    count++;
                }
                if (count > 1) {
                    all_product_layout.setClickable(true);
                    all_product_layout.setBackgroundResource(R.drawable.button_able_bg);
                } else {
                    all_product_layout.setClickable(false);
                    all_product_layout.setBackgroundResource(R.drawable.button_disable_bg);
                }
                boolean autoConnectStatus = AutoConnectUtils.getInstance().autoConnectStatus;
                if (autoConnectStatus) {
                    BleScanUtils.getInstance(mScan, context).run();
                    if (!mac.equals(AutoConnectUtils.getInstance().autoConnectMac)) {
                        counter = 120;
                        test1();
                    }
                }
            } else if (action.equals("connectedErrorMainPager")) {
                boolean autoConnectStatus = AutoConnectUtils.getInstance().autoConnectStatus;
                if (autoConnectStatus) {
                    BleScanUtils.getInstance(mScan, context).run();
                }
            }
            if (loading.getVisibility() == View.VISIBLE) {
                loading.setVisibility(View.GONE);
            }
        }
    };

    ViewStub viewStub;
    private final BroadcastReceiver addBLEReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (viewStub != null) {
                dialog_ble_list.removeFooterView(viewStub);
            }
            deviceAdapter = new DeviceAdapter(context, mScan.devices);
            dialog_ble_list.setAdapter(deviceAdapter);
            viewStub = new ViewStub(context);
            dialog_ble_list.addFooterView(viewStub);

        }
    };

    private final BroadcastReceiver targetBleMacReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position", 0);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(intent
                    .getStringExtra("mac"));
            gatt = new BleGatt();
            gatt.setWork(false);
            gatt.setPosition(0);
            gatt.init(ConnectActivity.this, position);
            gatt.connect(device);
            gatt.setBBQName(BbqName);
            gatt.setIP(netIp);
            gatt.setIMEI(mDeviceIMEI);
        }
    };

    //溫度旋轉
    private float temperature2Rotation(float targetTemperature) {
        float rotation = 210 + targetTemperature +40;
        if (rotation > 360) {
            rotation -= 360-60;
        }
        return rotation;
    }

    //顯示目標溫度
    private void showTargetTemperature(int position) { //showTargetTemperature
        System.out.println("position======" + position);
        BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
        if (paramer == null) {
            return;
        }
        int status = paramer.getStatus();
        int type = paramer.getType();
        String degree = paramer.getDegree();
        float targetTemperature = paramer.getTemperature();
        drawable = DataUtils.updateDrawable(status, type, context, 1);
        action.setVisibility(View.VISIBLE);
        action.setText(R.string.start);
        status_layout.setVisibility(View.VISIBLE);
        switch (status) {
            case 2:
                target_temperature.setVisibility(View.GONE);
                break;
            default:
                target_temperature.setVisibility(View.VISIBLE);
                float rotation = temperature2Rotation(targetTemperature);
                if (rotation >= 210 && rotation < 270) {
                    target_temperature
                            .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                    .sin(rotation * Math.PI / 180)));
                    target_temperature
                            .setTranslationY((float) ((-bitmapHeigh / 2 + 32 * percentage) * (float) Math
                                    .cos(rotation * Math.PI / 180)));
                    target_temperature.setRotation(rotation);
                } else if (rotation > 90 && rotation <= 150) {
                    target_temperature
                            .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                    .sin(rotation * Math.PI / 180)));
                    target_temperature
                            .setTranslationY((float) ((-bitmapHeigh / 2 + 32 * percentage) * (float) Math
                                    .cos(rotation * Math.PI / 180)));
                    target_temperature.setRotation(rotation);
                } else {
                    target_temperature
                            .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                    .sin(rotation * Math.PI / 180)));
                    target_temperature
                            .setTranslationY((float) ((-bitmapHeigh / 2 + 68 * percentage) * (float) Math
                                    .cos(rotation * Math.PI / 180)));
                    target_temperature.setRotation(rotation);
                }
                break;
        }
        if (barbecue_status.getVisibility() == View.INVISIBLE) {
            barbecue_status.setVisibility(View.GONE);
        }

        if (status == 1 || status == 3) {
            String temperature = DataUtils.displayTmeperature(paramer
                    .getTemperature());
            barbecue_status.setCompoundDrawables(null, drawable, null, null);
            if (status == 1) {
                barbecue_status.setText(getResources().getString(
                        R.string.single_barbecue_type,
                        DataUtils.selectGrillType(type, context), degree));
            } else {
                barbecue_status.setText(R.string.temperature);
            }
            target.setVisibility(View.VISIBLE);
            String unit = (DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
                    : DataUtils.fahrenhite;
            String text = getResources().getString(
                    R.string.single_barbecue_target_temperature, temperature,
                    unit);
            int size = getResources().getDimensionPixelSize(
                    R.dimen.barbecue_status_text_word_size);
            int pushSize = getResources().getDimensionPixelSize(
                    R.dimen.left_time_word_size);
            String splitValue = "\n";
            int defaultColor = getResources().getColor(
                    R.color.barbecue_status_text_color);
            int pushColor = Color.YELLOW;
            SpannableStringBuilder builder = DataUtils.setTextStyle(text,
                    splitValue, size, pushSize, defaultColor, pushColor);
            target.setText(builder);
        } else if (status == 2) {
            barbecue_status.setCompoundDrawables(null, drawable, null, null);
            barbecue_status.setText(R.string.timer);
            target.setVisibility(View.INVISIBLE);
        }
    }

    private void firstLeftTime(int position, int leftTime) {
        switch (position) {
            case 1:
                if (BleConnectUtils.oneStatus) {
                    if (BleConnectUtils.cutTimerTask1 != null) {
                        BleConnectUtils.cutTimerTask1.cancel();
                        BleConnectUtils.timer1.cancel();
                    }
                    leftTime1 = leftTime;
                    BleConnectUtils.timer1 = new Timer();
                    BleConnectUtils.cutTimerTask1 = new CutTimerTask(1, leftTime1,
                            context);
                    BleConnectUtils.timer1.schedule(BleConnectUtils.cutTimerTask1,
                            1000, 1000);
                }

                break;
            case 2:
                if (BleConnectUtils.twoStatus) {
                    if (BleConnectUtils.cutTimerTask2 != null) {
                        BleConnectUtils.cutTimerTask2.cancel();
                        BleConnectUtils.timer2.cancel();
                    }
                    leftTime2 = leftTime;
                    BleConnectUtils.timer2 = new Timer();
                    BleConnectUtils.cutTimerTask2 = new CutTimerTask(2, leftTime2,
                            context);
                    BleConnectUtils.timer2.schedule(BleConnectUtils.cutTimerTask2,
                            1000, 1000);
                }

                break;
            case 3:
                if (BleConnectUtils.threeStatus) {
                    if (BleConnectUtils.cutTimerTask3 != null) {
                        BleConnectUtils.cutTimerTask3.cancel();
                        BleConnectUtils.timer3.cancel();
                    }
                    leftTime3 = leftTime;
                    BleConnectUtils.timer3 = new Timer();
                    BleConnectUtils.cutTimerTask3 = new CutTimerTask(3, leftTime3,
                            context);
                    BleConnectUtils.timer3.schedule(BleConnectUtils.cutTimerTask3,
                            1000, 1000);
                }

                break;
            case 4:
                if (BleConnectUtils.fourStatus) {
                    if (BleConnectUtils.cutTimerTask4 != null) {
                        BleConnectUtils.cutTimerTask4.cancel();
                        BleConnectUtils.timer4.cancel();
                    }
                    leftTime4 = leftTime;
                    BleConnectUtils.timer4 = new Timer();
                    BleConnectUtils.cutTimerTask4 = new CutTimerTask(4, leftTime4,
                            context);
                    BleConnectUtils.timer4.schedule(BleConnectUtils.cutTimerTask4,
                            1000, 1000);
                }

                break;
        }
    }

    private final BroadcastReceiver recipeUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int position = bundle.getInt("position");
            BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
            int status = paramer.getStatus();
            int type = paramer.getType();
            String degree = paramer.getDegree();
            float targetTemperature = paramer.getTemperature();
            drawable = DataUtils.updateDrawable(status, type, context, 1);
            action.setVisibility(View.VISIBLE);
            action.setText(R.string.stop);
            menu_type.update(0);
            BleConnectUtils.connectMap.get(position).startFlag = true;
            status_layout.setVisibility(View.VISIBLE);
            left_time_tv.setText(R.string.cooking);
            switch (status) {
                case 2:
                    target_temperature.setVisibility(View.GONE);
                    break;
                default:
                    target_temperature.setVisibility(View.VISIBLE);
                    float rotation = temperature2Rotation(targetTemperature);
                    if (rotation >= 210 && rotation < 270) {
                        target_temperature
                                .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                        .sin(rotation * Math.PI / 180)));
                        target_temperature
                                .setTranslationY((float) ((-bitmapHeigh / 2 + 32 * percentage) * (float) Math
                                        .cos(rotation * Math.PI / 180)));
                        target_temperature.setRotation(rotation);
                    } else if (rotation > 90 && rotation <= 150) {
                        target_temperature
                                .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                        .sin(rotation * Math.PI / 180)));
                        target_temperature
                                .setTranslationY((float) ((-bitmapHeigh / 2 + 32 * percentage) * (float) Math
                                        .cos(rotation * Math.PI / 180)));
                        target_temperature.setRotation(rotation);
                    } else {
                        target_temperature
                                .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                        .sin(rotation * Math.PI / 180)));
                        target_temperature
                                .setTranslationY((float) ((-bitmapHeigh / 2 + 68 * percentage) * (float) Math
                                        .cos(rotation * Math.PI / 180)));
                        target_temperature.setRotation(rotation);
                    }
                    break;
            }
            switch (position) {
                case 1:
                    if (BleConnectUtils.cutTimerTask1 != null) {
                        BleConnectUtils.cutTimerTask1.cancel();
                        BleConnectUtils.timer1.cancel();
                    }
                    if (BleConnectUtils.oneStatus && paramer.getStatus() == 2) {
                        leftTime1 = paramer.getHour() * 3600 + paramer.getMin()
                                * 60 + paramer.getSecond();
                        BleConnectUtils.timer1 = new Timer();
                        BleConnectUtils.cutTimerTask1 = new CutTimerTask(1,
                                leftTime1, context);
                        BleConnectUtils.timer1.schedule(
                                BleConnectUtils.cutTimerTask1, 1000, 1000);
                    }
                    break;
                case 2:
                    if (BleConnectUtils.cutTimerTask2 != null) {
                        BleConnectUtils.cutTimerTask2.cancel();
                        BleConnectUtils.timer2.cancel();
                    }
                    if (BleConnectUtils.twoStatus && paramer.getStatus() == 2) {
                        leftTime2 = paramer.getHour() * 3600 + paramer.getMin()
                                * 60 + paramer.getSecond();
                        BleConnectUtils.timer2 = new Timer();
                        BleConnectUtils.cutTimerTask2 = new CutTimerTask(2,
                                leftTime2, context);
                        BleConnectUtils.timer2.schedule(
                                BleConnectUtils.cutTimerTask2, 1000, 1000);
                    }

                    break;
                case 3:
                    if (BleConnectUtils.cutTimerTask3 != null) {
                        BleConnectUtils.cutTimerTask3.cancel();
                        BleConnectUtils.timer3.cancel();
                    }
                    if (BleConnectUtils.threeStatus && paramer.getStatus() == 2) {
                        leftTime3 = paramer.getHour() * 3600 + paramer.getMin()
                                * 60 + paramer.getSecond();
                        BleConnectUtils.timer3 = new Timer();
                        BleConnectUtils.cutTimerTask3 = new CutTimerTask(3,
                                leftTime3, context);
                        BleConnectUtils.timer3.schedule(
                                BleConnectUtils.cutTimerTask3, 1000, 1000);

                    }

                    break;
                case 4:
                    if (BleConnectUtils.cutTimerTask4 != null) {
                        BleConnectUtils.cutTimerTask4.cancel();
                        BleConnectUtils.timer4.cancel();
                    }
                    if (BleConnectUtils.fourStatus && paramer.getStatus() == 2) {
                        leftTime4 = paramer.getHour() * 3600 + paramer.getMin()
                                * 60 + paramer.getSecond();
                        BleConnectUtils.timer4 = new Timer();
                        BleConnectUtils.cutTimerTask4 = new CutTimerTask(4,
                                leftTime4, context);
                        BleConnectUtils.timer4.schedule(
                                BleConnectUtils.cutTimerTask4, 1000, 1000);
                    }

                    break;
            }
            if (barbecue_status.getVisibility() == View.INVISIBLE) {
                barbecue_status.setVisibility(View.GONE);
            }

            if (status == 1 || status == 3) {
                String temperature = DataUtils.displayTmeperature(paramer
                        .getTemperature());
                barbecue_status
                        .setCompoundDrawables(null, drawable, null, null);
                if (status == 1) {
                    barbecue_status.setText(getResources().getString(
                            R.string.single_barbecue_type,
                            DataUtils.selectGrillType(type, context), degree));
                } else {
                    barbecue_status.setText(R.string.temperature);
                }
                target.setVisibility(View.VISIBLE);
                String unit = (DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
                        : DataUtils.fahrenhite;
                String text = getResources().getString(
                        R.string.single_barbecue_target_temperature,
                        temperature, unit);
                int size = getResources().getDimensionPixelSize(
                        R.dimen.barbecue_status_text_word_size);
                int pushSize = getResources().getDimensionPixelSize(
                        R.dimen.left_time_word_size);
                String splitValue = "\n";
                int defaultColor = getResources().getColor(
                        R.color.barbecue_status_text_color);
                int pushColor = Color.YELLOW;
                SpannableStringBuilder builder = DataUtils.setTextStyle(text,
                        splitValue, size, pushSize, defaultColor, pushColor);
                target.setText(builder);
            } else if (status == 2) {
                barbecue_status
                        .setCompoundDrawables(null, drawable, null, null);
                barbecue_status.setText(R.string.timer);
                target.setVisibility(View.INVISIBLE);
            }
        }

    };

    private final BroadcastReceiver temperatureUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayMinMaxTemperature();
            BarbecueParamer paramer = BleConnectUtils.uiMap
                    .get(DataUtils.selectedChannelNumber);
            if (paramer == null) {
                return;
            }
            int status = paramer.getStatus();
            int type = paramer.getType();
            String degree = paramer.getDegree();
            String temperature = DataUtils.displayTmeperature(paramer
                    .getTemperature());
            drawable = DataUtils.updateDrawable(status, type, context, 1);
            status_layout.setVisibility(View.VISIBLE);
            displayMinMaxTemperature();
            if (status == 1 || status == 3) {
                barbecue_status
                        .setCompoundDrawables(null, drawable, null, null);
                if (status == 1) {
                    barbecue_status.setText(getResources().getString(
                            R.string.single_barbecue_type,
                            DataUtils.selectGrillType(type, context), degree));
                } else {
                    barbecue_status.setText(R.string.temperature);
                }
                target.setVisibility(View.VISIBLE);
                String unit = (DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
                        : DataUtils.fahrenhite;
                String text = getResources().getString(
                        R.string.single_barbecue_target_temperature,
                        temperature, unit);
                int size = getResources().getDimensionPixelSize(
                        R.dimen.barbecue_status_text_word_size);
                int pushSize = getResources().getDimensionPixelSize(
                        R.dimen.left_time_word_size);
                String splitValue = "\n";
                int defaultColor = getResources().getColor(
                        R.color.barbecue_status_text_color);
                int pushColor = Color.YELLOW;
                SpannableStringBuilder builder = DataUtils.setTextStyle(text,
                        splitValue, size, pushSize, defaultColor, pushColor);
                target.setText(builder);
            } else if (status == 2) {
                barbecue_status
                        .setCompoundDrawables(null, drawable, null, null);
                barbecue_status.setText(R.string.timer);
                target.setVisibility(View.INVISIBLE);
            }
            barbecue_status.setVisibility(View.GONE);
        }
    };

    private final BroadcastReceiver bleDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            if (actionStr.equals("temperatureData")) {
                float currentTemperature = intent.getFloatExtra("current", 0);
                float grillTemperature = intent.getFloatExtra("grill", 0);
                int position = intent.getIntExtra("position", 0);
                String thePoeName = intent.getStringExtra("pon");
                //Log.i("TEST", " Channel " + position + " Thoe POE Name : " + thePoeName);
                left_time_tv.setText(R.string.cooking);
                switch (DataUtils.selectedChannelNumber) {
                    case 1:

                        if (position == 1)
                            name_label.setText(thePoeName);
                        //name_label.setText(BleConnectUtils.channelName1.replace("BBQ ProbeE ",""));
                        //Log.i("TEST", " Channel " + DataUtils.selectedChannelNumber + "  The POE Name : "+thePoeName);

                        break;
                    case 2:

                        if (position == 2)
                            name_label.setText(thePoeName);
                        //name_label.setText(BleConnectUtils.channelName2.replace("BBQ ProbeE ",""));
                        //Log.i("TEST", " Channel " + DataUtils.selectedChannelNumber + " Name : "+thePoeName);

                        break;
                    case 3:

                        if (position == 3)
                            name_label.setText(thePoeName);
                        //name_label.setText(BleConnectUtils.channelName3.replace("BBQ ProbeE ",""));
                        //Log.i("TEST", " Channel " + DataUtils.selectedChannelNumber + " Thoe POE Name : " + thePoeName);

                        break;
                    case 4:

                        if (position == 4)
                            name_label.setText(thePoeName);
                        //name_label.setText(BleConnectUtils.channelName4.replace("BBQ ProbeE ",""));
                        //Log.i("TEST", " Channel " + DataUtils.selectedChannelNumber + " Thoe POE Name : " + thePoeName);

                        break;
                }
                temperatureHand(currentTemperature, grillTemperature, position);
            } else if (actionStr.equals("leftTime")) {
                int countDownFinish = intent.getIntExtra("countDownFinish", 0);
                int position = intent.getIntExtra("position", 0);
                int leftTime = intent.getIntExtra("leftTime", 0);
                if (leftTime == 0) {
                    switch (position) {
                        case 1:
                            if (BleConnectUtils.cutTimerTask1 != null) {
                                BleConnectUtils.cutTimerTask1.setLeftTime(leftTime);
                            }
                            break;
                        case 2:
                            if (BleConnectUtils.cutTimerTask2 != null) {
                                BleConnectUtils.cutTimerTask2.setLeftTime(leftTime);
                            }
                            break;
                        case 3:
                            if (BleConnectUtils.cutTimerTask3 != null) {
                                BleConnectUtils.cutTimerTask3.setLeftTime(leftTime);
                            }
                            break;
                        case 4:
                            if (BleConnectUtils.cutTimerTask4 != null) {
                                BleConnectUtils.cutTimerTask4.setLeftTime(leftTime);
                            }
                            break;
                    }
                }
                Log.e("leftTime,countDown", leftTime + "," + countDownFinish);
                BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
                if (paramer != null && paramer.getWorkStatus() == 1) {
                    if (countDownFinish == 1) {
                        if (DataUtils.selectedChannelNumber == position) {
                            //left_time_tv.setText(DataUtils.getStringTime(leftTime));
                        }
                    } else if (countDownFinish == -1) {
                        if (paramer.getStatus() != 2) {
                            if (leftTime < 0) {
                                leftTime = 0;
                            }
                            switch (position) {
                                case 1:
                                    if (BleConnectUtils.cutTimerTask1 == null) {
                                        leftTime1 = leftTime;
                                        BleConnectUtils.timer1 = new Timer();
                                        BleConnectUtils.cutTimerTask1 = new CutTimerTask(
                                                1, leftTime1, context);
                                        BleConnectUtils.timer1.schedule(
                                                BleConnectUtils.cutTimerTask1,
                                                1000, 1000);
                                    } else {
                                        BleConnectUtils.cutTimerTask1
                                                .setLeftTime(leftTime);
                                        leftTime1 = leftTime;
                                    }
                                    break;
                                case 2:
                                    if (BleConnectUtils.cutTimerTask2 == null) {
                                        leftTime2 = leftTime;
                                        BleConnectUtils.timer2 = new Timer();
                                        BleConnectUtils.cutTimerTask2 = new CutTimerTask(
                                                2, leftTime2, context);
                                        BleConnectUtils.timer2.schedule(
                                                BleConnectUtils.cutTimerTask2,
                                                1000, 1000);
                                    } else {
                                        BleConnectUtils.cutTimerTask2
                                                .setLeftTime(leftTime);
                                        leftTime2 = leftTime;
                                    }
                                    break;
                                case 3:
                                    if (BleConnectUtils.cutTimerTask3 == null) {
                                        leftTime3 = leftTime;
                                        BleConnectUtils.timer3 = new Timer();
                                        BleConnectUtils.cutTimerTask3 = new CutTimerTask(
                                                3, leftTime3, context);
                                        BleConnectUtils.timer3.schedule(
                                                BleConnectUtils.cutTimerTask3,
                                                1000, 1000);
                                    } else {
                                        BleConnectUtils.cutTimerTask3
                                                .setLeftTime(leftTime);
                                        leftTime3 = leftTime;
                                    }
                                    break;
                                case 4:
                                    if (BleConnectUtils.cutTimerTask4 == null) {
                                        leftTime4 = leftTime;
                                        BleConnectUtils.timer4 = new Timer();
                                        BleConnectUtils.cutTimerTask4 = new CutTimerTask(
                                                4, leftTime4, context);
                                        BleConnectUtils.timer4.schedule(
                                                BleConnectUtils.cutTimerTask4,
                                                1000, 1000);
                                    } else {
                                        BleConnectUtils.cutTimerTask4
                                                .setLeftTime(leftTime);
                                        leftTime4 = leftTime;
                                    }
                                    break;
                            }
                        }
                    }
                }
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

    private final BroadcastReceiver timerUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String actionStr = intent.getAction();
            int position = bundle.getInt("position");
            if (position != DataUtils.selectedChannelNumber) {
                return;
            }
            if (action.getText().toString()
                    .equals(getResources().getString(R.string.start))) {
                return;
            }
            switch (position) {
                case 1:
                    leftTime1 = bundle.getInt("leftTime");
                    if (leftTime1 <= 0) {
                        Log.e("leftTime", "======================" + leftTime1 + ",position==" + position);
                    }
                    //left_time_tv.setText(DataUtils.getStringTime(leftTime1));
                    break;
                case 2:
                    leftTime2 = bundle.getInt("leftTime");
                    //left_time_tv.setText(DataUtils.getStringTime(leftTime2));
                    break;
                case 3:
                    leftTime3 = bundle.getInt("leftTime");
                    //left_time_tv.setText(DataUtils.getStringTime(leftTime3));
                    break;
                case 4:
                    leftTime4 = bundle.getInt("leftTime");
                    //left_time_tv.setText(DataUtils.getStringTime(leftTime4));
                    break;
            }
        }
    };

    public void updateUI(int position, int scanPosition) {
        if (position == DataUtils.selectedChannelNumber) {
            menu_type.update(1);
            switch (position) {
                case 1:
                    BleConnectUtils.oneStatus = true;
                    one.setBackgroundResource(R.drawable.one_big_connect);
                    break;
                case 2:
                    BleConnectUtils.twoStatus = true;
                    two.setBackgroundResource(R.drawable.two_big_connect);
                    break;
                case 3:
                    BleConnectUtils.threeStatus = true;
                    three.setBackgroundResource(R.drawable.three_big_connect);
                    break;
                case 4:
                    BleConnectUtils.fourStatus = true;
                    four.setBackgroundResource(R.drawable.four_big_connect);
                    break;
            }
        } else {
            switch (position) {
                case 1:
                    BleConnectUtils.oneStatus = true;
                    one.setBackgroundResource(R.drawable.one_small_connect);
                    break;
                case 2:
                    BleConnectUtils.twoStatus = true;
                    two.setBackgroundResource(R.drawable.two_small_connect);
                    break;
                case 3:
                    BleConnectUtils.threeStatus = true;
                    three.setBackgroundResource(R.drawable.three_small_connect);
                    break;
                case 4:
                    BleConnectUtils.fourStatus = true;
                    four.setBackgroundResource(R.drawable.four_small_connect);
                    break;
            }
        }

        int count = 0;
        if (BleConnectUtils.oneStatus) {
            count++;
        }
        if (BleConnectUtils.twoStatus) {
            count++;
        }
        if (BleConnectUtils.threeStatus) {
            count++;
        }
        if (BleConnectUtils.fourStatus) {
            count++;
        }
        if (count > 1) {
            all_product_layout.setClickable(true);
            all_product_layout.setBackgroundResource(R.drawable.button_able_bg);
        }
        if (BleConnectUtils.oneStatus || BleConnectUtils.twoStatus
                || BleConnectUtils.threeStatus || BleConnectUtils.fourStatus) {
            connect_status.setImageResource(R.drawable.ble_status);
        } else {
            connect_status.setImageResource(R.drawable.disconnect);
        }
        updateBleConnectDialog(scanPosition);
    }

    public void startTimer(int position, BarbecueParamer paramer) {
        switch (position) {
            case 1:
                if (BleConnectUtils.cutTimerTask1 == null) {
                    leftTime1 = paramer.getHour() * 3600
                            + paramer.getMin() * 60;
                    BleConnectUtils.timer1 = new Timer();
                    BleConnectUtils.cutTimerTask1 = new CutTimerTask(
                            1, leftTime1, context);
                    BleConnectUtils.timer1.schedule(
                            BleConnectUtils.cutTimerTask1, 1000,
                            1000);
                }
                break;
            case 2:
                if (BleConnectUtils.cutTimerTask2 == null) {
                    leftTime2 = paramer.getHour() * 3600
                            + paramer.getMin() * 60;
                    BleConnectUtils.cutTimerTask2 = new CutTimerTask(2,
                            leftTime2, context);
                    BleConnectUtils.timer2 = new Timer();
                    BleConnectUtils.timer2.schedule(
                            BleConnectUtils.cutTimerTask2, 1000, 1000);
                }
                break;
            case 3:
                if (BleConnectUtils.cutTimerTask3 == null) {
                    leftTime3 = paramer.getHour() * 3600
                            + paramer.getMin() * 60;
                    BleConnectUtils.cutTimerTask3 = new CutTimerTask(3,
                            leftTime3, context);
                    BleConnectUtils.timer3 = new Timer();
                    BleConnectUtils.timer3.schedule(
                            BleConnectUtils.cutTimerTask3, 1000, 1000);
                }
                break;
            case 4:
                if (BleConnectUtils.cutTimerTask4 == null) {
                    leftTime4 = paramer.getHour() * 3600
                            + paramer.getMin() * 60;
                    BleConnectUtils.cutTimerTask4 = new CutTimerTask(4,
                            leftTime4, context);
                    BleConnectUtils.timer4 = new Timer();
                    BleConnectUtils.timer4.schedule(
                            BleConnectUtils.cutTimerTask4, 1000, 1000);
                }
                break;
        }

    }

    public void endTimer(int position) {
        switch (position) {
            case 1:
                if (BleConnectUtils.cutTimerTask1 != null) {
                    BleConnectUtils.cutTimerTask1.cancel();
                    BleConnectUtils.timer1.cancel();
                    BleConnectUtils.cutTimerTask1 = null;
                    BleConnectUtils.timer1 = null;
                }
                break;
            case 2:
                if (BleConnectUtils.cutTimerTask2 != null) {
                    BleConnectUtils.cutTimerTask2.cancel();
                    BleConnectUtils.timer2.cancel();
                    BleConnectUtils.cutTimerTask2 = null;
                    BleConnectUtils.timer2 = null;
                }
                break;
            case 3:
                if (BleConnectUtils.cutTimerTask3 != null) {
                    BleConnectUtils.cutTimerTask3.cancel();
                    BleConnectUtils.timer3.cancel();
                    BleConnectUtils.cutTimerTask3 = null;
                    BleConnectUtils.timer3 = null;
                }
                break;
            case 4:
                if (BleConnectUtils.cutTimerTask4 != null) {
                    BleConnectUtils.cutTimerTask4.cancel();
                    BleConnectUtils.timer4.cancel();
                    BleConnectUtils.cutTimerTask4 = null;
                    BleConnectUtils.timer4 = null;
                }
                break;
        }

    }

    public void updateBleWorkStatusUI(int position) {
        action.setVisibility(View.VISIBLE);
        action.setText(R.string.start);
        BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
        if (paramer != null) {
            if (paramer.getStatus() == 2) {
                startTimer(position, paramer);
            }
            showTargetTemperature(DataUtils.selectedChannelNumber);
            if (position == DataUtils.selectedChannelNumber && paramer.getWorkStatus() == 1) {
                action.setText(R.string.stop);
                left_time_tv.setText(R.string.cooking);
                menu_type.update(0);
            }
        } else {
            action.setVisibility(View.GONE);
        }

    }

    public void updateAlarmBackUI(int position) {
        BarbecueParamer paramer = BleConnectUtils.uiMap.get(position);
        if (paramer != null) {
            paramer.setWorkStatus(2);
        }
        ImageView view = null;
        BleConnectUtils.clearData(position);
        boolean bleConnectStatus = false;
        if (position == DataUtils.selectedChannelNumber) {
            left_time_tv.setText("_ _ _ _");
            switch (position) {
                case 1:
                    view = one;
                    if (BleConnectUtils.oneStatus) {
                        bleConnectStatus = true;
                        one.setBackgroundResource(R.drawable.one_big_connect);
                    } else {
                        one.setBackgroundResource(R.drawable.one_big_disconnect);
                    }
                    break;
                case 2:
                    view = two;
                    if (BleConnectUtils.twoStatus) {
                        bleConnectStatus = true;
                        two.setBackgroundResource(R.drawable.two_big_connect);
                    } else {
                        two.setBackgroundResource(R.drawable.two_big_disconnect);
                    }
                    break;
                case 3:
                    view = three;
                    if (BleConnectUtils.threeStatus) {
                        bleConnectStatus = true;
                        three.setBackgroundResource(R.drawable.three_big_connect);
                    } else {
                        three.setBackgroundResource(R.drawable.three_big_disconnect);
                    }
                    break;
                case 4:
                    view = four;
                    if (BleConnectUtils.fourStatus) {
                        bleConnectStatus = true;
                        four.setBackgroundResource(R.drawable.four_big_connect);
                    } else {
                        four.setBackgroundResource(R.drawable.four_big_disconnect);
                    }
                    break;
            }
            view.clearAnimation();
            if (bleConnectStatus && paramer.getWorkStatus() == 1) {
                menu_type.update(1);
                action.setText(R.string.start);
            } else {
                menu_type.update(0);
                action.setVisibility(View.GONE);
            }
        } else {
            switch (position) {
                case 1:
                    view = one;
                    if (BleConnectUtils.oneStatus) {
                        one.setBackgroundResource(R.drawable.one_small_connect);
                    } else {
                        one.setBackgroundResource(R.drawable.one_small_disconnect);
                    }
                    break;
                case 2:
                    view = two;
                    if (BleConnectUtils.twoStatus) {
                        two.setBackgroundResource(R.drawable.two_small_connect);
                    } else {
                        two.setBackgroundResource(R.drawable.two_small_disconnect);
                    }
                    break;
                case 3:
                    view = three;
                    if (BleConnectUtils.threeStatus) {
                        three.setBackgroundResource(R.drawable.three_small_connect);
                    } else {
                        three.setBackgroundResource(R.drawable.three_small_disconnect);
                    }
                    break;
                case 4:
                    view = four;
                    if (BleConnectUtils.fourStatus) {
                        four.setBackgroundResource(R.drawable.four_small_connect);
                    } else {
                        four.setBackgroundResource(R.drawable.four_small_disconnect);
                    }
                    break;
            }
            view.clearAnimation();
        }

    }

    public void updateAlarmUI(int position) {
        AnimationDrawable anim = null;
        if (position == DataUtils.selectedChannelNumber) {
            switch (position) {
                case 1:
                    one.setBackgroundResource(R.drawable.main_one_select_anim);
                    anim = (AnimationDrawable) one.getBackground();
                    break;
                case 2:
                    two.setBackgroundResource(R.drawable.main_two_select_anim);
                    anim = (AnimationDrawable) two.getBackground();
                    break;
                case 3:
                    three.setBackgroundResource(R.drawable.main_three_select_anim);
                    anim = (AnimationDrawable) three.getBackground();
                    break;
                case 4:
                    four.setBackgroundResource(R.drawable.main_four_select_anim);
                    anim = (AnimationDrawable) four.getBackground();
                    break;
            }
        } else {
            switch (position) {
                case 1:
                    one.setBackgroundResource(R.drawable.main_one_anim);
                    anim = (AnimationDrawable) one.getBackground();
                    break;
                case 2:
                    two.setBackgroundResource(R.drawable.main_two_anim);
                    anim = (AnimationDrawable) two.getBackground();
                    break;
                case 3:
                    three.setBackgroundResource(R.drawable.main_three_anim);
                    anim = (AnimationDrawable) three.getBackground();
                    break;
                case 4:
                    four.setBackgroundResource(R.drawable.main_four_anim);
                    anim = (AnimationDrawable) four.getBackground();
                    break;
            }

        }
        anim.start();
    }

    public void updateRemoveUI(int position) {
        Log.i("Button", "updateRemoveUI");
        BleConnectUtils.clearData(position);
        adapter.notifyDataSetChanged();
        if (dialog != null && dialog.isShowing()) {
            dialogTitle.setText(((BleConnectUtils.connectList == null) ? 0
                    : BleConnectUtils.connectList.size()) + "/4");
        }
        if (position == DataUtils.selectedChannelNumber) {
            clearAnimation(battery);
            hideTemperatureHand(position);
            hideStatusLayout();
            menu_type.update(0);
            action.setText(R.string.start);
            action.setVisibility(View.GONE);
            switch (position) {
                case 1:
                    BleConnectUtils.oneStatus = false;
                    one.setBackgroundResource(R.drawable.one_big_disconnect);
                    break;
                case 2:
                    BleConnectUtils.twoStatus = false;
                    two.setBackgroundResource(R.drawable.two_big_disconnect);
                    break;
                case 3:
                    BleConnectUtils.threeStatus = false;
                    three.setBackgroundResource(R.drawable.three_big_disconnect);
                    break;
                case 4:
                    BleConnectUtils.fourStatus = false;
                    four.setBackgroundResource(R.drawable.four_big_disconnect);
                    break;
            }
        } else {
            switch (position) {
                case 1:
                    BleConnectUtils.oneStatus = false;
                    one.setBackgroundResource(R.drawable.one_small_disconnect);
                    break;
                case 2:
                    BleConnectUtils.twoStatus = false;
                    two.setBackgroundResource(R.drawable.two_small_disconnect);
                    break;
                case 3:
                    BleConnectUtils.threeStatus = false;
                    three.setBackgroundResource(R.drawable.three_small_disconnect);
                    break;
                case 4:
                    BleConnectUtils.fourStatus = false;
                    four.setBackgroundResource(R.drawable.four_small_disconnect);
                    break;
            }
        }
        if (BleConnectUtils.oneStatus || BleConnectUtils.twoStatus
                || BleConnectUtils.threeStatus || BleConnectUtils.fourStatus) {
            connect_status.setImageResource(R.drawable.ble_status);
        } else {
            connect_status.setImageResource(R.drawable.disconnect);
            all_product_layout.setClickable(false);
            all_product_layout.setBackgroundResource(R.drawable.button_disable_bg);
        }

    }

    private void hideTemperatureHand(int position) {
        target_temperature.setVisibility(View.GONE);
        grill_temperature.setVisibility(View.GONE);
        current_temperature.setVisibility(View.GONE);
        BarbecueParamer barbecueParamer = BleConnectUtils.uiMap.get(position);
        BleGatt bleGatt = BleConnectUtils.connectMap.get(position);
        if (position == DataUtils.selectedChannelNumber && barbecueParamer != null && barbecueParamer.getWorkStatus() == 1) {
            if (bleGatt.calculateUtils.countDownFinish == 1) {
                //left_time_tv.setText(DataUtils.getStringTime(bleGatt.calculateUtils.lastLeftTime));
            } else {
                left_time_tv.setText(R.string.cooking);
            }
        } else {
            //left_time_tv.setText("_ _ _ _");
            //System.out.println(" Channel Tab UI Change.");
        }
        current_temperature_tv.setText("_ _ _ _");
    }

    private void hideStatusLayout() {
        Log.i("Button", "hideStatusLayout");
        status_layout.setVisibility(View.INVISIBLE);
        grill.setVisibility(View.INVISIBLE);
        target.setVisibility(View.INVISIBLE);
        //barbecue_status.setVisibility(View.GONE);
        barbecue_status.setVisibility(View.INVISIBLE);
        action.setVisibility(View.GONE);
    }

    private SpannableStringBuilder setTextStyle1(String textValue,
                                                 String splitValue, int size, int pushSize) {
        int splitPosition = textValue.indexOf(splitValue);
        SpannableStringBuilder builder = new SpannableStringBuilder(textValue);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.WHITE);
        ForegroundColorSpan yellowSpan = new ForegroundColorSpan(getResources()
                .getColor(R.color.barbecue_status_text_color));
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

    //更改頻道更新UI
    private void changeChannelUpdateUI() {
        BarbecueParamer paramer = BleConnectUtils.uiMap
                .get(DataUtils.selectedChannelNumber);

        String timer = "";
        int status = 0;
        int type = 0;
        String degree = null;
        float targetTemperature = -1f;
        boolean bleConnectStatus = false;
        float grillTemperature = -1f;
        float currentTemperature = -1f;
        if (paramer != null) {
            status = paramer.getStatus();
            type = paramer.getType();
            degree = paramer.getDegree();
            targetTemperature = paramer.getTemperature();
            System.out.println(" Channel Tab has been Clicked " + paramer.getTemperature());
        }

        switch (DataUtils.selectedChannelNumber) {
            case 1:
                if (BleConnectUtils.oneStatus) {
                    grillTemperature = BleConnectUtils.grill_temperature1;
                    currentTemperature = BleConnectUtils.current_temperature1;
                    bleConnectStatus = true;
                    one.setBackgroundResource(R.drawable.one_big_connect);
                    //System.out.println("leftTime1 == " + leftTime1);
                    if (BleConnectUtils.batteryLow1 == 1) {
                        batteryLowChange(DataUtils.selectedChannelNumber);
                    } else {
                        clearAnimation(battery);
                    }
                    switch (status) {
                        case 2:
                            targetTemperature = -1;
                            break;
                        case 1:
                        case 3:
                            break;
                    }
                } else {
                    one.setBackgroundResource(R.drawable.one_big_disconnect);
                }
                if (BleConnectUtils.twoStatus) {
                    two.setBackgroundResource(R.drawable.two_small_connect);
                } else {
                    two.setBackgroundResource(R.drawable.two_small_disconnect);
                }
                if (BleConnectUtils.threeStatus) {
                    three.setBackgroundResource(R.drawable.three_small_connect);
                } else {
                    three.setBackgroundResource(R.drawable.three_small_disconnect);
                }
                if (BleConnectUtils.fourStatus) {
                    four.setBackgroundResource(R.drawable.four_small_connect);
                } else {
                    four.setBackgroundResource(R.drawable.four_small_disconnect);
                }
                name_label.setText(BleConnectUtils.channelName1.replace("BBQ ProbeE ", ""));
                //name_label.setText(bbqn.get(0).toString());
                //System.out.println("changeChannelUpdateUI ---- " + BleConnectUtils.channelName1);
                break;
            case 2:
                if (BleConnectUtils.twoStatus) {
                    bleConnectStatus = true;
                    grillTemperature = BleConnectUtils.grill_temperature2;
                    currentTemperature = BleConnectUtils.current_temperature2;
                    two.setBackgroundResource(R.drawable.two_big_connect);
                    //System.out.println("leftTime2 == " + leftTime2);
                    if (BleConnectUtils.batteryLow2 == 1) {
                        batteryLowChange(DataUtils.selectedChannelNumber);
                    } else {
                        clearAnimation(battery);
                    }
                    switch (status) {
                        case 2:
                            targetTemperature = -1;
                            break;
                        case 1:
                        case 3:
                            break;
                    }
                } else {
                    two.setBackgroundResource(R.drawable.two_big_disconnect);
                }
                if (BleConnectUtils.oneStatus) {
                    one.setBackgroundResource(R.drawable.one_small_connect);
                } else {
                    one.setBackgroundResource(R.drawable.one_small_disconnect);
                }
                if (BleConnectUtils.threeStatus) {
                    three.setBackgroundResource(R.drawable.three_small_connect);
                } else {
                    three.setBackgroundResource(R.drawable.three_small_disconnect);
                }
                if (BleConnectUtils.fourStatus) {
                    four.setBackgroundResource(R.drawable.four_small_connect);
                } else {
                    four.setBackgroundResource(R.drawable.four_small_disconnect);
                }
                //name_label.setText(bbqn.get(1).toString());
                name_label.setText(BleConnectUtils.channelName2.replace("BBQ ProbeE ", ""));
                break;
            case 3:
                if (BleConnectUtils.threeStatus) {
                    bleConnectStatus = true;
                    grillTemperature = BleConnectUtils.grill_temperature3;
                    currentTemperature = BleConnectUtils.current_temperature3;
                    three.setBackgroundResource(R.drawable.three_big_connect);
                    timer = DataUtils.getStringTime(leftTime3);
                    switch (status) {
                        case 2:
                            targetTemperature = -1;
                            break;
                        case 1:
                        case 3:
                            break;
                    }
                } else {
                    three.setBackgroundResource(R.drawable.three_big_disconnect);
                }
                if (BleConnectUtils.oneStatus) {
                    one.setBackgroundResource(R.drawable.one_small_connect);
                } else {
                    one.setBackgroundResource(R.drawable.one_small_disconnect);
                }
                if (BleConnectUtils.twoStatus) {
                    two.setBackgroundResource(R.drawable.two_small_connect);
                } else {
                    two.setBackgroundResource(R.drawable.two_small_disconnect);
                }
                if (BleConnectUtils.fourStatus) {
                    four.setBackgroundResource(R.drawable.four_small_connect);
                } else {
                    four.setBackgroundResource(R.drawable.four_small_disconnect);
                }
                name_label.setText(BleConnectUtils.channelName3.replace("BBQ ProbeE ", ""));
                //name_label.setText(bbqn.get(2).toString());
                break;
            case 4:
                if (BleConnectUtils.fourStatus) {
                    bleConnectStatus = true;
                    grillTemperature = BleConnectUtils.grill_temperature4;
                    currentTemperature = BleConnectUtils.current_temperature4;
                    four.setBackgroundResource(R.drawable.four_big_connect);
                    timer = DataUtils.getStringTime(leftTime4);
                    switch (status) {
                        case 2:
                            targetTemperature = -1;
                            break;
                        case 1:
                        case 3:
                            break;
                    }
                } else {
                    four.setBackgroundResource(R.drawable.four_big_disconnect);
                    //hideTemperatureHand(selectedChannelNumber);
                    //hideStatusLayout();
                }
                if (BleConnectUtils.oneStatus) {
                    one.setBackgroundResource(R.drawable.one_small_connect);
                } else {
                    one.setBackgroundResource(R.drawable.one_small_disconnect);
                }
                if (BleConnectUtils.twoStatus) {
                    two.setBackgroundResource(R.drawable.two_small_connect);
                } else {
                    two.setBackgroundResource(R.drawable.two_small_disconnect);
                }
                if (BleConnectUtils.threeStatus) {
                    three.setBackgroundResource(R.drawable.three_small_connect);
                } else {
                    three.setBackgroundResource(R.drawable.three_small_disconnect);
                }
                if (BleConnectUtils.channelName4 != null)
                    name_label.setText(BleConnectUtils.channelName4.replace("BBQ ProbeE ", ""));
                //name_label.setText(bbqn.get(3).toString());
                break;
        }
        hideStatusLayout();
        hideTemperatureHand(DataUtils.selectedChannelNumber);
        //System.out.println("Channel Change bleConnectStatus == " + bleConnectStatus);
        if (bleConnectStatus) {
            grill.setVisibility(View.VISIBLE);
            temperatureHand(currentTemperature, grillTemperature,
                    DataUtils.selectedChannelNumber);
            int size = getResources().getDimensionPixelSize(
                    R.dimen.barbecue_status_text_word_size);
            int pushSize = getResources().getDimensionPixelSize(
                    R.dimen.left_time_word_size);
            String splitValue = "\n";
            int defaultColor = getResources().getColor(
                    R.color.barbecue_status_text_color);
            int pushColor = Color.YELLOW;
            SpannableStringBuilder builder = null;
            menu_type.update(1);
            status_layout.setVisibility(View.VISIBLE);
            System.out.println("Channel Change to " + DataUtils.selectedChannelNumber + " paramer Status == " + status);
            if (status > 0) {
                int workStatus = paramer.getWorkStatus();
                if (workStatus == 0) {
                    action.setVisibility(View.GONE);
                    menu_type.update(1);
                } else {
                    action.setVisibility(View.VISIBLE);
                    if (workStatus == 1) {
                        action.setText(R.string.stop);
                        menu_type.update(0);
                    } else {
                        action.setText(R.string.start);
                        menu_type.update(1);
                    }
                }
                if (status == 1 || status == 3) {
                    if (status == 3) {
                        drawable = getResources().getDrawable(
                                R.drawable.temperature_status);
                    } else {
                        drawable = DataUtils.updateDrawable(status, type,
                                context, 1);
                    }
                    if (barbecue_status.getVisibility() == View.INVISIBLE) {
                        barbecue_status.setVisibility(View.GONE);
                    }
                    if (target.getVisibility() == View.INVISIBLE) {
                        target.setVisibility(View.VISIBLE);
                    }
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    barbecue_status.setCompoundDrawables(null, drawable, null,
                            null);
                    if (status == 1) {
                        barbecue_status.setText(getResources().getString(
                                R.string.single_barbecue_type,
                                DataUtils.selectGrillType(type, context),
                                degree));
                    } else {
                        barbecue_status.setText(R.string.temperature);
                    }
                    drawable = getResources().getDrawable(R.drawable.target);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    target.setCompoundDrawables(null, drawable, null, null);
                    target_temperature.setVisibility(View.VISIBLE);
                    float rotation = temperature2Rotation(targetTemperature);
                    if (rotation >= 210 && rotation < 270) {
                        target_temperature
                                .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                        .sin(rotation * Math.PI / 180)));
                        target_temperature
                                .setTranslationY((float) ((-bitmapHeigh / 2 + 32 * percentage) * (float) Math
                                        .cos(rotation * Math.PI / 180)));
                        target_temperature.setRotation(rotation);
                    } else if (rotation > 90 && rotation <= 150) {
                        target_temperature
                                .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                        .sin(rotation * Math.PI / 180)));
                        target_temperature
                                .setTranslationY((float) ((-bitmapHeigh / 2 + 32 * percentage) * (float) Math
                                        .cos(rotation * Math.PI / 180)));
                        target_temperature.setRotation(rotation);
                    } else {
                        target_temperature
                                .setTranslationX((float) ((bitmapWidth / 2 - 68 * percentage) * (float) Math
                                        .sin(rotation * Math.PI / 180)));
                        target_temperature
                                .setTranslationY((float) ((-bitmapHeigh / 2 + 68 * percentage) * (float) Math
                                        .cos(rotation * Math.PI / 180)));
                        target_temperature.setRotation(rotation);
                    }
                    String temperature = DataUtils
                            .displayTmeperature(targetTemperature);
                    String text = getResources()
                            .getString(
                                    R.string.single_barbecue_target_temperature,
                                    temperature,
                                    (DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
                                            : DataUtils.fahrenhite);
                    builder = DataUtils.setTextStyle(text, splitValue, size,
                            pushSize, defaultColor, pushColor);
                    target.setText(builder);
                    target.setVisibility(View.VISIBLE);
                } else {
                    drawable = getResources().getDrawable(
                            R.drawable.timer_status);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    barbecue_status.setCompoundDrawables(null, drawable, null,
                            null);
                    barbecue_status.setText(R.string.timer);
                    if (barbecue_status.getVisibility() == View.INVISIBLE) {
                        barbecue_status.setVisibility(View.GONE);
                    }
                    if (target.getVisibility() == View.VISIBLE) {
                        target.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                menu_type.update(1);
            }
        } else {
            menu_type.update(0);
            hideStatusLayout();
        }

    }

    // 收到低电数据
    private void batteryLowChange(int position) {
        if (DataUtils.selectedChannelNumber == position) {
            switch (position) {
                case 1:
                    if (BleConnectUtils.batteryLow1 == 1) {
                        battery.setVisibility(View.VISIBLE);
                        setFlickerAnimation(battery);
                    } else {
                        clearAnimation(battery);
                    }
                    break;
                case 2:
                    if (BleConnectUtils.batteryLow2 == 1) {
                        battery.setVisibility(View.VISIBLE);
                        setFlickerAnimation(battery);
                    } else {
                        clearAnimation(battery);
                    }
                    break;
                case 3:
                    if (BleConnectUtils.batteryLow3 == 1) {
                        battery.setVisibility(View.VISIBLE);
                        setFlickerAnimation(battery);
                    } else {
                        clearAnimation(battery);
                    }
                    break;
                case 4:
                    if (BleConnectUtils.batteryLow4 == 1) {
                        battery.setVisibility(View.VISIBLE);
                        setFlickerAnimation(battery);
                    } else {
                        clearAnimation(battery);
                    }
                    break;
            }
        }
    }

    // 收到数据时的温度指针显示判断
    private void temperatureHand(float currentTemperature,
                                 float grillTemperature, int position) {
        float displayCT = currentTemperature;
        if (currentTemperature < -40f) {
            currentTemperature = -40;
        }
        float rotation = 210 + currentTemperature +40;
        if (rotation >= 360) {
            rotation -= 360-60;
        }
        float grillRotation = 210 + grillTemperature+40;
        if (grillRotation >= 360) {
            grillRotation -= 360-60;
        }
        if (DataUtils.selectedChannelNumber == position) {
            if (current_temperature_tv.getVisibility() == View.GONE) {
                current_temperature_tv.setVisibility(View.VISIBLE);
            }
            if (current_temperature.getVisibility() == View.GONE) {
                current_temperature.setVisibility(View.VISIBLE);
            }
            if (grill.getVisibility() == View.INVISIBLE) {
                grill.setVisibility(View.VISIBLE);
            }
            if (grill_temperature.getVisibility() == View.GONE) {
                grill_temperature.setVisibility(View.VISIBLE);
            }
            String temperatureUnit = ((DataUtils.temperatureUnit == 0) ? DataUtils.centigrade
                    : DataUtils.fahrenhite);
            current_temperature_tv.setText(DataUtils
                    .displayTmeperature(displayCT) + temperatureUnit);
            if (currentTemperature >= DataUtils.highTemperature) {
                setFlickerAnimation(current_temperature_tv);
            } else {
                clearAnimation(current_temperature_tv);
            }
            if (rotation >= 210 && rotation < 270) {
                current_temperature
                        .setTranslationX((float) ((bitmapWidth / 2 - 128 * percentage) * (float) Math
                                .sin(rotation * Math.PI / 180)));
                current_temperature
                        .setTranslationY((float) ((-bitmapHeigh / 2 + 98 * percentage) * (float) Math
                                .cos(rotation * Math.PI / 180)));
                current_temperature.setRotation(rotation);
            } else if (rotation > 90 && rotation <= 150) {
                current_temperature
                        .setTranslationX((float) ((bitmapWidth / 2 - 128 * percentage) * (float) Math
                                .sin(rotation * Math.PI / 180)));
                current_temperature
                        .setTranslationY((float) ((-bitmapHeigh / 2 + 98 * percentage) * (float) Math
                                .cos(rotation * Math.PI / 180)));
                current_temperature.setRotation(rotation);
            } else {
                current_temperature
                        .setTranslationX((float) ((bitmapWidth / 2 - 128 * percentage) * (float) Math
                                .sin(rotation * Math.PI / 180)));
                current_temperature
                        .setTranslationY((float) ((-bitmapHeigh / 2 + 128 * percentage) * (float) Math
                                .cos(rotation * Math.PI / 180)));
                current_temperature.setRotation(rotation);

            }
            if (status_layout.getVisibility() == View.INVISIBLE) {
                status_layout.setVisibility(View.VISIBLE);
            }
            if (grill.getVisibility() == View.INVISIBLE) {
                grill.setVisibility(View.VISIBLE);
            }

            String text = getResources().getString(
                    R.string.barbecue_temperature,
                    DataUtils.displayTmeperature(grillTemperature) + "",
                    temperatureUnit).replace(" ", "\n");
            int size = getResources().getDimensionPixelSize(
                    R.dimen.barbecue_status_text_word_size);
            int pushSize = getResources().getDimensionPixelSize(
                    R.dimen.left_time_word_size);
            String splitValue = "\n";
            int defaultColor = getResources().getColor(
                    R.color.barbecue_status_text_color);
            int pushColor = Color.WHITE;
            SpannableStringBuilder builder = DataUtils.setTextStyle(text,
                    splitValue, size, pushSize, defaultColor, pushColor);
            grill.setText(builder);
            if (grillTemperature >= DataUtils.highGrillTemperature) {
                setFlickerAnimation(grill);
            } else {
                clearAnimation(grill);
            }
            if (grillRotation >= 210 && grillRotation < 270) {
                grill_temperature
                        .setTranslationX((float) ((bitmapWidth / 2 - 128 * percentage) * (float) Math
                                .sin(grillRotation * Math.PI / 180)));
                grill_temperature
                        .setTranslationY((float) ((-bitmapHeigh / 2 + 98 * percentage) * (float) Math
                                .cos(grillRotation * Math.PI / 180)));
                grill_temperature.setRotation(grillRotation);
            } else if (grillRotation > 90 && grillRotation <= 150) {
                grill_temperature
                        .setTranslationX((float) ((bitmapWidth / 2 - 128 * percentage) * (float) Math
                                .sin(grillRotation * Math.PI / 180)));
                grill_temperature
                        .setTranslationY((float) ((-bitmapHeigh / 2 + 98 * percentage) * (float) Math
                                .cos(grillRotation * Math.PI / 180)));
                grill_temperature.setRotation(grillRotation);
            } else {
                grill_temperature
                        .setTranslationX((float) ((bitmapWidth / 2 - 128 * percentage) * (float) Math
                                .sin(grillRotation * Math.PI / 180)));
                grill_temperature
                        .setTranslationY((float) ((-bitmapHeigh / 2 + 128 * percentage) * (float) Math
                                .cos(grillRotation * Math.PI / 180)));
                grill_temperature.setRotation(grillRotation);

            }

        }
    }

    //初始化文本視圖值
    private void initTextViewValue() {
        left_time_label.setText(R.string.left_time);
        current_temperature_label.setText(R.string.current_temperature);
        left_time_tv.setText("_ _ _ _");
        current_temperature_tv.setText("_ _ _ _");
        postServerUrl = getResources().getString(R.string.postServerUrl);
    }

    private void bleStopWork() {
        byte[] bb = new byte[4];
        bb[0] = (byte) 13;
        BleConnectUtils.connectMap.get(DataUtils.selectedChannelNumber)
                .writeLlsAlertLevel(bb);
    }

    private boolean bleStartWrok(BarbecueParamer paramer) {
        byte[] bb = new byte[4];
        float temperature = 0;
        if (paramer.getStatus() == 2) {
            int hour = paramer.getHour();
            int min = paramer.getMin();
            bb[0] = (byte) 11;
            byte[] temp = Hex.shortToByteArray((short) (hour * 3600 + min * 60));
            bb[1] = temp[1];
            bb[2] = temp[0];
        } else if (paramer.getStatus() == 1) {
            temperature = DataUtils.saveTmeperature(String.valueOf(paramer
                    .getTemperature()));
            bb[0] = (byte) 10;
            int data = DataUtils.bleTmeperature(temperature);
            byte[] temp = Hex.shortToByteArray((short) data);
            bb[1] = temp[1];
            bb[2] = temp[0];
        } else if (paramer.getStatus() == 3) {
            temperature = paramer.getTemperature();
            DecimalFormat decimalFormat = new DecimalFormat(".0");
            Float temperatureValue = Float.parseFloat(decimalFormat.format(temperature));
            int data = DataUtils.bleTmeperature(temperatureValue);
            byte[] tempByte = Hex.shortToByteArray((short) data);
            bb[0] = (byte) 12;
            bb[1] = tempByte[1];
            bb[2] = tempByte[0];
        }

        return BleConnectUtils.connectMap.get(DataUtils.selectedChannelNumber)
                .writeLlsAlertLevel(bb);
    }

    ProgressDialog mProgressDialog;

    @Override
    protected Dialog onCreateDialog(int id) {
        mProgressDialog = new ProgressDialog(ConnectActivity.this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        return mProgressDialog;
    }

    class WaitAutoConnectTask extends AsyncTask<String, Void, Void> {
        boolean status = false;
        String albumName = "";

        protected void onPreExecute() {
            showDialog(0);
        }

        @Override
        protected Void doInBackground(String... params) {
            while (AutoConnectUtils.getInstance().connectionStatus == 1) {
                SystemClock.sleep(50);
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            removeDialog(0);
            if (BleConnectUtils.oneStatus || BleConnectUtils.twoStatus
                    || BleConnectUtils.threeStatus
                    || BleConnectUtils.fourStatus) {
                connect_status.setImageResource(R.drawable.ble_status);
            } else {
                connect_status.setImageResource(R.drawable.disconnect);
            }
            showBleConnectDialog();
        }
    }

    private void init() {

        status_layout = (LinearLayout) findViewById(R.id.status_layout);
        action = (Button) findViewById(R.id.action);
        left_time_label = (TextView) findViewById(R.id.left_time_label);
        current_temperature_label = (TextView) findViewById(R.id.current_temperature_label);
        lan_status = findViewById(R.id.lan_status);
        lan_status.setImageResource(R.drawable.button_red);

        action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("action click ==========  selected channel " + DataUtils.selectedChannelNumber);
                //target.setText("");
                //barbecue_status.setVisibility(View.INVISIBLE);
                //target.setVisibility(View.INVISIBLE);
                //action.setVisibility(View.INVISIBLE);
                //target_temperature.setVisibility(View.INVISIBLE);
                BarbecueParamer paramer = BleConnectUtils.uiMap.get(DataUtils.selectedChannelNumber);
                System.out.println("get BLE work status = " + paramer.getWorkStatus());

                switch (DataUtils.selectedChannelNumber) {
                    case 1:
                        if (paramer == null) {
                            return;
                        }
                        if (paramer.getWorkStatus() == 1) {
                            action.setText(R.string.start);
                            isDataSent = true;
                            bleStopWork();
                            menu_type.update(1);
                            BleConnectUtils.connectMap.get(1).resetInit();

                            //left_time_tv.setText("_ _ _ _");

                            paramer.setWorkStatus(2);
                            paramer.setStatus(0);
                            paramer.setTemperature(-1);
                            //BleConnectUtils.connectMap.get(1).setTargetTemperature(-1);
                            BleConnectUtils.connectMap.get(1).startFlag = false;
                            if (BleConnectUtils.cutTimerTask1 != null) {
                                BleConnectUtils.cutTimerTask1.cancel();
                                BleConnectUtils.timer1.cancel();
                                BleConnectUtils.cutTimerTask1 = null;
                                BleConnectUtils.timer1 = null;
                                //
                            }
                            //hideTemperatureHand(1);
                            //hideStatusLayout();
//                            target.setText("");
//                            barbecue_status.setVisibility(View.INVISIBLE);
//                            target.setVisibility(View.INVISIBLE);
//                            action.setVisibility(View.INVISIBLE);
//                            target_temperature.setVisibility(View.INVISIBLE);
                        } else if (paramer.getWorkStatus() == 2) {
                            action.setText(R.string.stop);
                            isDataSent = false;
                            paramer.setWorkStatus(1);
                            if (bleStartWrok(paramer)) {
                                //left_time_tv.setText(R.string.cooking);
                            }
                            menu_type.update(0);
                            BleConnectUtils.connectMap.get(1).startFlag = true;
                            if (paramer.getStatus() == 2) {
                                if (BleConnectUtils.cutTimerTask1 == null) {
                                    leftTime1 = paramer.getHour() * 3600
                                            + paramer.getMin() * 60;
                                    BleConnectUtils.timer1 = new Timer();
                                    BleConnectUtils.cutTimerTask1 = new CutTimerTask(
                                            1, leftTime1, context);
                                    BleConnectUtils.timer1.schedule(
                                            BleConnectUtils.cutTimerTask1, 1000,
                                            1000);
                                }
                            }

                        }
                        break;
                    case 2:
                        if (paramer == null) {
                            return;
                        }
                        if (paramer.getWorkStatus() == 1) {
                            action.setText(R.string.start);
                            bleStopWork();
                            menu_type.update(1);
                            BleConnectUtils.connectMap.get(2).resetInit();
                            //left_time_tv.setText("_ _ _ _");
                            paramer.setWorkStatus(2);
                            paramer.setStatus(0);
                            paramer.setTemperature(-1);
                            //BleConnectUtils.connectMap.get(2).setTargetTemperature(-1);
                            BleConnectUtils.connectMap.get(2).startFlag = false;
                            if (BleConnectUtils.cutTimerTask2 != null) {
                                BleConnectUtils.cutTimerTask2.cancel();
                                BleConnectUtils.timer2.cancel();
                                BleConnectUtils.cutTimerTask2 = null;
                                BleConnectUtils.timer2 = null;
                                //paramer.setTemperature(-1);
                            }
                            //hideTemperatureHand(2);
                            //hideStatusLayout();
//                            target.setText("");
//                            barbecue_status.setVisibility(View.INVISIBLE);
//                            target.setVisibility(View.INVISIBLE);
//                            action.setVisibility(View.INVISIBLE);
//                            target_temperature.setVisibility(View.INVISIBLE);
                        } else if (paramer.getWorkStatus() == 2) {
                            action.setText(R.string.stop);
                            paramer.setWorkStatus(1);
                            if (bleStartWrok(paramer)) {
                                //left_time_tv.setText(R.string.cooking);
                            }
                            menu_type.update(0);
                            BleConnectUtils.connectMap.get(2).startFlag = true;
                            if (paramer.getStatus() == 2) {
                                leftTime2 = paramer.getHour() * 3600
                                        + paramer.getMin() * 60;
                                BleConnectUtils.cutTimerTask2 = new CutTimerTask(2,
                                        leftTime2, context);
                                BleConnectUtils.timer2 = new Timer();
                                BleConnectUtils.timer2.schedule(
                                        BleConnectUtils.cutTimerTask2, 1000, 1000);
                            }
                        }
                        break;
                    case 3:
                        if (paramer == null) {
                            return;
                        }
                        if (paramer.getWorkStatus() == 1) {
                            action.setText(R.string.start);
                            bleStopWork();
                            menu_type.update(1);
                            BleConnectUtils.connectMap.get(3).resetInit();
                            //left_time_tv.setText("_ _ _ _");
                            paramer.setWorkStatus(2);
                            paramer.setStatus(0);
                            paramer.setTemperature(-1);
                            //BleConnectUtils.connectMap.get(3).setTargetTemperature(-1);
                            BleConnectUtils.connectMap.get(3).startFlag = false;
                            if (BleConnectUtils.cutTimerTask3 != null) {
                                BleConnectUtils.cutTimerTask3.cancel();
                                BleConnectUtils.timer3.cancel();
                                BleConnectUtils.cutTimerTask3 = null;
                                BleConnectUtils.timer3 = null;
                                //paramer.setTemperature(-1);
                            }
                            //hideTemperatureHand(3);
                            //hideStatusLayout();
//                            target.setText("");
//                            barbecue_status.setVisibility(View.INVISIBLE);
//                            target.setVisibility(View.INVISIBLE);
//                            action.setVisibility(View.INVISIBLE);
//                            target_temperature.setVisibility(View.INVISIBLE);
                        } else if (paramer.getWorkStatus() == 2) {
                            action.setText(R.string.stop);
                            paramer.setWorkStatus(1);
                            if (bleStartWrok(paramer)) {
                                //left_time_tv.setText(R.string.cooking);
                            }
                            menu_type.update(1);
                            BleConnectUtils.connectMap.get(3).startFlag = true;
                            BleConnectUtils.timer3 = new Timer();
                            if (paramer.getStatus() == 2) {
                                leftTime3 = paramer.getHour() * 3600
                                        + paramer.getMin() * 60;
                                BleConnectUtils.cutTimerTask3 = new CutTimerTask(3,
                                        leftTime3, context);
                                BleConnectUtils.timer3 = new Timer();
                                BleConnectUtils.timer3.schedule(
                                        BleConnectUtils.cutTimerTask3, 1000, 1000);
                            }
                        }
                        break;
                    case 4:
                        if (paramer == null) {
                            return;
                        }
                        if (paramer.getWorkStatus() == 1) {
                            action.setText(R.string.start);
                            bleStopWork();
                            menu_type.update(1);
                            BleConnectUtils.connectMap.get(4).resetInit();
                            //left_time_tv.setText("_ _ _ _");
                            paramer.setWorkStatus(2);
                            paramer.setStatus(0);
                            paramer.setTemperature(-1);
                            //BleConnectUtils.connectMap.get(4).setTargetTemperature(-1);
                            BleConnectUtils.connectMap.get(4).startFlag = false;
                            if (BleConnectUtils.cutTimerTask4 != null) {
                                BleConnectUtils.cutTimerTask4.cancel();
                                BleConnectUtils.timer4.cancel();
                                BleConnectUtils.cutTimerTask4 = null;
                                BleConnectUtils.timer4 = null;
                                //paramer.setTemperature(-1);
                            }
                            //hideTemperatureHand(4);
                            //hideStatusLayout();
//                            target.setText("");
//                            barbecue_status.setVisibility(View.INVISIBLE);
//                            target.setVisibility(View.INVISIBLE);
//                            action.setVisibility(View.INVISIBLE);
//                            target_temperature.setVisibility(View.INVISIBLE);
                        } else if (paramer.getWorkStatus() == 2) {
                            action.setText(R.string.stop);
                            paramer.setWorkStatus(1);
                            if (bleStartWrok(paramer)) {
                                //left_time_tv.setText(R.string.cooking);
                            }
                            menu_type.update(0);
                            BleConnectUtils.connectMap.get(4).startFlag = true;
                            BleConnectUtils.timer4 = new Timer();
                            if (paramer.getStatus() == 2) {
                                leftTime4 = paramer.getHour() * 3600
                                        + paramer.getMin() * 60;
                                BleConnectUtils.cutTimerTask4 = new CutTimerTask(4,
                                        leftTime4, context);
                                BleConnectUtils.timer4 = new Timer();
                                BleConnectUtils.timer4.schedule(
                                        BleConnectUtils.cutTimerTask4, 1000, 1000);
                            }
                        }
                        break;
                }


            }
        });
        one = (ImageView) findViewById(R.id.one);
        two = (ImageView) findViewById(R.id.two);
        three = (ImageView) findViewById(R.id.three);
        four = (ImageView) findViewById(R.id.four);
        one.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BleConnectUtils.oneStatus) {
                    DataUtils.selectedChannelNumber = 1;
                    changeChannelUpdateUI();
                }
            }
        });
        two.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BleConnectUtils.twoStatus) {
                    DataUtils.selectedChannelNumber = 2;
                    changeChannelUpdateUI();
                }
            }
        });
        three.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BleConnectUtils.threeStatus) {
                    DataUtils.selectedChannelNumber = 3;
                    changeChannelUpdateUI();
                }
            }
        });

        four.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BleConnectUtils.fourStatus) {
                    DataUtils.selectedChannelNumber = 4;
                    changeChannelUpdateUI();
                }
            }
        });

        connect_status = (ImageView) findViewById(R.id.connect_status);
        connect_status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
                    //判断是否具有权限
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //判断是否需要向用户解释为什么需要申请该权限
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ConnectActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {

                        }
                        //请求权限
                        ActivityCompat.requestPermissions(ConnectActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_ACCESS_COARSE_LOCATION);
                        return;
                    } else {
                        checkLocationPermission();
                    }
                } else {
                    checkPermissions();
                }
                connect_status.setClickable(false);
                AutoConnectUtils.getInstance().autoConnectStatus = false;
                AutoConnectUtils.getInstance().cleartargetList();
                if (AutoConnectUtils.getInstance().connectionStatus == 1) {
                    new WaitAutoConnectTask().execute();
                } else {
                    if (BleConnectUtils.oneStatus || BleConnectUtils.twoStatus
                            || BleConnectUtils.threeStatus
                            || BleConnectUtils.fourStatus) {
                        connect_status.setImageResource(R.drawable.ble_status);
                    } else {
                        connect_status.setImageResource(R.drawable.disconnect);
                    }
                    showBleConnectDialog();
                }
            }

            /*
             * if (gatt != null) { if (bleScanStatus) { if
             * (BleConnectUtils.oneStatus || BleConnectUtils.twoStatus ||
             * BleConnectUtils.threeStatus || BleConnectUtils.fourStatus) {
             * connect_status .setImageResource(R.drawable.ble_status); }
             * else { connect_status
             * .setImageResource(R.drawable.disconnect); }
             * showBleConnectDialog(); } else { Toast.makeText(context,
             * "loading.....", Toast.LENGTH_LONG).show(); } } else { if
             * (BleConnectUtils.oneStatus || BleConnectUtils.twoStatus ||
             * BleConnectUtils.threeStatus || BleConnectUtils.fourStatus) {
             * connect_status.setImageResource(R.drawable.ble_status); }
             * else {
             * connect_status.setImageResource(R.drawable.disconnect); }
             * showBleConnectDialog(); }
             */
        });
        menu_type = (SettingMenuImageView) findViewById(R.id.menu);
        menu_type.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (menu_type.getType() == 0) {
                            return true;
                        }
                        Bitmap bitmap = menu_type.getmBitmap1();
                        float x = event.getX();
                        float y = event.getY();
                        if (x < bitmap.getWidth() && y < bitmap.getHeight()) {
                            boolean menuClickable = false;
                            switch (DataUtils.selectedChannelNumber) {
                                case 1:
                                    menuClickable = BleConnectUtils.oneStatus;
                                    break;
                                case 2:
                                    menuClickable = BleConnectUtils.twoStatus;
                                    break;
                                case 3:
                                    menuClickable = BleConnectUtils.threeStatus;
                                    break;
                                case 4:
                                    menuClickable = BleConnectUtils.fourStatus;
                                    break;
                            }
                            if (menuClickable) {
                                Intent intent = new Intent(ConnectActivity.this,
                                        TemperatureActivity.class);
                                intent.putExtra("position", DataUtils.selectedChannelNumber);
                                startActivity(intent);
                            }
                        }
                        return true;
                }
                return false;
            }
        });
        setting = (ImageView) findViewById(R.id.setting);
        setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectActivity.this,
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
        all_product_layout = (LinearLayout) findViewById(R.id.all_product_layout);
        all_product_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectActivity.this,
                        AllProbesActivity.class);
                intent.putExtra("position", DataUtils.selectedChannelNumber);
                startActivity(intent);
            }
        });
        all_product_layout.setClickable(false);
        barbecue_status = (TextView) findViewById(R.id.barbecue_status);
        target = (TextView) findViewById(R.id.target);
        grill = (TextView) findViewById(R.id.grill);
        target_temperature = (ImageView) findViewById(R.id.target_temperature);
        grill_temperature = (ImageView) findViewById(R.id.grill_temperature);
        current_temperature = (ImageView) findViewById(R.id.current_temperature);
        targetBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.target_temperature);
        targetBitmap = adjustPhotoRotation(targetBitmap, 180);
        target_temperature.setImageBitmap(targetBitmap);
        start_temperature = (TextView) findViewById(R.id.start_temperature);
        end_temperature = (TextView) findViewById(R.id.end_temperature);
        displayMinMaxTemperature();
        left_time_tv = (TextView) findViewById(R.id.left_time_tv);
        current_temperature_tv = (TextView) findViewById(R.id.current_temperature_tv);
        battery = (ImageView) findViewById(R.id.battery);
        name_label = (TextView) findViewById(R.id.bbq_name_label);
        webView_data = findViewById(R.id.web);
        webView_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://wms.ysit.com.tw:6980/wms-v3/"));
                startActivity(i);
            }
        });
        if (isNetworkAvailable())
            http_server_OK();

    }

    //顯示最低最高溫度
    private void displayMinMaxTemperature() {
        switch (DataUtils.temperatureUnit) {
            case 0:
                start_temperature.setText(R.string.min_centigrade_temperature);
                end_temperature.setText(R.string.max_centigrade_temperature);
                break;
            case 1:
                start_temperature.setText(R.string.min_fahrenhite_temperature);
                end_temperature.setText(R.string.max_fahrenhite_temperature);
                break;
        }
    }

    //調整照片旋轉
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

    //銷毀
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        unregisterReceiver(mReceiver);
        unregisterReceiver(recipeUpdateUIReceiver);
        unregisterReceiver(timerUIReceiver);
        unregisterReceiver(temperatureUpdateUIReceiver);
        unregisterReceiver(batteryLowReceiver);
        unregisterReceiver(updateRingReceiver);
        unregisterReceiver(bleDataReceiver);
        unregisterReceiver(alarmReceiver);
        unregisterReceiver(addBLEReceiver);
        unregisterReceiver(bleWorkStatus);
        unregisterReceiver(targetBleMacReceiver);
        unregisterReceiver(allProbesAlarmClickReceiver);
        DataUtils.activityMap.remove("ConnectActivity");
        //LogcatHelper.getInstance(context).stop();
        unregisterReceiver(networkChangeReceiver);
    }

    //更新連接對話框
    private void updateBleConnectDialog(int scanPosition) {
        if (dialog != null && dialog.isShowing()) {
            dialogTitle.setText(((BleConnectUtils.connectList == null) ? 0
                    : BleConnectUtils.connectList.size()) + "/4");
            try {
                adapter.notifyDataSetChanged();
                mScan.devices.remove(scanPosition);
                deviceAdapter.setmDevices(mScan.devices);
                deviceAdapter.notifyDataSetChanged();
            } catch (Exception e) {

            }
        }
    }

    //顯示連接對話框
    @SuppressLint("NewApi")
    private void showBleConnectDialog() {
        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.ble_connect);
        dialogTitle = (TextView) dialog.findViewById(R.id.title);
        dialogTitle.setText(((BleConnectUtils.connectList == null) ? 0
                : BleConnectUtils.connectList.size()) + "/4");
        dialog_ble_list = (ListView) dialog.findViewById(R.id.ble_list);
        loading = (LinearLayout) dialog.findViewById(R.id.loading);
        dialog_connected_list = (ListView) dialog
                .findViewById(R.id.connected_list);
        if (BleConnectUtils.connectList.size() == 4) {
            dialog_ble_list.setEnabled(false);
        } else {
            dialog_ble_list.setEnabled(true);
        }
        dialog_connected_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                final Dialog dialog = new Dialog(context, R.style.dialog);
                dialog.setContentView(R.layout.ble_disconnect);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                String name = ((TextView) arg1.findViewById(R.id.device_name))
                        .getText().toString();
                final int removePosition = Integer.parseInt(((TextView) arg1
                        .findViewById(R.id.tv_position)).getText().toString());
                title.setText(getResources().getString(R.string.disconnect_msg,
                        name));
                FrameLayout no = (FrameLayout) dialog.findViewById(R.id.no);
                FrameLayout yes = (FrameLayout) dialog.findViewById(R.id.yes);
                no.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        loading.setVisibility(View.VISIBLE);
                        BleGatt bleGatt = BleConnectUtils.connectMap
                                .remove(removePosition);
                        handDisconnectPosition = removePosition;
                        AutoConnectUtils.getInstance().removeTargetMac(
                                bleGatt.mac);
                        bleGatt.disconnect();
                        BleConnectUtils.connectList.remove(arg2);
                        adapter.notifyDataSetChanged();
                        mScan.clear();
                        dialogTitle.setText(((BleConnectUtils.connectList == null) ? 0
                                : BleConnectUtils.connectList.size())
                                + "/4");
                        //bbqn.remove(arg2);
                        if (BleConnectUtils.connectList.size() <= 0) {
                            left_time_tv.setText("_ _ _ _");
                            name_label.setText("_ _ _ _");
                        }
                    }
                });
                dialog.setCancelable(false);
                dialog.show();

            }
        });
        adapter = new ConnectedDeviceAdapter(context);
        dialog_connected_list.setAdapter(adapter);
        dialog_connected_list.addFooterView(new ViewStub(this));
        dialog_ble_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long arg3) {
                TextView mac_tv = (TextView) arg1
                        .findViewById(R.id.device_addr);
                TextView name_tv = arg1.findViewById(R.id.device_name);
                if (mac_tv == null) {
                    return;
                }
                final String mac = mac_tv.getText().toString();

                BbqName = name_tv.getText().toString().replace("BBQ ProbeE ", "");
                //Log.i("Test" , "BBQ Name = " + name_tv.getText().toString() + "   POS: " + position);
                //if (bbqn.isEmpty()){
                //    bbqn.add(BbqName);
                //}
                //else{
                //    if (!bbqn.contains(BbqName))
                //        bbqn.add(BbqName);
                //}

                //Log.i("Test", bbqn.toString());


                if (BleConnectUtils.connectList.size() < 4) {
                    loading.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int ableConnectPosition = DataUtils.selectedChannelNumber;
                            if (DataUtils.selectedChannelNumber == 1) {
                                if (BleConnectUtils.oneStatus) {
                                    if (!BleConnectUtils.twoStatus) {
                                        ableConnectPosition = 2;
                                    } else if (!BleConnectUtils.threeStatus) {
                                        ableConnectPosition = 3;
                                    } else if (!BleConnectUtils.fourStatus) {
                                        ableConnectPosition = 4;
                                    }
                                }
                            } else if (DataUtils.selectedChannelNumber == 2) {
                                if (BleConnectUtils.twoStatus) {
                                    if (!BleConnectUtils.oneStatus) {
                                        ableConnectPosition = 1;
                                    } else if (!BleConnectUtils.threeStatus) {
                                        ableConnectPosition = 3;
                                    } else if (!BleConnectUtils.fourStatus) {
                                        ableConnectPosition = 4;
                                    }
                                }
                            } else if (DataUtils.selectedChannelNumber == 3) {
                                if (BleConnectUtils.threeStatus) {
                                    if (!BleConnectUtils.oneStatus) {
                                        ableConnectPosition = 1;
                                    } else if (!BleConnectUtils.twoStatus) {
                                        ableConnectPosition = 2;
                                    } else if (!BleConnectUtils.fourStatus) {
                                        ableConnectPosition = 4;
                                    }
                                }
                            } else if (DataUtils.selectedChannelNumber == 4) {
                                if (BleConnectUtils.fourStatus) {
                                    if (!BleConnectUtils.oneStatus) {
                                        ableConnectPosition = 1;
                                    } else if (!BleConnectUtils.twoStatus) {
                                        ableConnectPosition = 2;
                                    } else if (!BleConnectUtils.threeStatus) {
                                        ableConnectPosition = 3;
                                    }
                                }
                            }
                            BluetoothDevice device = mBluetoothAdapter
                                    .getRemoteDevice(mac);
                            gatt = new BleGatt();
                            gatt.setWork(false);
                            gatt.setPosition(position);
                            gatt.init(ConnectActivity.this, ableConnectPosition);
                            gatt.connect(device);
                            gatt.setBBQName(BbqName);
                            gatt.setIP(netIp);
                            gatt.setIMEI(mDeviceIMEI);
                        }
                    }).start();
                } else {
                    Toast.makeText(context, "最多连接4个设备", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        Button back = (Button) dialog.findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                connect_status.setClickable(true);
                Log.i("TEST", " connect list size " + BleConnectUtils.connectList.size());
//                if (BleConnectUtils.connectList.size() ==0 ) {
//                    setting.setClickable(true);
//                    left_time_tv.setText("_ _ _ _");
//                }
//                else
//                    setting.setClickable(false);
                BleScanUtils.getInstance(mScan, context).stop();
                dialog.dismiss();
                AutoConnectUtils.getInstance().autoConnectStatus = true;
                resetAutoConnectList();

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    //重置自動連接列表
    public void resetAutoConnectList() {
        BleGatt gatt;
        for (int i = 1; i <= 4; i++) {
            gatt = BleConnectUtils.connectMap.get(i);
            if (gatt != null) {
                if (gatt.getmGatt() != null) {
                    AutoConnectUtils.getInstance().addAutoConnectList(gatt.mac,
                            i, true);
                } else {
                    AutoConnectUtils.getInstance().addAutoConnectList(gatt.mac,
                            i, false);
                }
            }
        }
    }

    TimerTask timerTask = null;
    int counter = 120;

    public void timeoutClear() {
        BleGatt gatt;
        for (int i = 1; i <= 4; i++) {
            gatt = BleConnectUtils.connectMap.get(i);
            if (gatt != null) {
                if (gatt.getmGatt() == null) {
                    BleConnectUtils.connectMap.get(i).stopMyTimeTask();
                    BleConnectUtils.connectMap.remove(i);
                    AutoConnectUtils.getInstance().removeTargetMac(gatt.mac);
                }
            }
        }
    }

    public void test1() {
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                counter--;
                if (counter <= 0
                        || !AutoConnectUtils.getInstance().autoConnectStatus) {
                    cancel();
                    timeoutClear();
                    BleScanUtils.getInstance(mScan, context).stop();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    //活動結果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
            dialog.dismiss();
            return;
        }
        if (requestCode == 10009) {
            if (mBluetoothAdapter == null) {
                bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(context, "bluetooth_not_supported",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }
                mScan.init(ConnectActivity.this, mBluetoothAdapter);
                Log.e("init Scan", "============" + ((mBluetoothAdapter == null) ? "true" : "false"));
            }
            mScan.clear();
            BleScanUtils.getInstance(mScan, context).run();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setFlickerAnimation(View iv, int position) {
        switch (position) {
            case 1:
                if (animation1 == null) {
                    animation1 = new AlphaAnimation(1, 0); // Change alpha from
                    animation1.setDuration(500); // duration - half a second
                    animation1.setInterpolator(new LinearInterpolator()); // do not
                    animation1.setRepeatCount(Animation.INFINITE); // Repeat
                    animation1.setRepeatMode(Animation.REVERSE); //
                    iv.setAnimation(animation1);
                }
                break;
            case 2:
                if (animation2 == null) {
                    animation2 = new AlphaAnimation(1, 0); // Change alpha from
                    animation2.setDuration(500); // duration - half a second
                    animation2.setInterpolator(new LinearInterpolator()); // do not
                    animation2.setRepeatCount(Animation.INFINITE); // Repeat
                    animation2.setRepeatMode(Animation.REVERSE); //
                    iv.setAnimation(animation2);
                }
                break;
            case 3:
                if (animation3 == null) {
                    animation3 = new AlphaAnimation(1, 0); // Change alpha from
                    animation3.setDuration(500); // duration - half a second
                    animation3.setInterpolator(new LinearInterpolator()); // do not
                    animation3.setRepeatCount(Animation.INFINITE); // Repeat
                    animation3.setRepeatMode(Animation.REVERSE); //
                    iv.setAnimation(animation3);
                }
                break;
            case 4:
                if (animation4 == null) {
                    animation4 = new AlphaAnimation(1, 0); // Change alpha from
                    animation4.setDuration(500); // duration - half a second
                    animation4.setInterpolator(new LinearInterpolator()); // do not
                    animation4.setRepeatCount(Animation.INFINITE); // Repeat
                    animation4.setRepeatMode(Animation.REVERSE); //
                    iv.setAnimation(animation4);
                }
                break;

        }
    }

    //設置閃爍動畫
    private void setFlickerAnimation(View tv) {
        if (tv.getTag().equals("grill")) {
            if (grillAnimation == null) {
                grillAnimation = new AlphaAnimation(1, 0); // Change alpha from
                grillAnimation.setDuration(500); // duration - half a second
                grillAnimation.setInterpolator(new LinearInterpolator()); // do
                grillAnimation.setRepeatCount(Animation.INFINITE); // Repeat
                grillAnimation.setRepeatMode(Animation.REVERSE); //
                tv.setAnimation(grillAnimation);
            }
        } else if (tv.getTag().equals("current")) {
            if (currentAnimation == null) {
                currentAnimation = new AlphaAnimation(1, 0); // Change alpha
                currentAnimation.setDuration(500); // duration - half a second
                currentAnimation.setInterpolator(new LinearInterpolator()); // do
                currentAnimation.setRepeatCount(Animation.INFINITE); // Repeat
                currentAnimation.setRepeatMode(Animation.REVERSE); //
                tv.setAnimation(currentAnimation);
            }
        } else if (tv.getTag().equals("battery")) {
            if (batteryAnimation == null) {
                batteryAnimation = new AlphaAnimation(1, 0); // Change alpha
                batteryAnimation.setDuration(500); // duration - half a second
                batteryAnimation.setInterpolator(new LinearInterpolator()); // do
                batteryAnimation.setRepeatCount(Animation.INFINITE); // Repeat
                batteryAnimation.setRepeatMode(Animation.REVERSE); //
                tv.setAnimation(batteryAnimation);
            }
        }
    }

    //清除動畫
    private void clearAnimation(View tv) {
        if (tv.getTag().equals("grill")) {
            if (grillAnimation != null) {
                tv.clearAnimation();
                grillAnimation = null;
            }
        } else if (tv.getTag().equals("current")) {
            if (currentAnimation != null) {
                tv.clearAnimation();
                currentAnimation = null;
            }
        } else if (tv.getTag().equals("battery")) {
            if (batteryAnimation != null) {
                tv.clearAnimation();
                tv.setVisibility(View.GONE);
                batteryAnimation = null;
            }
        }
    }

    //暫停
    @Override
    protected void onPause() {
        super.onPause();
        if (DataUtils.activityMap.get("AllProbesActivity") != null) {
            DataUtils.activityMap.remove("AllProbesActivitys");
        }
        DataUtils.activityMap.put("ConnectActivity",
                "fmgtech.grillprobee.barbecue.ConnectActivity");
    }


    /*
     * PowerManager pm =(PowerManager)getSystemService(Context.POWER_SERVICE);
     * PowerManager.WakeLock wl
     * =pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"My Tag"); wl.acquire();
     * wl.release();
     */
    //檢查添加位置
    private boolean checkAddPosition(int position, ArrayList<Integer> list) {
        int size = list.size();
        boolean flag = false;
        for (int i = 0; i < size; i++) {
            if (position == list.get(i)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    //結束時間
    private void endTime(int position) {
        BarbecueParamer paramer = BleConnectUtils.uiMap
                .get(position);
        if (paramer == null) {
            return;
        }
        switch (position) {
            case 1:
                if (paramer.getWorkStatus() == 1) {
                    action.setText(R.string.start);
                    bleStopWork();
                    menu_type.update(1);
                    BleConnectUtils.connectMap.get(1).resetInit();
                    left_time_tv.setText("_ _ _ _");
                    paramer.setWorkStatus(2);
                    if (BleConnectUtils.cutTimerTask1 != null) {
                        BleConnectUtils.cutTimerTask1.cancel();
                        BleConnectUtils.timer1.cancel();
                        BleConnectUtils.cutTimerTask1 = null;
                        BleConnectUtils.timer1 = null;
                    }
                }
                break;
            case 2:
                if (paramer.getWorkStatus() == 1) {
                    action.setText(R.string.start);
                    bleStopWork();
                    menu_type.update(1);
                    BleConnectUtils.connectMap.get(2).resetInit();
                    left_time_tv.setText("_ _ _ _");
                    paramer.setWorkStatus(2);
                    if (BleConnectUtils.cutTimerTask2 != null) {
                        BleConnectUtils.cutTimerTask2.cancel();
                        BleConnectUtils.timer2.cancel();
                        BleConnectUtils.cutTimerTask2 = null;
                        BleConnectUtils.timer2 = null;
                    }
                }
                break;
            case 3:
                if (paramer.getWorkStatus() == 1) {
                    action.setText(R.string.start);
                    bleStopWork();
                    menu_type.update(1);
                    BleConnectUtils.connectMap.get(3).resetInit();
                    left_time_tv.setText("_ _ _ _");
                    paramer.setWorkStatus(2);
                    if (BleConnectUtils.cutTimerTask3 != null) {
                        BleConnectUtils.cutTimerTask3.cancel();
                        BleConnectUtils.timer3.cancel();
                        BleConnectUtils.cutTimerTask3 = null;
                        BleConnectUtils.timer3 = null;
                    }
                }
                break;
            case 4:
                if (paramer.getWorkStatus() == 1) {
                    action.setText(R.string.start);
                    bleStopWork();
                    menu_type.update(1);
                    BleConnectUtils.connectMap.get(4).resetInit();
                    left_time_tv.setText("_ _ _ _");
                    paramer.setWorkStatus(2);
                    if (BleConnectUtils.cutTimerTask4 != null) {
                        BleConnectUtils.cutTimerTask4.cancel();
                        BleConnectUtils.timer4.cancel();
                        BleConnectUtils.cutTimerTask4 = null;
                        BleConnectUtils.timer4 = null;
                    }
                }
                break;
        }
    }

    private final BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            final int position = intent.getIntExtra("position", 0);
            if (actionStr.equals("showAlarmDialogInMainPager")) {
                if (checkAddPosition(position, alarmChannel)) {
                    alarmChannel.add(position);
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
                } else {
                    if (alarmDialog != null && alarmDialog.isShowing()) {
                        return;
                    }
                }
            } else if (actionStr.equals("showDisconnectAlarmDialogInMainPager")) {
                if (position == handDisconnectPosition) {
                    handDisconnectPosition = -1;
                    return;
                }
                int size = BleConnectUtils.connectList.size();
                DeviceRecord deviceRecord = null;
                for (int i = 0; i < size; i++) {
                    deviceRecord = BleConnectUtils.connectList.get(i);
                    if (deviceRecord.getPosition() == position) {
                        BleConnectUtils.connectList.remove(i);
                        break;
                    }
                }
                disconnectAlarmChannel.add(position);
                updateAlarmUI(position);
                if (disconnectAlarmDialog != null
                        && disconnectAlarmDialog.isShowing()) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < alarmChannel.size(); i++) {
                        sb.append(alarmChannel.get(i)).append(",");
                    }
                    if (action.equals("showDisconnectAlarmDialogInMainPager")) {
                        disconnectAlarmDialogTitle.setText(getResources()
                                .getString(R.string.dialog_title,
                                        sb.toString()));
                    }
                    return;
                }
            } else if (actionStr.equals("showHighAlarmDialogInMainPager")) {
                if (checkAddPosition(position, highAlarmChannel)) {
                    highAlarmChannel.add(position);
                    updateAlarmUI(position);
                    if (highAlarmDialog != null && highAlarmDialog.isShowing()) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < highAlarmChannel.size(); i++) {
                            sb.append(highAlarmChannel.get(i)).append(",");
                        }
                        highAlarmDialogTitle.setText(getResources().getString(
                                R.string.dialog_title, sb.toString()));
                        return;
                    }
                } else {
                    if (highAlarmDialog != null && highAlarmDialog.isShowing()) {
                        return;
                    }
                }
            } else if (actionStr.equals("showGrillHighAlarmDialogInMainPager")) {
                if (checkAddPosition(position, highGrillAlarmChannel)) {
                    highGrillAlarmChannel.add(position);
                    updateAlarmUI(position);
                    if (highGrillAlarmDialog != null && highGrillAlarmDialog.isShowing()) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < highGrillAlarmChannel.size(); i++) {
                            sb.append(highGrillAlarmChannel.get(i)).append(",");
                        }
                        highGrillAlarmDialogTitle.setText(getResources().getString(
                                R.string.dialog_title, sb.toString()));
                        return;
                    }
                } else {
                    if (highGrillAlarmDialog != null && highGrillAlarmDialog.isShowing()) {
                        return;
                    }
                }
            }
            long[] pattern = {100, 400, 100, 400}; // 振动 开启
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
                            Log.e("hehe", "DataUtils.mp.start() error");
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
            if (actionStr.equals("showAlarmDialogInMainPager")) {
                alarmDialog = new Dialog(context, R.style.dialog);
                alarmDialog.setContentView(R.layout.alarm_start_dialog);
                alarmDialogTitle = (TextView) alarmDialog
                        .findViewById(R.id.title);
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
                        if (action
                                .getText()
                                .toString()
                                .equals(getResources().getString(R.string.stop))) {
                            endTime(position);
                        }
                        switch (DataUtils.alarmType) {
                            case 0:
                                if (DataUtils.mp != null) {
                                    DataUtils.mp.stop();
                                    DataUtils.mp = null;
                                }
                                break;
                            case 1:
                                if (DataUtils.vibrator != null) {
                                    DataUtils.vibrator.cancel();
                                    DataUtils.vibrator = null;
                                }
                                break;
                            case 2:
                                if (DataUtils.mp != null) {
                                    DataUtils.mp.stop();
                                    DataUtils.mp = null;
                                }
                                if (DataUtils.vibrator != null) {
                                    DataUtils.vibrator.cancel();
                                    DataUtils.vibrator = null;
                                }
                                break;
                        }
                        for (int i = 0; i < alarmChannel.size(); i++) {
                            updateAlarmBackUI(alarmChannel.get(i));
                        }
                        alarmChannel.clear();
                    }
                });
                alarmDialog.setCancelable(false);
                alarmDialog.show();
            } else if (actionStr.equals("showDisconnectAlarmDialogInMainPager")) {
                disconnectAlarmDialog = new Dialog(context, R.style.dialog);
                disconnectAlarmDialog
                        .setContentView(R.layout.alarm_start_dialog);
                disconnectAlarmDialogTitle = (TextView) disconnectAlarmDialog
                        .findViewById(R.id.title);
                disconnectAlarmDialogTitle.setText(getResources().getString(
                        R.string.dialog_title, position + ""));
                TextView msg = (TextView) disconnectAlarmDialog
                        .findViewById(R.id.msg);
                msg.setText(R.string.disconnect_alarm_msg);
                FrameLayout ok = (FrameLayout) disconnectAlarmDialog
                        .findViewById(R.id.ok);
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
                                if (DataUtils.vibrator != null) {
                                    DataUtils.vibrator.cancel();
                                    DataUtils.vibrator = null;
                                }
                                break;
                            case 2:
                                if (DataUtils.mp != null) {
                                    DataUtils.mp.stop();
                                    DataUtils.mp = null;
                                }
                                if (DataUtils.vibrator != null) {
                                    DataUtils.vibrator.cancel();
                                    DataUtils.vibrator = null;
                                }
                                break;
                        }
                        for (int i = 0; i < disconnectAlarmChannel.size(); i++) {
                            updateAlarmBackUI(disconnectAlarmChannel.get(i));
                        }
                        disconnectAlarmChannel.clear();

                    }
                });
                disconnectAlarmDialog.setCancelable(false);
                disconnectAlarmDialog.show();
            } else if (actionStr.equals("showHighAlarmDialogInMainPager")) {
                highAlarmDialog = new Dialog(context, R.style.dialog);
                highAlarmDialog.setContentView(R.layout.alarm_start_dialog);
                highAlarmDialogTitle = (TextView) highAlarmDialog
                        .findViewById(R.id.title);
                highAlarmDialogTitle.setText(getResources().getString(
                        R.string.dialog_title, position + ""));
                TextView msg = (TextView) highAlarmDialog
                        .findViewById(R.id.msg);
                msg.setText(R.string.high_alarm_msg);
                FrameLayout ok = (FrameLayout) highAlarmDialog
                        .findViewById(R.id.ok);
                ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        highAlarmDialog.dismiss();
                        switch (DataUtils.alarmType) {
                            case 0:
                                if (DataUtils.mp != null) {
                                    DataUtils.mp.stop();
                                    DataUtils.mp = null;
                                }

                                break;
                            case 1:
                                if (DataUtils.vibrator != null) {
                                    DataUtils.vibrator.cancel();
                                    DataUtils.vibrator = null;
                                }
                                break;
                            case 2:
                                if (DataUtils.mp != null) {
                                    DataUtils.mp.stop();
                                    DataUtils.mp = null;
                                }
                                if (DataUtils.vibrator != null) {
                                    DataUtils.vibrator.cancel();
                                    DataUtils.vibrator = null;
                                }
                                break;
                        }
                        for (int i = 0; i < highAlarmChannel.size(); i++) {
                            updateAlarmBackUI(highAlarmChannel.get(i));
                        }
                        highAlarmChannel.clear();

                    }
                });
                highAlarmDialog.setCancelable(false);
                highAlarmDialog.show();

            } else if (actionStr.equals("showHighGrillAlarmDialogInMainPager")) {
                highGrillAlarmDialog = new Dialog(context, R.style.dialog);
                highGrillAlarmDialog.setContentView(R.layout.alarm_start_dialog);
                highGrillAlarmDialogTitle = (TextView) highGrillAlarmDialog
                        .findViewById(R.id.title);
                highGrillAlarmDialogTitle.setText(getResources().getString(
                        R.string.dialog_title, position + ""));
                TextView msg = (TextView) highGrillAlarmDialog
                        .findViewById(R.id.msg);
                msg.setText(R.string.grill_high_alarm_msg);
                FrameLayout ok = (FrameLayout) highGrillAlarmDialog
                        .findViewById(R.id.ok);
                ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        highGrillAlarmDialog.dismiss();
                        switch (DataUtils.alarmType) {
                            case 0:
                                if (DataUtils.mp != null) {
                                    DataUtils.mp.stop();
                                    DataUtils.mp = null;
                                }

                                break;
                            case 1:
                                if (DataUtils.vibrator != null) {
                                    DataUtils.vibrator.cancel();
                                    DataUtils.vibrator = null;
                                }
                                break;
                            case 2:
                                if (DataUtils.mp != null) {
                                    DataUtils.mp.stop();
                                    DataUtils.mp = null;
                                }
                                if (DataUtils.vibrator != null) {
                                    DataUtils.vibrator.cancel();
                                    DataUtils.vibrator = null;
                                }
                                break;
                        }
                        for (int i = 0; i < highGrillAlarmChannel.size(); i++) {
                            updateAlarmBackUI(highGrillAlarmChannel.get(i));
                        }
                        highGrillAlarmChannel.clear();
                    }
                });
                highGrillAlarmDialog.setCancelable(false);
                highGrillAlarmDialog.show();

            }
        }

    };


    private final BroadcastReceiver updateRingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent.getIntExtra("position", 1));
        }
    };


    private final BroadcastReceiver allProbesAlarmClickReceiver = new BroadcastReceiver() { //所有探針鬧鐘接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Integer> list = intent.getIntegerArrayListExtra("alarmChannel");
            int size = list.size();
            for (int i = 0; i < size; i++) {
                endTime(list.get(i));
            }
        }
    };

    //獲得資源
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
                            (int) targetTemperature, type).replace(" ", "_")));
                    BleConnectUtils.uiMap.put(position, paramer);
                    break;
                case 2:
                    paramer.setStatus(2);
                    Log.e("time", targetTemperature + "");
                    int hour = (int) targetTemperature / 3600;
                    int min = (int) targetTemperature % 3600 / 60;
                    int second = (int) targetTemperature % 60;
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
            updateBleWorkStatusUI(position);
            if (batteryStatus == 1) {
                batteryLowChange(position);
            }

        }
    };

    //更新界面
    public void updateUI(int position) {
        if (position == DataUtils.selectedChannelNumber) {
        } else {
            DataUtils.selectedChannelNumber = position;
            changeChannelUpdateUI();
        }

    }

    //後退按下
    @Override
    public void onBackPressed() { //onBackPressed
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }

    //檢查權限
    private void checkPermissions() { //檢查權限
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if (mBluetoothAdapter == null) {
                    bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = bluetoothManager.getAdapter();
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(context, "bluetooth_not_supported",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }
                    mScan.init(ConnectActivity.this, mBluetoothAdapter);
                    Log.e("init Scan", "============" + ((mBluetoothAdapter == null) ? "true" : "false"));
                }
                mScan.clear();
                BleScanUtils.getInstance(mScan, context).run();
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
    }

    //檢查位置權限
    public void checkLocationPermission() {
        if (!isLocServiceEnable(context)) {//检测是否开启定位服务
            openLocation();
            //showLocServiceDialog(getContext());
        } else {//检测用户是否将当前应用的定位权限拒绝
            int checkResult = checkOp(context, 2, AppOpsManager.OPSTR_FINE_LOCATION);//其中2代表AppOpsManager.OP_GPS，如果要判断悬浮框权限，第二个参数需换成24即AppOpsManager。OP_SYSTEM_ALERT_WINDOW及，第三个参数需要换成AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW
            int checkResult2 = checkOp(context, 1, AppOpsManager.OPSTR_FINE_LOCATION);
            if (AppOpsManagerCompat.MODE_IGNORED == checkResult || AppOpsManagerCompat.MODE_IGNORED == checkResult2) {
                openLocation();
                //showLocIgnoredDialog(getContext());
            } else {
                if (mBluetoothAdapter == null) {
                    bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = bluetoothManager.getAdapter();
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(context, "bluetooth_not_supported",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }
                    mScan.init(ConnectActivity.this, mBluetoothAdapter);
                    Log.e("init Scan", "============" + ((mBluetoothAdapter == null) ? "true" : "false"));
                }
                mScan.clear();
                BleScanUtils.getInstance(mScan, context).run();
            }
        }
    }

    /**
     * 直接跳转至位置信息设置界面
     */
    public void openLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 10009);
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 检查权限列表
     *
     * @param context
     * @param op       这个值被hide了，去AppOpsManager类源码找，如位置权限  AppOpsManager.OP_GPS==2
     * @param opString 如判断定位权限 AppOpsManager.OPSTR_FINE_LOCATION
     * @return @see 如果返回值 AppOpsManagerCompat.MODE_IGNORED 表示被禁用了
     */
    public static int checkOp(Context context, int op, String opString) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
//            Object object = context.getSystemService("appops");
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
                if (Build.VERSION.SDK_INT >= 23) {
                    return AppOpsManagerCompat.noteOp(context, opString, context.getApplicationInfo().uid,
                            context.getPackageName());
                }

            }
        }
        return -1;
    }

    //網絡可用
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //http_server 正常
    private void http_server_OK() {

        String theDay = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        OkHttpClient client = new OkHttpClient();
        long unixTime = System.currentTimeMillis() / 1000L;
        Log.i("HKT", Long.toString(unixTime) + "  HEX:" + Long.toHexString(225111191 + unixTime));
        String ser_url = getResources().getString(R.string.postServerUrl);
        HttpUrl.Builder builder = HttpUrl.parse(ser_url).newBuilder();
        builder.addQueryParameter("params1", Long.toHexString(225111191 + unixTime));

        //builder.addQueryParameter("parame2", unixTime);
        Request request = new Request.Builder().url(builder.toString()).build();
        Call call = client.newCall(request);

        // 執行 Call 連線
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Date df = new java.util.Date(Long.parseLong(result) * 1000L);
                String vv = new SimpleDateFormat("yyyyMMdd").format(df);
                String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
                Log.d("TEST", result + "   theDay" + vv + " today:" + today);
                if (vv.contentEquals(today)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lan_status.setImageResource(R.drawable.button_yellow);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("TEST", " Http fail -> " + e.toString());

                Toast.makeText(context, R.string.http_error, Toast.LENGTH_LONG);

            }

        });
    }

    //獲取網絡IP
    private String getNetWorkIp() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //ConnectivityManager connectionManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo().isConnected()) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
                final List<LinkAddress> linkAddress = linkProperties.getLinkAddresses();
                InetAddress address = null;
                Log.i("Test", " linkAddress size  " + linkAddress.size());
                switch (linkAddress.size()) {
                    //final InetAddress address = linkAddress.get(0).getAddress();
                    case 1:
                        address = linkAddress.get(0).getAddress();
                        break;
                    case 2:
                        address = linkAddress.get(1).getAddress();
                        break;
                }

                return address.getHostAddress();
            } else
                return "127.0.0.1";
        } else
            return "127.0.0.1";
    }

    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
                mServiceField.setAccessible(true);

                Object btManagerService = mServiceField.get(bluetoothAdapter);

                if (btManagerService != null) {
                    bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
                }
            } catch (NoSuchFieldException e) {

            } catch (NoSuchMethodException e) {

            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            }
        } else {
            bluetoothMacAddress = bluetoothAdapter.getAddress();
        }
        return bluetoothMacAddress;
    }
}
