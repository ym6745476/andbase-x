package com.andbase.library.bluetooth.callback;

public interface AbBluetoothWriteCallback {
    void success();
    void fail(int code, String message);
}
