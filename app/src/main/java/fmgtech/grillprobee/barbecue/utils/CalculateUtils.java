package fmgtech.grillprobee.barbecue.utils;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class CalculateUtils {
    int position;
    //炉温测量值
    float TLC;
    //炉温显示值
    float TLX;
    //食物温度测量值
    float TFC;
    //采集周期
    int Cycle;
    //炉温变化率
    float KL;
    //食物温度变化率
    float KF;
    //目标温度
    float TGB;
    //倒计时剩余时间
    int DCT;
    //食物温度变化正向阀值
    final int standardPlusKF = 1;
    //食物温度变化负向阀值
    int standardMinusKF = -2;
    //炉温变化正向阀值
    int standardPlusKL = 5;
    //炉温变化负向阀值
    int standardMinusKL = -6;
    //目前的食物温度测量值
    Float TFCCurrent;
    //前一分钟食物温度测量值
    Float TFCLast;
    //目前的炉温测量值
    Float TLCCurrent;
    //前一分钟炉温测量值
    Float TLCLast;
    //倒计时状态
    public int countDownFinish = 0;  //1 开始   -1 结束

    public Integer lastLeftTime;
    boolean status = false;  //true表示需要补偿

    public CalculateUtils(int position){
        this.position = position;
    }

    public void init(){
        status = false;
        countDownFinish = 0;
        KF = 0f;
        TFCCurrent = null;
        TFCLast = null;
        TLCCurrent = null;
        TLCLast = null;
    }

    public void setTGB(float TGB) {
        lastLeftTime = null;
        this.TGB = TGB;
    }

    public void setTFC(float value) {
        if (TFCCurrent == null) {
            TFCCurrent = value;
        } else{
            TFCLast = TFCCurrent;
            TFCCurrent = value;
        }
    }

    public void setValue(float foodValue, float grillTemperature,String macPosition) {
        setTFC(foodValue);
        if (TLCCurrent == null) {
            TLCCurrent = grillTemperature;
        } else{
            TLCLast = TLCCurrent;
            TLCCurrent = grillTemperature;
            compensateStatus(macPosition);
        }
    }


    //return true表示补偿
    public void compensateStatus(String  mac) {
        KF = TFCCurrent - TFCLast;
        KL = TLCCurrent - TLCLast;
        Log.e("TFCC,TFCLast,KF,pos","TFCCurrent=="+TFCCurrent+"   TFCLast=="+TFCLast+" position=="+position);
        Log.e("TLCC,TLCLast,KL，TGB,pos","TLCCurrent=="+TLCCurrent+"   TLCLast=="+TLCLast+"KL=="+KL+"   ，TGB=="+TGB+" position=="+position);
        if (KF >= standardPlusKF && TLCCurrent >= 60 && (TFCCurrent<TGB && TFCCurrent>=25)) {
            if(countDownFinish==0) {
                countDownFinish = 1;
                Log.e("countDownFinish,pos", "countDownFinish==" + countDownFinish + " position==" + position);
            }
        }
        if (!status) {
            if (KL >= standardPlusKL && KF >= standardPlusKF) {
                status = true;
                //BleConnectUtils.compensateMap.put(mac,status);
            }
        } else {
            if (KL <= standardMinusKL && TLCCurrent < 100) {
                status = false;
                //BleConnectUtils.compensateMap.put(mac,status);
            }
        }
    }

    public void setCompensateStatus(boolean status){
        this.status = status;
    }


    //补偿炉温
    public float compensateValue(float value) {
        if (status) {
            if (value >= 100) {
                value += 35;
            } else {
                value = value + (35 * value / 100);
            }
        }else{
            if(value>=100){
                value += 35;
            }
        }
        return value;
    }


    public int leftTime() {
        if (TGB - TFCCurrent <= 3 ) {
            if(TGB <= TFCCurrent){
                countDownFinish = 0;
                Log.e("倒计时","倒计时=="+0+" position=="+position);
                return 0;
            }else {
                countDownFinish = -1;
                Log.e("倒计时", "倒计时==" + 59 + " position==" + position);
                lastLeftTime = 59;
                return lastLeftTime;
            }
        }
        if (countDownFinish == 1) {
            int value = ((int) ((TGB - TFCLast) / (TFCCurrent - TFCLast)))*60;
            if(lastLeftTime!=null && value>=lastLeftTime){
                Log.e("不倒计时", "上次计算的倒计时==" + lastLeftTime + "此次计算的倒计时==" + value +" position==" + position);
                return lastLeftTime;
            }else {
                lastLeftTime = value;
                Log.e("倒计时", "倒计时==" + lastLeftTime + " position==" + position);
                return lastLeftTime;
            }
        }
        return 0;
    }


}
