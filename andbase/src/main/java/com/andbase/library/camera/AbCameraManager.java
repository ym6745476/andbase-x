package com.andbase.library.camera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.andbase.library.utils.AbAppUtil;
import com.andbase.library.utils.AbLogUtil;

public final class AbCameraManager {

	private Context context;
	private static AbCameraManager cameraManager;
	private AbCameraConfiguration configManager;
	private Camera camera;
    private SurfaceHolder surfaceHolder = null;
    private boolean previewing = false;
	private Camera.AutoFocusCallback autoFocusCallback;
	private Camera.PreviewCallback previewCallback;
	private Camera.PictureCallback pictureCallback;
	private SurfaceView surfaceView = null;
	private boolean surfaceCreated = false;
	private Handler handler = null;

	/** 横屏0 竖屏1 */
	public int orientation = 0;
	/** 前后摄像 1是前 0是后 */
	public int cameraId = 0;
	/** 对焦模式 1连续对焦  0 自动对焦 */
	public int focusMode = 0;

    /** 解决界面上的其他操作返回直接弹出获取权限框的问题 */
    public boolean paused = false;

	/** 双指缩放 */
	private float lastDistance;
	/** 默认触控误差值 */
	private static final int DEVIATION = 6;


	public SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder surfaceHolder) {
			Log.e("AbCameraManager", "surfaceCreated...");
            surfaceCreated = true;

            if(!paused){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean result = AbAppUtil.hasPermission(context,Manifest.permission.CAMERA);
                    if(result){
                        initCamera();
                    }
                }else{
                    initCamera();
                }
            }

		}

		public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
		}

		public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
			Log.e("AbCameraManager", "surfaceDestroyed...");
            surfaceCreated = false;
		}
	};

	public static AbCameraManager init(Context context, SurfaceView surfaceView) {
		cameraManager = new AbCameraManager(context,surfaceView);
		return cameraManager;
	}

	private AbCameraManager(final Context context, SurfaceView surfaceView) {
		this.context = context;
		this.surfaceView = surfaceView;
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(callback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		autoFocusCallback = new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				AbLogUtil.i(context,"AutoFocus Callback:" + success);
			}
		};

	}

	/**
	 * 打开相机.
	 */
	public void initCamera() {
		try {
            configManager = new AbCameraConfiguration(context,cameraManager);
			cameraManager.openDriver(surfaceHolder, cameraId);
			cameraManager.startPreview();
		}catch (Exception e) {
			e.printStackTrace();
			if(handler!=null){
				Message message = handler.obtainMessage(-1,"调用摄像头失败，请检查应用权限！");
				handler.sendMessage(message);
			}
		}
	}

    /**
     * 恢复预览相机.
     */
    public void initSurfaceHolder() {
       if(surfaceCreated){
           initCamera();
       }
    }

	/**
	 * 打开相机
	 *
	 * @param holder
	 * @param cameraID
	 * @throws IOException
	 */
	public void openDriver(SurfaceHolder holder, int cameraID)
			throws IOException {
		if (camera == null) {
			camera = Camera.open(cameraID);
			if (camera == null) {
				throw new IOException();
			}
			if(holder!=null){
				camera.setPreviewDisplay(holder);
			}

			//
			if (orientation == 1) {
				camera.setDisplayOrientation(90);
			}

            // 设置预览参数
			configManager.initFromCameraParameters(camera);
			configManager.setDesiredCameraParameters(camera,cameraID);

		}
	}

	/**
	 * 关闭
	 */
	public void closeDriver() {
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	/**
	 * 开始预览
	 */
	public void startPreview() {
		if (camera != null && !previewing) {
			Log.e("Camera", "相机开始预览");
			if(previewCallback!=null){
				camera.setPreviewCallback(previewCallback);
			}
			camera.startPreview();
			previewing = true;
			if(focusMode == 0){
				requestAutoFocus(null);
			}else{
				camera.cancelAutoFocus();
			}
		}else{
			if(focusMode == 0) {
				this.requestAutoFocus(null);
			}
		}
	}

	/**
	 * 停止预览
	 */
	public void stopPreview() {
		if (camera != null && previewing) {
			Log.e("Camera", "相机停止预览");
			camera.setPreviewCallback(null);
			camera.stopPreview();
			previewing = false;
		}
	}

	/**
	 * 开始对焦
	 */
	public void requestAutoFocus(MotionEvent event) {
		try {
			if (camera != null && previewing) {
				if(event == null){
					camera.autoFocus(autoFocusCallback);
				}else{
					this.configManager.focusOnTouch(camera,event,autoFocusCallback);
				}


			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拍照
	 */
	public void takePicture() {
		camera.takePicture(null, null, this.pictureCallback);
	}

	/**
	 * 闪光灯
	 */
	public void toogleFlash() {
		Parameters parameters = camera.getParameters();
		if(parameters.getFlashMode().equals(Parameters.FLASH_MODE_OFF)){
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		}else{
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		}
		camera.setParameters(parameters);
	}

	public void onTouchEvent(MotionEvent event){
		if(camera!=null && previewing){
			//多点触控
			if(event.getPointerCount() > 1) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_POINTER_DOWN:
						lastDistance = configManager.calcFingerSpacing(event);
						break;
					case MotionEvent.ACTION_MOVE:
						float newDistance = configManager.calcFingerSpacing(event);

						if (newDistance > lastDistance + DEVIATION) {
							configManager.handleZoom(camera,true);
						} else if (newDistance < lastDistance - DEVIATION) {
							configManager.handleZoom(camera,false);
						}
						lastDistance = newDistance;
						break;
				}
			}else {
				if(cameraManager.focusMode == 0){
					cameraManager.requestAutoFocus(event);
				}
			}
		}
	}

	public Camera getCamera() {
		if (camera != null) {
			return camera;
		}
		return null;
	}

	public AbCameraConfiguration getConfigManager() {
		return this.configManager;
	}

	public Point getPreviewSize() {
		return configManager.getCameraResolution();
	}

	public Point getPictureSize() {
		return configManager.getPictureResolution();
	}

	public Point getScreenResolution() {
		return configManager.getScreenResolution();
	}

	public Camera.AutoFocusCallback getAutoFocusCallback() {
		return autoFocusCallback;
	}

	public void setAutoFocusCallback(Camera.AutoFocusCallback autoFocusCallback) {
		this.autoFocusCallback = autoFocusCallback;
	}

	public Camera.PreviewCallback getPreviewCallback() {
		return previewCallback;
	}

	public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
		this.previewCallback = previewCallback;
	}

	public Camera.PictureCallback getPictureCallback() {
		return pictureCallback;
	}

	public void setPictureCallback(Camera.PictureCallback pictureCallback) {
		this.pictureCallback = pictureCallback;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

	public boolean isPreviewing() {
		return previewing;
	}

	public void setPreviewing(boolean previewing) {
		this.previewing = previewing;
	}
}

