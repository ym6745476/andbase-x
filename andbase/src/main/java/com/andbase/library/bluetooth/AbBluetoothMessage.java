package com.andbase.library.bluetooth;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/04/13 09:13
 * Email 396196516@qq.com
 * Info 消息定义
 */
public class AbBluetoothMessage {

    /**蓝牙权限申请*/
    public static final int MSG_BLE_REQUEST_CODE = 1;

    /**蓝牙打开*/
    public static final int  MSG_BLE_ENABLE_REQUEST_CODE = 2;

    /**连接状态 连接成功*/
    public static final int MSG_CONNECT_RESULT_CONNECTED = 3;

    /**连接状态 连接失败*/
    public static final int MSG_CONNECT_RESULT_DISCONNECTED = 4;

    /**连接状态 服务找到*/
    public static final int MSG_CONNECT_RESULT_CONNECT_SERVICES_OK = 5;

    /**连接状态 服务发现失败*/
    public static final int MSG_CONNECT_RESULT_CONNECT_SERVICES_FAIL = 6;

    /**连接状态 服务发现成功*/
    public static final int MSG_CONNECT_RESULT_NO_SERVICES = 7;

    /**连接超时*/
    public static final int MSG_CONNECT_BLE_TIMEOUT = 8;

    /**指令超时*/
    public static final int MSG_RESULT_BLE_TIMEOUT = 9;

    /**连接状态  未发现设备*/
    public static final int MSG_CONNECT_RESULT_NO_DEVICE = 10;


    /**连接状态  MTU设置*/
    public static final int MSG_CONNECT_RESULT_MTU = 11;

}
