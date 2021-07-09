package fmgtech.grillprobee.barbecue.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@SuppressLint("NewApi")
public class BleGatt {
    private BluetoothDevice mDevice;
    private int timeCount;
    private int interval;
    public boolean startFlag = false;
    public float targetTemperature;
    private boolean isWork;
    public ArrayList<String> responseValue = new ArrayList<String>();
    private ArrayMap<String, String> powerArray = new ArrayMap<>() ;
    private ArrayMap<String, String> reportArray = new ArrayMap<>() ;
    private String TAG = "BleGatt";
    // 在蓝牙扫描结果列表的位置
    private int position;
    private int connectPosition;
    private boolean highTemperatureSendBroadcast = false;
    private boolean highGrillTemperatureSendBroadcast = false;
    private boolean targetTemperatureSendBroadcast = false;
    public String mac;

    private Activity mActivity = null;

    private BluetoothGatt mGatt = null;
    private BluetoothGattService mGattService = null;
    private BluetoothGattCharacteristic mGattCharCmd = null;
    private BluetoothGattCharacteristic mReadGattCharNotify = null;
    private BluetoothGattCharacteristic mGattCharNotify = null;
    private BluetoothGattCharacteristic writeGattCharacteristic = null;
    private static String STATUS_NOTIFY_CHARACTERISTIC = "0000fb05-0000-1000-8000-00805f9b34fb";
    private static String TEMPERATURE_NOTIFY_CHARACTERISTIC = "0000fb02-0000-1000-8000-00805f9b34fb";
    private static String SPECIAL_SERVICE = "0000fb00-0000-1000-8000-00805f9b34fb";
    private static String WRITE_SERVICE = "0000fb03-0000-1000-8000-00805f9b34fb";
    public static final UUID UUID_SERVICE = UUID.fromString(SPECIAL_SERVICE);
    public static final UUID UUID_NOTIFY = UUID
            .fromString(TEMPERATURE_NOTIFY_CHARACTERISTIC);
    public static final UUID UUID_READ_NOTIFY = UUID
            .fromString(STATUS_NOTIFY_CHARACTERISTIC);
    public static final UUID UUID_WRITE = UUID.fromString(WRITE_SERVICE);
    public CalculateUtils calculateUtils;
    boolean sleepThread = false;
    private String rp_time = "";
    private String powerLevel = "";
    private String bbqNqme = "";
    private String getNetworkIp = "";
    private String getIMEI = "";
    private MqttAndroidClient client;
    private boolean mqConnected = false;
    private final String server_location = "tcp://dtmtsocket.ysit.com.tw:8883";
    public void resetInit() {
        startFlag = false;
        timeCount = 0;
        interval = 0;
        highTemperatureSendBroadcast = false;
        targetTemperatureSendBroadcast = false;
        highGrillTemperatureSendBroadcast = false;
    }


    public BluetoothGatt getmGatt() {
        return mGatt;
    }

    public void setmGatt(BluetoothGatt mGatt) {
        this.mGatt = mGatt;
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean isWork) {
        this.isWork = isWork;
    }

    public float getTargetTemperature() {
        return targetTemperature;
    }

    public void setTargetTemperature(float targetTemperature) {
        this.targetTemperature = targetTemperature;
        if(calculateUtils==null) {
            calculateUtils = new CalculateUtils(connectPosition);
        }
        calculateUtils.setTGB(targetTemperature);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setIP(String theIp){
        this.getNetworkIp = theIp;
    }

    public void  setIMEI(String theIMEI){
        this.getIMEI = theIMEI;
    }

    public void setBBQName(String BbqName){ this.bbqNqme = BbqName;}

    private BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {

        /**
         * Callback invoked by Android framework and a LE connection state
         * change occurs
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            Log.i(TAG, "onConnectionStateChange: status = " + status
                    + ", newState = " + newState);
            AutoConnectUtils.getInstance().connectionStatus = 2;
            if (AutoConnectUtils.getInstance().autoConnectStatus) {
                AutoConnectUtils.getInstance().autoConnectMac = "";
            }
            if (status != 0 && newState != BluetoothProfile.STATE_DISCONNECTED) {
                gatt.disconnect();
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (!gatt.discoverServices()) {
                    Log.e("BleGatt", "没有发现服务");
                } else {
                    Intent intent = new Intent(
                            BlemeshIntent.ACTION_GATT_CONNECTED);
                    Bundle bundle = new Bundle();
                    String name = mGatt.getDevice().getName();
                    mac = mGatt.getDevice().getAddress();
                    bundle.putString("name", name);
                    bundle.putString("mac", mac);
                    bundle.putInt("position", position);
                    bundle.putBoolean("autoConnectStatus",
                            AutoConnectUtils.getInstance().autoConnectStatus);
                    int tempPosition = CacheDataUtils
                            .checkAddPosition(mActivity);
                    if(calculateUtils==null) {
                        calculateUtils = new CalculateUtils(connectPosition);
                    }
                    calculateUtils.setCompensateStatus(BleConnectUtils.compensateMap.get(mac)==null?false:BleConnectUtils.compensateMap.get(mac));
                    bundle.putInt("connectPosition", connectPosition);
                    intent.putExtras(bundle);
                    CacheDataUtils.replacePosition(mActivity, connectPosition,
                            name, mac);
                    mActivity.sendBroadcast(intent);
                    if(myTimeTask==null) {
                        myTimeTask = new MyTimeTask(1000, new TimerTask() {
                            @Override
                            public void run() {
                                if (currentTemperature == -1000) {
                                    return;
                                }
                                //Log.e("action","==========");
                                Intent intent = new Intent("temperatureData");
                                intent.putExtra("current", currentTemperature);
                                intent.putExtra("grill", grillTemperature);
                                intent.putExtra("position", connectPosition);
                                intent.putExtra("pon", bbqNqme);
                                mActivity.sendBroadcast(intent);
                            }
                        });
                        currentTemperature = -1000;
                    }
                    myTimeTask.start();
                    /*if(connectPosition==2){
                        initList1();
                    }else {
                        initList();
                    }*/
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close();
                //mGatt = null;
               // mGattService = null;
               // mGattCharCmd = null;
               // mReadGattCharNotify = null;
                if (status == 133) {
                    System.out.println("连接出现status错误==" + status + "  当前是处在弹出框== " + AutoConnectUtils.getInstance().autoConnectStatus);
                    Intent intent = new Intent(
                            BlemeshIntent.ACTION_GATT_CONNECTION_ERROR);
                    Bundle bundle = new Bundle();
                    bundle.putInt("removePosition", connectPosition);
                    intent.putExtras(bundle);
                    mActivity.sendBroadcast(intent);
                } else {
                    switch (connectPosition){
                        case 1:
                            BleConnectUtils.batteryLow1 = 0;
                            break;
                        case 2:
                            BleConnectUtils.batteryLow2 = 0;
                            break;
                        case 3:
                            BleConnectUtils.batteryLow3 = 0;
                            break;
                        case 4:
                            BleConnectUtils.batteryLow4 = 0;
                            break;
                    }
                    if (status == 0) {
                        Intent intent = new Intent(
                                BlemeshIntent.ACTION_GATT_DISCONNECTED);
                        Bundle bundle = new Bundle();
                        bundle.putInt("removePosition", connectPosition);
                        bundle.putString("mac", mac);
                        intent.putExtras(bundle);
                        mActivity.sendBroadcast(intent);
                    } else {
                        System.out.println("断开出现status错误==" + status + "  当前是处在弹出框== " + AutoConnectUtils.getInstance().autoConnectStatus);
                        Intent intent = new Intent(
                                BlemeshIntent.ACTION_GATT_DISCONNECTED_ERROR);
                        Bundle bundle = new Bundle();
                        bundle.putInt("removePosition", connectPosition);
                        bundle.putString("mac", mac);
                        intent.putExtras(bundle);
                        mActivity.sendBroadcast(intent);
                    }

                }
            }
        }

        /**
         * Callback invoked by Android framework when LE service discovery
         * completes
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered: status = " + status);
            if (status != 0) {
                gatt.disconnect();
                return;
            }

            mGattService = gatt.getService(UUID_SERVICE);
            if (mGattService == null) {
                Log.e(TAG, "onServicesDiscovered: Blemesh Service ("
                        + UUID_SERVICE + ") not found");
                gatt.disconnect();
                return;
            }

            mGattCharNotify = mGattService.getCharacteristic(UUID_NOTIFY);
            if (mGattCharNotify == null) {
                Log.e(TAG, "onServicesDiscovered: Blemesh Characteristic ("
                        + UUID_NOTIFY + ") not found");
                gatt.disconnect();
                return;
            }

            writeGattCharacteristic = mGattService
                    .getCharacteristic(UUID_WRITE);
            if (writeGattCharacteristic == null) {
                Log.e(TAG, "onServicesDiscovered: Blemesh Characteristic ("
                        + UUID_WRITE + ") not found");
                gatt.disconnect();
                return;
            }
            writeGattCharacteristic
                    .setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

            mReadGattCharNotify = mGattService
                    .getCharacteristic(UUID_READ_NOTIFY);
            if (mReadGattCharNotify == null) {
                Log.e(TAG, "onServicesDiscovered: Blemesh Characteristic ("
                        + UUID_READ_NOTIFY + ") not found");
                gatt.disconnect();
                return;
            }

            mGatt.readCharacteristic(mReadGattCharNotify);
        }

        /**
         * Callback invoked by Android framework when a descriptor write
         * completes
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // Log.e(TAG, "onCharacteristicWrite: status = " + status);
            byte[] val = characteristic.getValue();
            // Log.e(TAG, "onCharacteristicWrite: val = " +
            // Hex.bytesToHexString(val));

            if (status != 0) {
                gatt.disconnect();
            }
        }

        /**
         * Callback invoked by Android framework when a characteristic
         * notification occurs
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(characteristic, gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            /*String tempValue = "030001980300";
			byte[] data  = Hex.hexStringToBytes(tempValue);*/
            final byte[] data = characteristic.getValue();
            Log.e(TAG,
                    "onCharacteristicRead: val = " + Hex.bytesToHexString(data));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // broadcastUpdate(characteristic);
                int batteryStatus = data[1];
                int workStatus = data[2];
                short temp = Hex
                        .byteArrayToShort(new byte[]{data[3], data[4]});
                int type = data[5];
                Intent intent = new Intent("BleWorkStatus");
                intent.putExtra("position", connectPosition);
                intent.putExtra("workStatus", workStatus);
                intent.putExtra("batteryStatus", batteryStatus);
                if(workStatus>0) {
                    startFlag = true;
                    if (workStatus == 2) {
                        intent.putExtra("targetTemperature", (float) temp);
                    } else {
                        targetTemperature = (int) DataUtils
                                .trueTmeperature(temp);
                        intent.putExtra("targetTemperature", targetTemperature);
                    }
                    intent.putExtra("type", type);
                    mActivity.sendBroadcast(intent);
                    Intent intent1 = new Intent("AllProbesBleWorkStatus");
                    intent1.putExtra("position", connectPosition);
                    intent1.putExtra("workStatus", workStatus);
                    intent1.putExtra("batteryStatus", batteryStatus);
                    if (workStatus == 2) {
                        intent1.putExtra("targetTemperature", (float) temp);
                    } else {
                        targetTemperature = (int) DataUtils
                                .trueTmeperature(temp);
                        intent1.putExtra("targetTemperature", targetTemperature);

                    }
                    intent1.putExtra("type", type);
                    mActivity.sendBroadcast(intent1);
                    calculateUtils.setTGB(targetTemperature);
                }

                boolean isEnableNotification = gatt
                        .setCharacteristicNotification(mGattCharNotify, true);
                if (isEnableNotification) {
                    List<BluetoothGattDescriptor> descriptorList = mGattCharNotify
                            .getDescriptors();
                    if (descriptorList != null && descriptorList.size() > 0) {
                        for (BluetoothGattDescriptor descriptor : descriptorList) {
                            descriptor
                                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            boolean isEnableNotification = mGatt
                                    .setCharacteristicNotification(
                                            mReadGattCharNotify, true);
                            if (isEnableNotification) {
                                List<BluetoothGattDescriptor> descriptorList = mReadGattCharNotify
                                        .getDescriptors();
                                if (descriptorList != null
                                        && descriptorList.size() > 0) {
                                    for (BluetoothGattDescriptor descriptor : descriptorList) {
                                        descriptor
                                                .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        mGatt.writeDescriptor(descriptor);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        }

    };

    public boolean writeLlsAlertLevel(byte[] bb) {
        try {
            writeGattCharacteristic.setValue(bb);
            return mGatt.writeCharacteristic(writeGattCharacteristic);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    int count = 0;

    ArrayList<float[]> list = null;

    float currentTemperature = -1000;
    float grillTemperature = -1000;

    private void broadcastUpdate(
            final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        // change to hex data
        final byte[] data = characteristic.getValue();
        //Log.e("data",Hex.encodeHexStr(data));
        String t = Hex.encodeHexStr(data);
        if (t.substring(0,2).equals("ff")){
            rp_time = t.substring(t.length()-1,t.length());
            //Log.i("Test","report time: " + rp_time + "  " + gatt.getDevice().getAddress());
            if (reportArray.isEmpty()){
                reportArray.put(gatt.getDevice().getAddress(),rp_time);
            }
            else{
                String rp = reportArray.get(gatt.getDevice().getAddress());
                reportArray.put(gatt.getDevice().getAddress(),rp);
            }
        }

        if (t.substring(0,2).equals("03")) {
            powerLevel = t.substring(2,4);
            //Log.i("Test","porwe level "  + powerLevel + "  " + gatt.getDevice().getAddress());
            if (powerArray.isEmpty()){
                powerArray.put(gatt.getDevice().getAddress(),powerLevel);
            }
            else{
                String pt = powerArray.get(gatt.getDevice().getAddress());
                powerArray.put(gatt.getDevice().getAddress(),pt);
            }

        }
        if (characteristic.getUuid().equals(UUID_NOTIFY)) {
            int bleTemperature = Hex.byteArrayToShort(new byte[]{data[2],
                    data[3]});
            int bleGrill = Hex
                    .byteArrayToShort(new byte[]{data[4], data[5]});
            currentTemperature = DataUtils
                    .trueTmeperature(bleTemperature);
            grillTemperature = DataUtils.trueTmeperature(bleGrill);
            /*grillTemperature = list.get(count)[1];
            currentTemperature = list.get(count)[0];
            count++;*/
            if(currentTemperature>=targetTemperature && startFlag && targetTemperature>0){
                Intent leftTime = new Intent("leftTime");
                leftTime.putExtra("countDownFinish", 1);
                leftTime.putExtra("position", connectPosition);
                leftTime.putExtra("leftTime", 0);
                mActivity.sendBroadcast(leftTime);
                calculateUtils.countDownFinish = 0;
                //Log.e("left0 tar,curr，gri,pos",targetTemperature+"  ,"+currentTemperature+"  ,"+grillTemperature+" sleepThread=="+sleepThread+"  ,"+connectPosition);

            }
            data2SDStorage(connectPosition,getIMEI,getNetworkIp, currentTemperature, grillTemperature, gatt.getDevice().getAddress(),rp_time);

            //Log.e("ble温度值current，grill,pos",currentTemperature+"  ,"+grillTemperature+" sleepThread=="+sleepThread+"  ,"+connectPosition);
            if (!sleepThread) {
                calculateUtils.setValue(currentTemperature,grillTemperature,mac);
                //Log.e("传入周期计算current，grill,pos",currentTemperature+"  ,"+grillTemperature+"  ,"+connectPosition);
                //Log.e("倒计时条件target,finish,pos",targetTemperature+"  ,"+calculateUtils.countDownFinish+"  ,"+connectPosition);
                if(targetTemperature>0 && currentTemperature<targetTemperature) {
                    if(calculateUtils.countDownFinish==1) {
                        Intent leftTime = new Intent("leftTime");
                        leftTime.putExtra("position", connectPosition);
                        leftTime.putExtra("leftTime", calculateUtils.leftTime());
                        leftTime.putExtra("countDownFinish", calculateUtils.countDownFinish);
                        mActivity.sendBroadcast(leftTime);
                    }
                }
                sleepThread = true;
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(60*1000);
                            sleepThread = false;
                            //Log.e("thread","60s执行完成====");
                        }
                    }).start();
            }
            //if (grillTemperature < 0) {
            //    grillTemperature = 0;
            //}
            if (grillTemperature > 300) {
                grillTemperature = 300;
            }
           /* if (currentTemperature < 0) {
                currentTemperature = 0;
            }*/
            if (currentTemperature > 300) {
                currentTemperature = 300;
            }
            //grillTemperature = calculateUtils.compensateValue(grillTemperature);
            switch (connectPosition) {
                case 1:
                    BleConnectUtils.current_temperature1 = currentTemperature;
                    BleConnectUtils.grill_temperature1 = grillTemperature;
                    break;
                case 2:
                    BleConnectUtils.current_temperature2 = currentTemperature;
                    BleConnectUtils.grill_temperature2 = grillTemperature;
                    break;
                case 3:
                    BleConnectUtils.current_temperature3 = currentTemperature;
                    BleConnectUtils.grill_temperature3 = grillTemperature;
                    break;
                case 4:
                    BleConnectUtils.current_temperature4 = currentTemperature;
                    BleConnectUtils.grill_temperature4 = grillTemperature;
                    break;
            }
            //Log.e("计算后温度值current，grill,pos",currentTemperature+"  ,"+grillTemperature+"  ,"+connectPosition);
		/*	if (startFlag) {
				if (rangeTemperatureList.size() > 0) {
					if (currentTemperature - rangeTemperatureList.get(0) >= rangeTemperature) {
						intent.putExtra("startTemperature",
								rangeTemperatureList.get(0));
						if (rangeTemperatureList.size() > 1) {
							intent.putExtra("timeCount", timeCount);
						} else {
							intent.putExtra("timeCount", interval);
						}
						// Log.e("test","5个温度单位清0,计时温度=="+rangeTemperatureList.get(0)+"  当前温度="+currentTemperature);
						rangeTemperatureList.clear();
						timeCount = 0;

					} else {
						if (targetTemperature > 0
								&& currentTemperature >= targetTemperature) {
							intent.putExtra("startTemperature",
									rangeTemperatureList.get(0));
							if (rangeTemperatureList.size() > 1) {
								intent.putExtra("timeCount", timeCount);
							} else {
								intent.putExtra("timeCount", interval);
							}
							rangeTemperatureList.clear();
							timeCount = 0;
						} else {
							rangeTemperatureList.add(currentTemperature);
							timeCount += interval;
						}
					}
				} else {
					if (currentTemperature - lastTemperature >= rangeTemperature
							&& interval > 0) {
						intent.putExtra("startTemperature", lastTemperature);
						intent.putExtra("timeCount", interval);
						rangeTemperatureList.clear();
						timeCount = 0;
					} else {
						if (targetTemperature > 0
								&& currentTemperature >= targetTemperature) {
							intent.putExtra("startTemperature",
									currentTemperature);
							intent.putExtra("timeCount", 1);
							rangeTemperatureList.clear();
							timeCount = 0;
						} else {
							rangeTemperatureList.add(currentTemperature);
						}
					}
				}
				lastTemperature = currentTemperature;
				interval = data[6];
			}*/
            if (currentTemperature >= DataUtils.highTemperature
                    && !highTemperatureSendBroadcast) {
                highTemperatureSendBroadcast = true;
                Intent alarmIntent = new Intent("highTemperatureAlarm");
                alarmIntent.putExtra("position", connectPosition);
                mActivity.sendBroadcast(alarmIntent);
                //Log.e("highAlarm pos","  ,"+connectPosition);
            }
            if (grillTemperature >= DataUtils.highGrillTemperature
                    && !highGrillTemperatureSendBroadcast) {
                highGrillTemperatureSendBroadcast = true;
                Intent alarmIntent = new Intent("highGrillTemperatureAlarm");
                alarmIntent.putExtra("position", connectPosition);
                mActivity.sendBroadcast(alarmIntent);
                //Log.e("highGrillAlarm pos","  ,"+connectPosition);
            }

            if(startFlag && (targetTemperature>0 && targetTemperature<=currentTemperature) && !targetTemperatureSendBroadcast){
                targetTemperatureSendBroadcast = true;
                Intent targetIntent = new Intent("timerAlarmRun");
                targetIntent.putExtra("position", connectPosition);
                mActivity.sendBroadcast(targetIntent);
                //Log.e("timerAlarmRun pos","  ,"+connectPosition);
            }

        } else if (characteristic.getUuid().equals(UUID_READ_NOTIFY)) {
           // Log.e("UUID_READ_NOTIFY", "电量状态值是多少==" + Hex.bytesToHexString(data));
            if (data[1] == 0x00) {
                switch (connectPosition){
                    case 1:
                        if( BleConnectUtils.batteryLow1==1) {
                            BleConnectUtils.batteryLow1 = 0;
                            Intent intent = new Intent("batteryLow");
                            intent.putExtra("position", connectPosition);
                            mActivity.sendBroadcast(intent);
                        }
                        break;
                    case 2:
                        if( BleConnectUtils.batteryLow2==1) {
                            BleConnectUtils.batteryLow2 = 0;
                            Intent intent = new Intent("batteryLow");
                            intent.putExtra("position", connectPosition);
                            mActivity.sendBroadcast(intent);
                        }
                        break;
                    case 3:
                        if( BleConnectUtils.batteryLow3==1) {
                            BleConnectUtils.batteryLow3 = 0;
                            Intent intent = new Intent("batteryLow");
                            intent.putExtra("position", connectPosition);
                            mActivity.sendBroadcast(intent);
                        }
                        break;
                    case 4:
                        if( BleConnectUtils.batteryLow4==1) {
                            BleConnectUtils.batteryLow4 = 0;
                            Intent intent = new Intent("batteryLow");
                            intent.putExtra("position", connectPosition);
                            mActivity.sendBroadcast(intent);
                        }
                        break;
                }
            } else {
                switch (connectPosition){
                    case 1:
                        if( BleConnectUtils.batteryLow1==0) {
                            BleConnectUtils.batteryLow1 = 1;
                            Intent intent = new Intent("batteryLow");
                            intent.putExtra("position", connectPosition);
                            mActivity.sendBroadcast(intent);
                        }
                        break;
                    case 2:
                        if( BleConnectUtils.batteryLow2==0) {
                            BleConnectUtils.batteryLow2 = 1;
                            Intent intent = new Intent("batteryLow");
                            intent.putExtra("position", connectPosition);
                            mActivity.sendBroadcast(intent);
                        }
                        break;
                    case 3:
                        if( BleConnectUtils.batteryLow3==0) {
                            BleConnectUtils.batteryLow3 = 1;
                            Intent intent = new Intent("batteryLow");
                            intent.putExtra("position", connectPosition);
                            mActivity.sendBroadcast(intent);
                        }
                        break;
                    case 4:
                        if( BleConnectUtils.batteryLow4==0) {
                            BleConnectUtils.batteryLow4 = 1;
                            Intent intent = new Intent("batteryLow");
                            intent.putExtra("position", connectPosition);
                            mActivity.sendBroadcast(intent);
                        }
                        break;
                }
                System.out.println("============电量低电");
            }

        }
    }

    public void init(Activity activity, int connectPosition) {
        mActivity = activity;
        this.connectPosition = connectPosition;
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond",
                    (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connect(BluetoothDevice device) {
        mDevice = device;
        mGatt = device.connectGatt(mActivity, false, mGattCallbacks);
        if (mGatt != null) {
            return true;
        }
        Log.e(TAG, "connect: Failed to connect device " + device);
        return false;
    }


    public void disconnect() {
        if (mGatt != null) {
            mGatt.disconnect(); // Disconnect the connected proxy device
            if(myTimeTask!=null) {
                myTimeTask.stop();
            }
        }
    }


    public void stopMyTimeTask() {
        if(myTimeTask!=null) {
            myTimeTask.stop();
        }
    }

    MyTimeTask myTimeTask;

    class MyTimeTask {
        private Timer timer;
        private TimerTask task;
        private long time;

        public MyTimeTask(long time, TimerTask task) {
            this.task = task;
            this.time = time;
            if (timer == null){
                timer=new Timer();
            }
        }

        public void start(){
            timer.schedule(task, 1000, time);//每隔time时间段就执行一次
        }

        public void stop(){
            if (task != null) {
                task.cancel();  //将原任务从队列中移除
            }
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    private void data2SDStorage(int pos, String hwName, String netIp, float ct, float gt, String bbqMac, String respond_time){
        final int REQUEST_WRITE_STORAGE = 112;

        boolean hasPermission = (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                final File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File path = new File(externalStoragePublicDirectory, "ysit");
                path.mkdirs();
                //Log.i("Test", " Ysit Dir create failed = " + Environment.MEDIA_MOUNTED_READ_ONLY);
                //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ysit/";
                //File storageDir = new File(path);
                //if (!storageDir.exists() && !storageDir.mkdirs()) {
                // This should never happen - log handled exception!
                //    Log.i("Test", " Ysit Dir create failed");
                //}
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String utcs = Long.toHexString(System.currentTimeMillis() / 1000L);
                String filename = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()) + ".txt";
                //Log.i("Test", path + "/" + filename);
                //Log.i("Test",hwName + " , " + netIp);
                String txtData = hwName + " , " + netIp + " , " + currentTime + " , " + ct + " , " + gt + " , " + bbqMac + " , " + bbqNqme + " , " + respond_time +"\r\n";

                try {
                    File myFile = new File(path, filename);
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile,true);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(txtData);
                    myOutWriter.close();
                    fOut.close();
                    //Log.i("Test", "write file done");

                } catch (IOException e) {
                    Log.e("ERRR", "Could not create file", e);
                }
                Mqtt_sent(pos,hwName,netIp,bbqMac,ct,gt,utcs, bbqNqme);
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void Mqtt_sent(int item ,String hwn, String sender, String address, float t1, float t2,String utc, String bbqNqme) {
        //pos ,hwName,netIp,bbqMac,ct,gt,respond_time,currentTime
        if (isNetworkAvailable())
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String bbq_value = Integer.toHexString((int)t1*10);
                    String cooker_value = Integer.toHexString((int)t2*10);
                    String bbq_mac = address.replace(":","");
                    String sender_mac = sender.replace(".","");
                    if (sender_mac.length() %2 >0)
                        sender_mac = "0" + sender_mac;
                    //String utc_time = Long.toHexString(System.currentTimeMillis() / 1000L);
                    //String utc_time = Long.toHexString(new Date(String.valueOf(Calendar.getInstance().getTime())).getTime());
                    //Log.i(TAG,"UTC " + utc_time);
                    //int parsedResult = (int) Long.parseLong(utc_time, 16);
                    //Log.i("Test", "power level =" + powerArray.get(address)+ "   pZ : " + powerArray.size() +   "   report : " + reportArray.get(address) + "  rZ: " + reportArray.size());
                    String sec_send = reportArray.get(address);
                    String bat =(powerArray.size() ==0 ? "ff" : (powerArray.get(address) == "00" ? "ff" : powerArray.get(address)));
                    //String bbqNqme = bbq_mac.substring(bbq_mac.length()-5, bbq_mac.length());
                    String mq_text = "10," + sender_mac + "," + bbq_mac + "," + bbqNqme + ","+ item +",0," + bbq_value +
                            "," + cooker_value +"," + bat + ","+ sec_send + ",0";
                    String mq_text_1 = sender_mac + "," + bbq_mac + ","+ bbqNqme + "," + item + ",0," + bbq_value +
                            "," + cooker_value +"," + bat + ","+ sec_send + ",0";
                    //Log.i(TAG," CHKSUM : " + mq_text_1);
                    int chksum =  loop_chk_sum(mq_text_1);
                    String mq_text_0 = mq_text + "," + Integer.toHexString(chksum) + ",0,0," + utc +"," + "ProbeE" + "," + hwn ;
                    //Log.i(TAG," MQTT : " + item + "  " + mq_text_0 + " Size= " + mq_text_0.length());

                    if (mqConnected)
                        push_bbq_data(mq_text_0);
                    else
                        enableNotifi(mq_text_0);
                }
            }).start();
    }

    private void enableNotifi(final String bbqData){
        //String topic = notificationTopic;
        Log.i(TAG," Server IP " + server_location);
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(mActivity.getApplicationContext(), server_location,clientId);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        //connOpts.setUserName("mobile");
        //connOpts.setPassword("123456".toCharArray());
        connOpts.setUserName("rd");
        connOpts.setPassword("123456".toCharArray());
        try {
            IMqttToken token = client.connect(connOpts);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.i(TAG, "Connected Mqtt server Successfully");
                    //Log.i(TAG,"Topic order name " + notificationTopic);
                    mqConnected = true;

                    //subscribe_notification(notificationTopic);
                    if (bbqData.length()>5) push_bbq_data(bbqData);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.i(TAG, "Connect to Mqtt server Failure " + exception.getLocalizedMessage());
                    mqConnected = false;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG, message.toString() + "  ######   " + topic);
                //next_Ble();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void push_bbq_data(String message){
        String topic = "mqtt/blezb/1";
        MqttMessage msg=new MqttMessage();
        String msgStr=message;
        msg.setPayload(msgStr.getBytes());
        msg.setQos(2);
        msg.setRetained(false);
        try {
            IMqttToken subToken = client.publish(topic,msg);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "Push info topic to Mqtt server Successfully");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.i(TAG, "Push Data Failling on Mqtt server ");
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    mqConnected = false;
                    if (mqConnected) enableNotifi("");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.i(TAG, " Mqtt server Exception " + e.getMessage());
        }
    }

    int loop_chk_sum(String all_string){
        String[] all=all_string.split(",");
        int value =0;
        for(int i=0 ; i < all.length ; i++){
            //Log.i(TAG,"BBQ : " + i + " %%%%% " + all[i]);
            switch(i) {
                case 2:
                    int gg = bbq_name_sum_10(all[i]);
                    //Log.i(TAG, "NAME sum : " + gg);
                    value += gg;
                    break;
                case 3:
                case 4:
                case 7:
                case 8:
                case 9:
                    value += Integer.parseInt(all[i],16);
                    break;
                case 0:
                case 1:
                case 5:
                case 6:
                    if (all[i].length() == 3) {
                        value += t1t2("0" + all[i]);
                    }
                    else
                        value += t1t2(all[i]);
                    break;
            }

        }
        return value;
    }

    int t1t2(String t1t2){
        int sum_num=0;

        for(int j=0 ; j < t1t2.length() ; j+=2 ) {
            sum_num += Integer.parseInt(t1t2.substring(j, j + 2), 16);
        }
        //Log.i(TAG," T1T2 " + t1t2 + "   sum= " + sum_num);
        return sum_num;
    }

    int bbq_name_sum_10(String bbqn){
        //String[] pn = bbqn.split(",");
        //Log.i(TAG, "input length " + bbqn.length());
        int value =0;
        for(int i=0 ; i < bbqn.length() ; i++){
            int tmp = 0;
            switch(bbqn.substring(i,i+1)){
                case "A":
                case "B":
                case "C":
                case "D":
                case "E":
                case "F":
                case "a":
                case "b":
                case "c":
                case "d":
                case "e":
                case "f":
                    tmp = Integer.parseInt(bbqn.substring(i,i+1),16);
                    break;
                default:
                    tmp = Integer.parseInt(bbqn.substring(i,i+1),10) + 48 ;
                    break;
            }

            //Log.i(TAG, "input " + tmp );
            value += tmp ;
        }
        //Log.i(TAG," SUM : " + Integer.toHexString(value);
        return value;
    }
}
