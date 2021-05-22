package com.andbase.qrcode;

import com.google.zxing.ResultPoint;

import java.util.List;

public interface AbResultPointCallback {
    void foundResultPoints(List<ResultPoint> points);
}
