package com.andbase.library.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.andbase.library.bluetooth.adapter.AbBluetoothAdapter;
import com.andbase.library.bluetooth.callback.AbBluetoothConnectCallback;
import com.andbase.library.bluetooth.callback.AbBluetoothWriteCallback;
import com.andbase.library.bluetooth.utils.AbBluetoothHexUtil;

import java.util.List;
import java.util.UUID;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/04/13 09:13
 * Email 396196516@qq.com
 * Info 一个蓝牙连接
 */

public class AbBlueTooth {

    /**上下文*/
    private Context context;

    /**蓝牙基本类*/
    private BluetoothGatt bluetoothGatt;
    private List<BluetoothGattService> serviceList = null;
    private BluetoothGattService bluetoothGattService = null;
    private BluetoothGattCharacteristic characteristic = null;
    private BluetoothGattCallback gattCallback = null;
    private AbBluetoothAdapter bleAdapter;

    /**当前连接的设备*/
    public BluetoothDevice device;

    /**连接的监听器*/
    public Handler connectHandler;

    /**指令响应的监听器*/
    public Handler resultHandler;

    /**蓝牙服务特征连接状态*/
    public boolean initOk = false;

    /**蓝牙连接状态*/
    public boolean isConnected = false;

    /**指令计时器*/
    public Handler timerHandler = new Handler();
    public Runnable timeoutRunable = null;

    /** 包大小 20  */
    public static int packageSize = 20;

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_FUNCTION = "0000ffe2-0000-1000-8000-00805f9b34fb";

    public AbBluetoothConnectCallback callback  = null;

    public AbBlueTooth(Context context) {
        this.context = context;
    }

    /**
     * 连接设备
     * @param device 设备
     * @param callback 回调
     */
    public void connect(final BluetoothDevice device, final AbBluetoothConnectCallback callback){
        this.callback = callback;

        if(device != null){
            this.device = device;
        }

        if(this.device == null){
            if(connectHandler != null){
                Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_NO_DEVICE,"未找到蓝牙设备");
                connectHandler.sendMessage(msg);
            }
            return;
        }

        initBluetoothGattCallback();

        bluetoothGatt = this.device.connectGatt(context, false, gattCallback);

        boolean connect = bluetoothGatt.connect();
        if(connect){
            //在回调里开始
        }else{
            //
        }

        //启动指令计时器
        timeoutRunable = new  Runnable() {
            @Override
            public void run() {
                if(callback!=null){
                    callback.fail(-1,"连接超时");
                }

            }
        };
        timerHandler.removeCallbacks(timeoutRunable);
        timerHandler.postDelayed(timeoutRunable, AbBluetoothConfig.connectTimeout);
    }

    /**
     * 重新连接
     */
    public void reConnect(){
        initOk = false;
        isConnected = false;
        if (bluetoothGatt != null){
            bluetoothGatt.disconnect();
        }
        connect(null,callback);
    }

    /**
     * 关闭连接
     */
    public void  closeConnect(){
        initOk = false;
        isConnected = false;

        clearHandler();
        if (bluetoothGatt != null){
            bluetoothGatt.disconnect();
        }
    }

    /**
     * 初始化监听
     */
    public void  initBluetoothGattCallback(){
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                if(timeoutRunable != null){
                    timerHandler.removeCallbacks(timeoutRunable);
                }

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //连接回调ok
                    Log.e("BleBlueTooth", "BluetoothGattCallback STATE_CONNECTED");

                    isConnected = true;

                    if(connectHandler!=null) {
                        //连接成功
                        Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_CONNECTED, bluetoothGatt.getDevice().getAddress());
                        connectHandler.sendMessage(msg);
                    }

                    //发现服务
                    boolean flag = bluetoothGatt.discoverServices();
                    if(!flag){
                        if(connectHandler!=null) {
                            Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_CONNECT_SERVICES_FAIL,"发现服务失败!");
                            connectHandler.sendMessage(msg);
                        }
                    }

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //连接断开ok
                    Log.e("BleBlueTooth", "BluetoothGattCallback STATE_DISCONNECTED");

                    isConnected = false;
                    initOk = false;

                    //连接
                    if(connectHandler!=null){
                        //连接断开
                        Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_DISCONNECTED,"连接断开");
                        connectHandler.sendMessage(msg);
                    }

                    if(bluetoothGatt!= null){
                        bluetoothGatt.close();
                        bluetoothGatt = null;
                    }

                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {

                    Log.e("BleBlueTooth", "BluetoothGattCallback onServicesDiscovered SUCCESS");
                    //获取蓝牙服务
                    serviceList = gatt.getServices();

                    //服务
                    if(serviceList == null || serviceList.size() == 0){
                        Log.e("BleBlueTooth","gatt service size 0!");
                        initOk = false;

                        if(connectHandler!=null){
                            Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_NO_SERVICES,"发现服务失败!");
                            connectHandler.sendMessage(msg);
                        }
                        return;
                    }
                    //FEE1
                    Log.e("BleBlueTooth", "BluetoothGattService Size:" + serviceList.size());
                    for(BluetoothGattService service1:serviceList) {
                        Log.e("BleBlueTooth", "BluetoothGattService:" + service1.getUuid());
                        if (service1.getUuid().toString().toUpperCase().indexOf(bleAdapter.bleService) != -1) {
                            bluetoothGattService = service1;
                            initOk = true;
                            break;
                        }
                    }
                    if (bluetoothGattService == null) {
                        Log.e("BleBlueTooth", "gatt service FFE1 not found!");
                        initOk = false;

                        if (connectHandler != null) {
                            Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_NO_SERVICES, "发现服务失败!");
                            connectHandler.sendMessage(msg);
                        }
                        return;
                    }
                    List<BluetoothGattCharacteristic> characteristicList = bluetoothGattService.getCharacteristics();
                    //特征
                    for (BluetoothGattCharacteristic characteristic1 : characteristicList) {
                        Log.e("BleBlueTooth", "BluetoothGattCharacteristic:" +characteristic1.getUuid());
                        if (characteristic1.getUuid().toString().toUpperCase().indexOf(bleAdapter.bleCharacteristic[0]) != -1) {
                            characteristic = characteristic1;
                        }
                    }
                    if (characteristic == null) {
                        Log.e("BleBlueTooth", "gatt characteristic not found!");
                        initOk = false;

                        if (connectHandler != null) {
                            Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_NO_SERVICES, "gatt characteristic not found!");
                            connectHandler.sendMessage(msg);
                        }
                        return;
                    }

                    //类型
                    /*if(characteristic.getProperties() == 0){
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    }else{
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    }*/

                    //接收写入蓝牙模块的数据onCharacteristicWrite
                    bluetoothGatt.setCharacteristicNotification(characteristic, true);

                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                    //发送大包数据 只有设置成1 才可以
                    byte[] bytes = { 1 };
                    descriptor.setValue(bytes);

                    //这2个属性都不行
                    /* for(BluetoothGattDescriptor dp: characteristic.getDescriptors()){
                        if (dp != null) {
                            if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                                dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                                dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                            }
                            gatt.writeDescriptor(dp);
                        }
                    }*/


                    bluetoothGatt.writeDescriptor(descriptor);

                    initOk = true;
                    Log.e("BleBlueTooth", "BluetoothGattCallback onServicesDiscovered initOk");

                    //服务OK
                    if (connectHandler != null) {
                        Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_CONNECT_SERVICES_OK, "连接服务成功!");
                        msg.obj = bluetoothGatt.getDevice().getAddress();
                        connectHandler.sendMessage(msg);
                    }

                } else {
                    Log.e("BleBlueTooth", "BluetoothGattCallback onServicesDiscovered FAIL");
                    //发现失败
                    initOk = false;
                    if(connectHandler!=null){
                        Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_CONNECT_SERVICES_FAIL,"连接服务失败!");
                        connectHandler.sendMessage(msg);
                    }
                }

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                byte[] data = characteristic.getValue();
                Log.e("BleBlueTooth", "onCharacteristicRead BLE数据被读取:"+ AbBluetoothHexUtil.toHexString(data,true));

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic, int status) {
                byte[] data = characteristic.getValue();
                Log.e("BleBlueTooth", "[onCharacteristicWrite 发送]:"+ AbBluetoothHexUtil.toHexString(data,true));
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt,mtu,status);
                Log.e("BleBlueTooth","onMtuChanged mtu="+mtu+",status="+status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    packageSize = mtu - 3;
                    if(connectHandler!=null){
                        Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_MTU,"MTU设置成功了：" +packageSize);
                        connectHandler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {

                byte[] data = characteristic.getValue();
                Log.e("BleBlueTooth", "[返回]:" + AbBluetoothHexUtil.toHexString(data,true));
                timerHandler.removeCallbacks(timeoutRunable);

                if(bleAdapter != null){
                    bleAdapter.onReceiveValue(data);
                }
            }
        };

    }

    /**
     *
     * 写入数据
     * @param data 16进制数据字符串
     * @param callback 成功失败的回调
     */
    public void writeData(String data,final AbBluetoothWriteCallback callback){
        writeData(data, AbBluetoothConfig.readTimeout,callback);
    }

    /**
     * 写入数据
     * @param data  16进制数据字符串
     * @param timeout 超时时间
     * @param callback 写数据回调
     */
    public void writeData(String data,int timeout,final AbBluetoothWriteCallback callback){
        try {
            Log.e("BleBlueTooth","[发送]:"+data);

            //转换
            String[] strArray = data.split(" ");

            //转byte[]
            byte[] dataByte = new byte[strArray.length];
            for(int i = 0;i<strArray.length;i++){
                dataByte[i] = Integer.valueOf(strArray[i], 16).byteValue();
            }

            //分包
            sendDataForPackage(dataByte,0);

            if(timeoutRunable != null){
                timerHandler.removeCallbacks(timeoutRunable);
            }

            if(timeout > 0){
                //启动指令计时器
                timeoutRunable = new  Runnable() {
                    @Override
                    public void run() {
                        Log.e("BleBlueTooth","指令超时");
                        callback.fail(-1,"指令超时");
                    }
                };
                Log.e("BleBlueTooth","指令超时开始计时");
                timerHandler.postDelayed(timeoutRunable, timeout);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入数据到蓝牙设备
     * @param data
     */
    public void writeBle(byte[] data) {

        //设置要发送的数据内容
        boolean setv = characteristic.setValue(data);
        Log.e("BleBlueTooth", "characteristic setValue:" + setv);

        //往蓝牙模块写入数据
        boolean status = bluetoothGatt.writeCharacteristic(characteristic);
        Log.e("BleBlueTooth", "write characteristic status:" + status);

    }

    /**
     * 递归分包发送数据
     * @param data 数据
     * @param num  包号
     */
    public void  sendDataForPackage(byte[] data,int num){

        int packageCount = data.length/packageSize;
        if(data.length%packageSize > 0){
            packageCount = packageCount+1;
        }

        byte[] buffer = new byte[packageSize];
        if(packageCount == 1){
            //刚好只有一个包
            writeBle(data);
            return;
        }else{
            if(packageCount == num + 1){
                //最后一个包
                buffer = new byte[data.length%packageSize];
            }
        }

        for (int i = num * packageSize; i < data.length; i++) {
            byte b = data[i];
            if (i - num * packageSize+1 < buffer.length) {
                buffer[i - num * packageSize] = b;
            } else if (i - num * packageSize+1 ==  buffer.length) {
                buffer[i - num * packageSize] = b;
                //将buffer数据发送
                writeBle(buffer);
                //有下一个包
                if(num +1  < packageCount){
                    try {
                        Thread.sleep(300);
                    }catch (Exception e){

                    }
                    sendDataForPackage(data,num+1);
                }

                return;
            }

        }
    }


    /**
     * 设置连接回调Handler
     * @param connectHandler
     */
    public void setConnectHandler(Handler connectHandler) {
        this.connectHandler = connectHandler;
    }

    /**
     * 设置连接回调Handler
     * @param resultHandler
     */
    public void setResultHandler(Handler resultHandler) {
        this.resultHandler = resultHandler;
        if(this.bleAdapter != null){
            this.bleAdapter.resultHandler = resultHandler;
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean setMTU(int mtu){
        Log.e("BleBlueTooth","setMTU "+mtu);
        boolean ret =   bluetoothGatt.requestMtu(mtu);
        Log.e("BleBlueTooth","requestMTU "+mtu+" ret="+ret);
        if(connectHandler!=null){
            Message msg = connectHandler.obtainMessage(AbBluetoothMessage.MSG_CONNECT_RESULT_MTU,"MTU设置完成!");
            connectHandler.sendMessage(msg);
        }
        return ret;

    }

    /**
     * 清除回调Handler
     */
    public void clearHandler(){
        connectHandler = null;
        resultHandler = null;
    }

    /**
     * 获取数据适配器
     */
    public AbBluetoothAdapter getBleAdapter() {
        return bleAdapter;
    }

    /**
     * 为这个连接定义数据适配器
     * @param bleAdapter
     */
    public void setBleAdapter(AbBluetoothAdapter bleAdapter) {
        bleAdapter.setBlueTooth(this);
        this.bleAdapter = bleAdapter;
    }
}
