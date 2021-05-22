package com.andbase.library.camera;


import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class AbCameraConfiguration {

	private static final String TAG = "CameraConfiguration";

	private final Context context;
	private AbCameraManager cameraManager;
	private Point screenResolution;
	private Point cameraResolution;
	private Point pictureResolution;
	private long lastZoomTime;

	public AbCameraConfiguration(Context context, AbCameraManager cameraManager) {
		this.context = context;
		this.cameraManager = cameraManager;
	}

	/**
	 * 初始化相机参数. cameraResolution，screenResolution
	 */
	public void initFromCameraParameters(Camera camera) {
		Parameters parameters = camera.getParameters();
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		screenResolution = new Point(display.getWidth(), display.getHeight());
		// preview size is always something like 480*320, other 320*480
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;
		// preview size is always something like 480*320, other 320*480
		//
		if (cameraManager.orientation == 1) {
			if (screenResolution.x < screenResolution.y) {
				screenResolutionForCamera.x = screenResolution.y;
				screenResolutionForCamera.y = screenResolution.x;
			}
		}

		Log.d(TAG, "Screen resolution: " + screenResolutionForCamera);
		initCameraResolution(parameters,screenResolutionForCamera);

	}

	/**
	 * 设置相机参数
	 *
	 * @param camera
	 */
	public void setDesiredCameraParameters(Camera camera,int cameraID) {
		Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		parameters.setPictureSize(pictureResolution.x, pictureResolution.y);
		Size size = parameters.getPreviewSize();
		Log.e(TAG, "camera default previewSize: " + size.width + ","+ size.height);

		Size size1 = parameters.getPictureSize();
		Log.e(TAG, "camera default pictureSize: " + size1.width + ","+ size1.height);

		parameters.setPictureFormat(ImageFormat.JPEG);
		//parameters.setJpegQuality(85);
		parameters.setPreviewFormat(ImageFormat.NV21);
		Log.e(TAG, "camera setPreviewSize: " + cameraResolution.x + ","+ cameraResolution.y);
		if(cameraID == 0){
			//对焦模式
			if(cameraManager.focusMode == 0){
				parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			}else{
				parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}

		}

		parameters.setZoom(0);
		setFlash(camera,false);
		camera.setParameters(parameters);
	}

	private void initCameraResolution(Parameters parameters,Point screenResolution) {
		List<Size> list = parameters.getSupportedPreviewSizes();
		List<Size> list2 = parameters.getSupportedPictureSizes();
		for(Size size: list){
			Log.d(TAG, "camera preview-size-values: " +size.width+"x"+size.height);
		}

		cameraResolution = findBestPreviewSizeValue(list,screenResolution);
		pictureResolution = findBestPictureSizeValue(list2,screenResolution);

		Log.e(TAG, "camera best preview-size-values: " + cameraResolution.x+ "," + cameraResolution.y);
		Log.e(TAG, "camera best picture-size-values: " + pictureResolution.x+ "," + pictureResolution.y);
	}

	private Point findBestPreviewSizeValue(
			List<Size> list, Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (Size previewSize : list) {
			int newX = previewSize.width;
			int newY = previewSize.height;

			int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}
		}
		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}

	/**
	 * 比例最相近的最大分辨率
	 * @param list
	 * @param screenResolution
	 * @return
	 */
	private Point findBestPictureSizeValue(
			List<Size> list, Point screenResolution) {

		//比预览的大1倍
		screenResolution = new Point(screenResolution.x*2,screenResolution.y*2);

		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (Size previewSize : list) {
			int newX = previewSize.width;
			int newY = previewSize.height;

			int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}
		}
		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}

	/**
	 *
	 * 设置闪光灯.
	 */
	private void setFlash(Camera camera,boolean open) {
		Parameters parameters = camera.getParameters();
		if(open){
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		}else{
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		}
	}

	/**
	 * 处理变焦缩放
	 * @param camera
	 * @param isZoomIn
	 */
	public void handleZoom(Camera camera,boolean isZoomIn) {
		Camera.Parameters params = camera.getParameters();
		if (params.isZoomSupported()) {
			int maxZoom = params.getMaxZoom();
			int zoom = params.getZoom();
			if (isZoomIn && zoom < maxZoom) {
				zoom++;
			} else if (zoom > 0) {
				zoom--;
			}
			params.setZoom(zoom);
			camera.setParameters(params);
		} else {
			Log.i(TAG, "zoom not supported");
		}
	}

	public boolean handleAutoZoom(int length,int width){
		if(lastZoomTime > System.currentTimeMillis() - 1000){
			return true;
		}
		if(length<width/5){
			Camera camera = cameraManager.getCamera();
			if(camera!=null){
				Camera.Parameters params = camera.getParameters();
				if (params.isZoomSupported()) {
					int maxZoom = params.getMaxZoom();
					int zoom = params.getZoom();
					params.setZoom(Math.min(zoom + maxZoom/5,maxZoom));
					camera.setParameters(params);
					lastZoomTime = System.currentTimeMillis();
					return true;
				} else {
					Log.i(TAG, "Zoom not supported");
				}
			}
		}

		return false;
	}

	/**
	 * 聚焦
	 * @param camera
	 * @param event
	 */
	public void focusOnTouch(Camera camera,MotionEvent event,final Camera.AutoFocusCallback autoFocusCallback) {

		Camera.Parameters params = camera.getParameters();
		Camera.Size previewSize = params.getPreviewSize();

		Rect focusRect = calcTapArea(event.getRawX(), event.getRawY(), 1f,previewSize);
		Rect meteringRect = calcTapArea(event.getRawX(), event.getRawY(), 1.5f,previewSize);
		Camera.Parameters parameters = camera.getParameters();
		if (parameters.getMaxNumFocusAreas() > 0) {
			List<Camera.Area> focusAreas = new ArrayList<>();
			focusAreas.add(new Camera.Area(focusRect, 600));
			parameters.setFocusAreas(focusAreas);
		}

		if (parameters.getMaxNumMeteringAreas() > 0) {
			List<Camera.Area> meteringAreas = new ArrayList<>();
			meteringAreas.add(new Camera.Area(meteringRect, 600));
			parameters.setMeteringAreas(meteringAreas);
		}
		final String currentFocusMode = params.getFocusMode();
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		camera.setParameters(params);

		camera.autoFocus(new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				Camera.Parameters params = camera.getParameters();
				params.setFocusMode(currentFocusMode);
				camera.setParameters(params);
				autoFocusCallback.onAutoFocus(success,camera);
			}
		});

	}


	/**
	 * 计算两指间距离
	 * @param event
	 * @return
	 */
	public float calcFingerSpacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 计算对焦区域
	 * @param x
	 * @param y
	 * @param coefficient
	 * @param previewSize
	 * @return
	 */
	private Rect calcTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
		float focusAreaSize = 200;
		int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
		int centerX = (int) ((x / previewSize.width) * 2000 - 1000);
		int centerY = (int) ((y / previewSize.height) * 2000 - 1000);
		int left = clamp(centerX - (areaSize / 2), -1000, 1000);
		int top = clamp(centerY - (areaSize / 2), -1000, 1000);
		RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
		return new Rect(Math.round(rectF.left), Math.round(rectF.top),
				Math.round(rectF.right), Math.round(rectF.bottom));
	}

	/**
	 * 根据范围限定值
	 * @param x
	 * @param min 范围最小值
	 * @param max 范围最大值
	 * @return
	 */
	private int clamp(int x, int min, int max) {
		if (x > max) {
			return max;
		}
		if (x < min) {
			return min;
		}
		return x;
	}

	public Point getCameraResolution() {
		return cameraResolution;
	}

	public Point getScreenResolution() {
		return screenResolution;
	}

	public Point getPictureResolution() {
		return pictureResolution;
	}
}
