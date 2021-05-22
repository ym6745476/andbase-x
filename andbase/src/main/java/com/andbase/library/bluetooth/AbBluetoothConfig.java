package com.andbase.library.bluetooth;


import java.util.UUID;

public class AbBluetoothConfig {

    /** 扫描时间*/
    public static int scanTimeout = 10000;

    /** 连接超时时间*/
    public static int connectTimeout = 10000;

    /** 指令超时时间*/
    public static int readTimeout = 10000;

    /** 扫描的service过滤*/
    public UUID[] scanService = null;

}
