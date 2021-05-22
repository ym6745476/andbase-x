package com.andbase.library.bluetooth.callback;

public interface AbBluetoothInitCallback {
    void success();
    void fail(int code, String message);
}
