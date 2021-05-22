package com.andbase.library.bluetooth.callback;

import android.bluetooth.BluetoothDevice;

public interface AbBluetoothScanCallback {
    void success(BluetoothDevice device, int rssi);
    void fail(int code, String message);
}
