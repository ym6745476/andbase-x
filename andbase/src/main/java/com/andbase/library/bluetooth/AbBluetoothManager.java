package com.andbase.library.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import androidx.annotation.RequiresPermission;
import android.util.Log;
import com.andbase.library.bluetooth.callback.AbBluetoothInitCallback;
import com.andbase.library.bluetooth.callback.AbBluetoothScanCallback;
import com.andbase.library.utils.AbAppUtil;
import com.andbase.library.utils.AbStrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/04/13 09:13
 * Email 396196516@qq.com
 * Info 蓝牙连接管理类
 */
public class AbBluetoothManager {

    private AbBluetoothConfig scanConfig = new AbBluetoothConfig();
    private BluetoothLeScanner scanner = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private List<AbBlueTooth> connectedBlueTooth = new ArrayList<AbBlueTooth>();
    public Handler timerHandler = new Handler();
    public Runnable timeoutRunable = null;
    private ScanCallback scanCallback;
    private BluetoothAdapter.LeScanCallback scanCallback2;

    /**
     * 初始化
     * @param context
     * @param callback
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public void  init(Context context, AbBluetoothInitCallback callback){
        boolean flag = true;
        if (Build.VERSION.SDK_INT >= 23) {
            flag = AbAppUtil.requestBlueToothPermission((Activity)context, AbBluetoothMessage.MSG_BLE_REQUEST_CODE);
        }
        if(flag){
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();

            if (bluetoothAdapter == null){
                if(callback!=null){
                    callback.fail(-1,"设备不支持蓝牙");
                }
            }else  if (!bluetoothAdapter.isEnabled()) {
                if(callback!=null) {
                    callback.fail(-2, "设备未打开蓝牙");
                }
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity)context).startActivityForResult(enableBtIntent, AbBluetoothMessage.MSG_BLE_ENABLE_REQUEST_CODE);
            }else{
                if(callback!=null) {
                    callback.success();
                }
            }
        }else{
            if(callback!=null) {
                callback.fail(-1, "蓝牙权限未被允许");
            }
        }

    }


    /**
     * 开始扫描
     * @param context
     * @param callback
     */
    @RequiresPermission(allOf  = {
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
    })
    public void scan(Context context, final AbBluetoothScanCallback callback){

        init(context, new AbBluetoothInitCallback() {
            @Override
            @RequiresPermission(allOf  = {
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
            })
            public void success() {

                /*scanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        ScanRecord scanRecord = result.getScanRecord();
                        SparseArray<byte[]> mandufacturerData = scanRecord.getManufacturerSpecificData();

                        for (int i = 0; i < mandufacturerData.size(); i++) {
                            int key = mandufacturerData.keyAt(i);
                            byte[] data = mandufacturerData.get(key);

                            String keyHexStr = String.format("%04x",key);
                            Log.i("BleManager","scan andufacturerData2: key=" + keyHexStr + ",value=" + BleHexUtils.toHexString(data,true));
                        }


                        if(result.getDevice()!= null && !AbStrUtil.isEmpty(result.getDevice().getName())){
                            Log.i("BleManager","scan:" + result.getDevice().getAddress() + ":" + result.getDevice().getName());
                            callback.success(result.getDevice(),result.getRssi());
                        }

                    }


                };*/

                scanCallback2 =  new BluetoothAdapter.LeScanCallback() {
                    @Override
                    @RequiresPermission(Manifest.permission.BLUETOOTH)
                    public void onLeScan(BluetoothDevice device, int i, byte[] bytes) {
                        if(!AbStrUtil.isEmpty(device.getName())){
                            Log.i("BleManager","scan:" + device.getAddress() + ":" + device.getName());
                            callback.success(device,i);
                        }
                    }
                };

                //scanner = bluetoothAdapter.getBluetoothLeScanner();
                bluetoothAdapter.startLeScan(scanConfig.scanService,scanCallback2);

                if(scanner!=null){
                    List<ScanFilter> bleScanFilters = new ArrayList<>();
                    if(scanConfig.scanService!= null && scanConfig.scanService.length > 0){
                        for(UUID uuid:scanConfig.scanService){
                            bleScanFilters.add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(uuid.toString())).build());
                        }
                        ScanSettings scanSettings = new ScanSettings.Builder().build();
                        scanner.startScan(bleScanFilters,scanSettings,scanCallback);
                    }else{
                        scanner.startScan(scanCallback);
                    }
                }


                //启动计时器
                timeoutRunable = new  Runnable() {
                    @Override
                    @RequiresPermission(allOf  = {
                            android.Manifest.permission.BLUETOOTH,
                            android.Manifest.permission.BLUETOOTH_ADMIN,
                    })
                    public void run() {
                        stopScan();
                        Log.e("BleManager","搜索计时器：超时");
                        callback.fail(0,"搜索完成");
                    }
                };
                Log.e("BleManager","搜索计时器：开始...");
                timerHandler.removeCallbacks(timeoutRunable);
                timerHandler.postDelayed(timeoutRunable, AbBluetoothConfig.scanTimeout);
            }

            @Override
            public void fail(int code, String message) {

            }
        });

    }

    /**
     * 停止扫描
     */
    @RequiresPermission(allOf  = {
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
    })
    public void stopScan(){
        if(timerHandler!= null){
            timerHandler.removeCallbacks(timeoutRunable);
        }

        if(scanner!=null){
            scanner.stopScan(scanCallback);
        }
        if(scanCallback2!=null){
            bluetoothAdapter.stopLeScan(scanCallback2);
        }
        bluetoothAdapter.cancelDiscovery();
    }

    /**
     * 将蓝牙连接添加到管理器中
     * @param blueToothDevice
     */
    public void addBlueTooth(AbBlueTooth blueToothDevice){
        if (connectedBlueTooth.size() > 0) {
            for (int i = 0; i < connectedBlueTooth.size(); i++) {
                AbBlueTooth blueToothTemp = connectedBlueTooth.get(i);
                blueToothTemp.closeConnect();
                if (blueToothTemp.device.getAddress().equals(blueToothDevice.device.getAddress())){
                    connectedBlueTooth.set(i,blueToothDevice);
                }
            }

        }else{
            connectedBlueTooth.add(blueToothDevice);
        }
    }

    /**
     * 获取管理器中的连接
     * 实际场景只维护了一个链接
     * @return
     */
    public AbBlueTooth getBlueTooth(){
        if(connectedBlueTooth.size() > 0){
            return connectedBlueTooth.get(0);
        }
        return null;
    }


    /**
     * 关闭所有设备
     */
    public void  stopAllDevice(){
        for(int i=0;i<connectedBlueTooth.size();i++){
            AbBlueTooth blueToothDevice = connectedBlueTooth.get(i);
            blueToothDevice.closeConnect();
        }
        connectedBlueTooth.clear();

    }

}
