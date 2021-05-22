package com.andbase.qrcode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import com.andbase.library.camera.AbCameraManager;
import com.andbase.library.utils.AbLogUtil;
import com.andbase.library.utils.AbStrUtil;
import com.google.zxing.ResultPoint;

import java.util.List;

public class AbDecodeThread extends Thread {

    /** The AbCameraManager. */
    private AbCameraManager cameraManager;

    /** The handler. */
    private Handler handler;

    /** The frameRect. */
    private Rect frameRect;

    /** The data. */
    private byte[] data;

    /** The width. */
    private int width;

    /** The height. */
    private int height;

    /** The flag. */
    private boolean flag = true;

    /** The doing. */
    private boolean doing = false;

    /** The count. */
    private int count = 0;

    private AbResultPointCallback resultPointCallback;

    public AbDecodeThread(final AbCameraManager cameraManager, Handler handler) {
        this.cameraManager = cameraManager;
        this.handler = handler;
        this.resultPointCallback = new AbResultPointCallback() {
            @Override
            public void foundResultPoints(List<ResultPoint> resultPoints) {
                AbLogUtil.i("foundResultPoints","resultPoints:" + resultPoints.size());
                if(resultPoints.size() >= 3){
                    float distance1 = ResultPoint.distance(resultPoints.get(0),resultPoints.get(1));
                    float distance2 = ResultPoint.distance(resultPoints.get(1),resultPoints.get(2));
                    float distance3 = ResultPoint.distance(resultPoints.get(0),resultPoints.get(2));
                    int maxDistance = (int)Math.max(Math.max(distance1,distance2),distance3);
                    cameraManager.getConfigManager().handleAutoZoom(maxDistance,width);
                }
            }
        };
    }

    /**
     * 线程同步方法.
     * @param data
     */
    public synchronized void setData(byte[] data) {
        this.data = data;
        this.notify();
    }

    @Override
    public void run() {
        while (flag) {
            try {
                // 等待结果
                synchronized (this) {
                    this.wait();
                }
                // 开始识别
                doing = true;
                decode(data, width, height);
            } catch (Exception e) {
                e.printStackTrace();
                doing = false;
            }
        }
    }


    private void decode(byte[] data, int width, int height) {

        try {
            String code =  AbDecodeUtil.decode(data,width,height,this.frameRect,resultPointCallback);
            if(!AbStrUtil.isEmpty(code)){
                if(this.handler!=null){
                    count = 0;
                    Message message = Message.obtain(handler, 1, code);
                    message.sendToTarget();
                }
            }else{
                doing = false;
                if(count > 1){
                    cameraManager.requestAutoFocus(null);
                    count = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            doing = false;
        }
        count ++;

    }


    public void closeDecodeThread() {
        this.flag = false;
        this.doing = false;
        this.handler = null;
        try {
            this.interrupt();
        } catch (Exception e) {
        }
    }

    public boolean isDoing() {
        return doing;
    }

    public void setDoing(boolean doing) {
        this.doing = doing;
    }

    public byte[] getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Rect getFrameRect() {
        return frameRect;
    }

    public void setFrameRect(Rect frameRect) {
        this.frameRect = frameRect;
    }
}
