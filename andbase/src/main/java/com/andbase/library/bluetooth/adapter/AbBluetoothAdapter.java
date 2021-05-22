package com.andbase.library.bluetooth.adapter;

import android.os.Handler;

import com.andbase.library.bluetooth.AbBlueTooth;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/04/13 09:13
 * Email 396196516@qq.com
 * Info 基础的蓝牙数据适配器
 */
public class AbBluetoothAdapter {

    /**连接实例*/
    public AbBlueTooth blueTooth;

    /**服务设置*/
    public String  bleService;

    /**特征值设置*/
    public String[] bleCharacteristic;

    /**结果消息句柄*/
    public Handler resultHandler;

    /**
     * 连接成功后要将这个BleBlueTooth 设置给Adapter
     * @param blueTooth
     */
    public void setBlueTooth(AbBlueTooth blueTooth){
        this.blueTooth = blueTooth;
        this.resultHandler = blueTooth.resultHandler;
    }

    /**
     * 有数据接收到
     * @param data
     */
    public void onReceiveValue(byte[] data){}

}
