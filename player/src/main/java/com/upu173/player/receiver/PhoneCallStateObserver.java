package com.upu173.player.receiver;

import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class PhoneCallStateObserver {

    public enum PhoneCallStateEnum {
        IDLE,           // 空闲
        INCOMING_CALL,  // 有来电
        DIALING_OUT,    // 呼出电话已经接通
        DIALING_IN      // 来电已接通
    }

    private final String TAG = "PhoneCallStateObserver";

    private int phoneState = TelephonyManager.CALL_STATE_IDLE;
    private PhoneCallStateEnum stateEnum = PhoneCallStateObserver.PhoneCallStateEnum.IDLE;

    private List<Observer<Integer>> localPhoneObservers = new ArrayList<>(1); // 本地电话的监听

    private static class InstanceHolder {
        public final static PhoneCallStateObserver instance = new PhoneCallStateObserver();
    }

    private PhoneCallStateObserver() {

    }

    public static PhoneCallStateObserver getInstance() {
        return InstanceHolder.instance;
    }

    public void onCallStateChanged(String state) {
        Log.i(TAG, "onCallStateChanged, now state =" + state);

        stateEnum = PhoneCallStateEnum.IDLE;
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            phoneState = TelephonyManager.CALL_STATE_IDLE;
            stateEnum = PhoneCallStateEnum.IDLE;
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            phoneState = TelephonyManager.CALL_STATE_RINGING;
            stateEnum = PhoneCallStateEnum.INCOMING_CALL;
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            int lastPhoneState = phoneState;
            phoneState = TelephonyManager.CALL_STATE_OFFHOOK;
            if (lastPhoneState == TelephonyManager.CALL_STATE_IDLE) {
                stateEnum = PhoneCallStateEnum.DIALING_OUT;
                return;
            } else if (lastPhoneState == TelephonyManager.CALL_STATE_RINGING) {
                stateEnum = PhoneCallStateEnum.DIALING_IN;
            }
        }

        handleLocalCall();
    }

    /**
     * 处理本地电话与播放器逻辑
     * Android端软编或者硬编时：
     * 1、点播：来电时我们把播放暂停，电话挂断后恢复播放
     * 2、直播：来电时我们把播放停掉，电话恢复后重新拉流播放
     */
    public void handleLocalCall() {
        Log.i(TAG, "notify phone state changed, state=" + stateEnum.name());
        ObserverUtils.notifyObservers(localPhoneObservers, phoneState);
    }

    public PhoneCallStateEnum getPhoneCallState() {
        return stateEnum;
    }

    public void observeLocalPhoneObserver(Observer<Integer> observer, boolean register) {
        Log.i(TAG, "observeAutoHangUpForLocalPhone->" + observer + "#" + register);
        ObserverUtils.registerObservers(this.localPhoneObservers, observer, register);
    }
}
