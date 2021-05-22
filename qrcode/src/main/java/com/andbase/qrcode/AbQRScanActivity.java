package com.andbase.qrcode;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andbase.library.app.base.AbBaseActivity;
import com.andbase.library.app.global.AbAudioManager;
import com.andbase.library.camera.AbCameraManager;
import com.andbase.library.camera.AbScanRectView;
import com.andbase.library.utils.AbAppUtil;
import com.andbase.library.utils.AbToastUtil;
import com.andbase.library.utils.AbViewUtil;
import com.upu173.qrcode.R;

/**
 *  Intent intent = new Intent(MainActivity.this, QRCameraActivity.class);
 *  intent.putExtra("cameraId", 0);
 *  intent.putExtra("orientation", 1);
 *  startActivityForResult(intent,1);
 *
 *
 *
 *  @Override
 *  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *  *  if (resultCode == RESULT_OK) {
 *  *  *  if(requestCode == 1){
 *  *  *  *  Bundle bundle = data.getExtras();
 *  *  *  *  String qrcode = bundle.getString("qrcode");
 *  *  *  *  ToastUtil.success(DiscoveryActivity.this,qrcode);
 *  *  *  }
 *  *  }
 *  *  super.onActivityResult(requestCode, resultCode, data);
 *  }
 */
public class AbQRScanActivity extends AbBaseActivity {

	/** 扫码请求. */
	public static final int REQUEST_CODE_QR = 3;

	/** 返回数据的KEY. */
	public static final String RESULT = "SCAN_RESULT";

    /** 摄像头权限请求 */
    public static final int REQUEST_CODE_CAMERA = 1;

	/** AbCameraManager. */
	public AbCameraManager cameraManager = null;

	/** 操作按钮. */
	private ImageView flashBtn;

	/** 闪光灯. */
	private boolean isFlashOn = false;

	/** 预览区. */
	private AbScanRectView scanRectView;

	/**识别线程*/
	private AbDecodeThread thread = null;


	private AbAudioManager audioManager;


	/**识别结果*/
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case 1:
					//成功
                    String message = (String)msg.obj;
					audioManager.playBeepSoundAndVibrate();
					stopCameraManager();
					Intent intent = new Intent();
					intent.putExtra(AbQRScanActivity.RESULT, message);
					setResult(RESULT_OK, intent);
					finish();
					break;
				case -1:
					//关键代码：开始数据接收
					thread.setDoing(false);
					break;
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * 开始.
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//屏幕参数
		int cameraId = this.getIntent().getIntExtra("cameraId", 0);
		int orientation = this.getIntent().getIntExtra("orientation", 0);

		//强制为横屏
		if(orientation == 0){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		setContentView(R.layout.activity_qr_code);

		setToolbarView("",true);

		// 初始化CameraManager
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		cameraManager = AbCameraManager.init(getApplication(),surfaceView);
		cameraManager.cameraId = cameraId;
		cameraManager.orientation = orientation;
		cameraManager.focusMode = 0;
		scanRectView = (AbScanRectView) findViewById(R.id.scan_rect_view);

		//UI相关
		flashBtn = (ImageView) this.findViewById(R.id.flash_btn);

		if(cameraId==1){
			flashBtn.setVisibility(View.GONE);
		}

		//拍照的回调
		cameraManager.setPictureCallback(new Camera.PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				//关闭预览
				cameraManager.stopPreview();
			}
		});

		// 预览
		cameraManager.setPreviewCallback(new Camera.PreviewCallback() {

			@Override
			public void onPreviewFrame(final byte[] data, Camera camera) {

				//关键代码：数据更新
				if(thread!=null && !thread.isDoing()){
					thread.setData(data);
				}

				new Handler().post(new Runnable() {
					@Override
					public void run() {
						scanRectView.invalidate();
					}
				});
			}
		});


		//拍照
		/*camBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cameraManager.takePicture();
				//声音
				shootSound();
			}
		});*/


		//闪光灯
		flashBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isFlashOn = !isFlashOn;
				v.setSelected(isFlashOn);
				cameraManager.toogleFlash();
			}
		});

		audioManager = new AbAudioManager(this);
		audioManager.initAudio(R.raw.beep);

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)flashBtn.getLayoutParams();
		DisplayMetrics displayMetrics = AbAppUtil.getDisplayMetrics(this);
		int frameWidth = (int)(displayMetrics.widthPixels * 0.6);
		lp.topMargin = (displayMetrics.heightPixels-frameWidth)/2 + frameWidth - (int) AbViewUtil.dip2px(this,35.0f);
		flashBtn.setLayoutParams(lp);

	}


	/**
	 * 设置扫描框尺寸
	 */
	public void initScanRectView2(){
		int width = scanRectView.getMeasuredWidth();
		int height = scanRectView.getMeasuredHeight();
		Rect frameRect = new Rect();
		int frameWidth = (int)(width * 0.6);
		int frameHeight = (int)(frameWidth * 2);
		frameRect.set((int)(width*0.2),(height-frameHeight)/2,width-(int)(width*0.2),(height-frameHeight)/2 + frameHeight);
		scanRectView.setFrameRect(frameRect);
		scanRectView.setTips("请将试剂卡对准拍照区域");
	}


	/**
	 * 暂停,将相机关闭.
	 */
	@Override
	protected void onPause() {
		stopCameraManager();
		Log.e("QRCameraActivity", "onPause  相机界面暂停");
		super.onPause();
	}

	/**
	 * 恢复.
	 */
	@Override
	protected void onResume() {
        Log.e("QRCameraActivity", "onResume  相机界面恢复");
        //检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			boolean result =  AbAppUtil.requestCameraPermission(AbQRScanActivity.this,REQUEST_CODE_CAMERA);
            if(result){
                startCameraPreview();
            }
        }else{
            startCameraPreview();
        }

		super.onResume();

	}

	@Override
	public void finish() {
		if(audioManager!=null){
			audioManager.release();
		}

		super.finish();
	}

	public void stopCameraManager() {
		try{
			cameraManager.stopPreview();
			cameraManager.closeDriver();
			if(thread!=null){
				thread.closeDecodeThread();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void startCameraPreview() {

		cameraManager.setHandler(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == -1){
					AbToastUtil.showToast(AbQRScanActivity.this,(String)msg.obj);
					finish();
				}
			}
		});

        cameraManager.initSurfaceHolder();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try{
					//设置扫描框尺寸
					//initScanRectView2();
					scanRectView.initQRScanRectView("将二维码放入框内，即可自动扫描");
					Point previewSize = cameraManager.getPreviewSize();
					Rect rect = scanRectView.getFrameRect();
					if(previewSize!=null){
						//识别线程
						thread = new AbDecodeThread(cameraManager,handler);
						//关键代码：设置尺寸
						thread.setWidth(previewSize.x);
						thread.setHeight(previewSize.y);
						thread.setFrameRect(rect);
						thread.start();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}, 1000);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		cameraManager.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_CAMERA){
			if(AbAppUtil.hasAllPermissionsGranted(grantResults)){

			}else{
				Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

}