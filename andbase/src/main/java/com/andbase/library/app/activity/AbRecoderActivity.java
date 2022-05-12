package com.andbase.library.app.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.andbase.library.R;

import com.andbase.library.app.base.AbBaseActivity;
import com.andbase.library.camera.AbCameraManager;
import com.andbase.library.utils.AbAppUtil;
import com.andbase.library.utils.AbFileUtil;
import com.andbase.library.utils.AbToastUtil;


import java.io.File;
import java.util.Random;

/**
 *  Intent intent = new Intent(DiscoveryActivity.this, RecoderActivity.class);
 *  intent.putExtra("cameraId", 0);
 *  intent.putExtra("orientation", 1);
 *  startActivity(intent);
 *
 * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *   if(requestCode == RecoderActivity.REQUEST_CODE_RECODER){
 *      Bundle bundle = data.getExtras();
 *      String path = bundle.getString("path");
 *      AbLogUtil.i("开始Route to video：",path);
 *      ActivityController.toVideoAdd(MainActivity.this,path);
 *   }
 * }
 */
public class AbRecoderActivity extends AbBaseActivity{

    /** 录像请求. */
    public static int REQUEST_CODE_RECODER = 2;

	/** AbCameraManager. */
	public AbCameraManager cameraManager = null;

	/** SurfaceView. */
	public SurfaceView surfaceView = null;
	
	/** 录制按钮. */
	private Button startBtn;
	
	/** 录制. */
	private MediaRecorder mediaRecorder;
	
	/** 录制的文件. */
	private File videoFile;

	/** 录制开关. */
	private boolean isRecoding = false;
	
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
		if(orientation==0){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		setContentView(R.layout.ab_activity_recoder);

		setToolbarView("",true);

		// 初始化CameraManager
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		cameraManager = AbCameraManager.init(getApplication(),surfaceView);
		cameraManager.cameraId = cameraId;
		cameraManager.orientation = orientation;
		cameraManager.focusMode = 0;
		
		//UI相关
		startBtn = (Button)this.findViewById(R.id.start_button);

		startBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(isRecoding){
					startBtn.setBackgroundResource(R.drawable.ic_start_record);
					isRecoding = false;
					stopRecorder();
					Intent intent = new Intent();
					intent.putExtra("path", videoFile.getPath());
					setResult(RESULT_OK,intent);
					finish();
				}else{
                    startBtn.setBackgroundResource(R.drawable.ic_stop_record);
					isRecoding = true;
					startRecorder();
				}

			}
		});
	}


	/**
	 * 暂停,将相机关闭.
	 */
	@Override
	public void onPause() {
        if(isRecoding){
            startBtn.setBackgroundResource(R.drawable.ic_start_record);
            isRecoding = false;
            stopRecorder();
        }
		stopCameraManager();
		Log.e("onPause", "onPause  相机界面暂停");
		super.onPause();
	}
	
	/**
	 * 恢复.
	 */
	@Override
	public void onResume() {

		//检查权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           boolean result =  AbAppUtil.requestPermissions(this,new String []{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE
            },REQUEST_CODE_RECODER);
            if(result){
                startCameraPreview();
            }
		}else{
            startCameraPreview();
		}

		//关键代码：恢复数据
		Log.e("onResume", "onResume  相机界面恢复");
		super.onResume();
		
	}

	public void startCameraPreview(){
		cameraManager.setHandler(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == -1){
					AbToastUtil.showToast(AbRecoderActivity.this,(String)msg.obj);
					finish();
				}
			}
		});
		cameraManager.initSurfaceHolder();
	}

	public void stopCameraManager() {
		try{
			cameraManager.stopPreview();
			cameraManager.closeDriver();
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * 完成.
	 */
	@Override
	public void finish() {
		super.finish();
	}
    
    public void startRecorder(){
		try {
			
			// 创建保存录制视频的视频文件
			String photoDir = AbFileUtil.getImageDownloadDir(AbRecoderActivity.this);
			String fileName = "video_"+new Random().nextInt(1000) + "-" + System.currentTimeMillis() + ".mp4";

			videoFile = new File(photoDir, fileName);

	        try {
				if(videoFile.exists()){
					videoFile.delete();
				}
	   
				if(!videoFile.getParentFile().exists()){
					videoFile.getParentFile().mkdirs();
				}
				if(!videoFile.exists()){
					videoFile.createNewFile();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 创建MediaPlayer对象  
	        cameraManager.getCamera().unlock();
			mediaRecorder = new MediaRecorder();  
			mediaRecorder.reset();  
			mediaRecorder.setCamera(cameraManager.getCamera());
			// 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)  
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
			// 设置从摄像头采集图像  
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);  
			// 设置视频文件的输出格式  
			// 必须在设置声音编码格式、图像编码格式之前设置  
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  
			// 设置声音编码的格式  
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
			// 设置图像编码的格式  
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  
			 
			mediaRecorder.setOutputFile(videoFile.getAbsolutePath());  
			// 指定使用SurfaceView来预览视频
			mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
			mediaRecorder.prepare();  
			// 开始录制  
			mediaRecorder.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void  stopRecorder(){
		try {
			if(mediaRecorder!=null){
				// 停止录制  
			    mediaRecorder.stop();  
			    // 释放资源  
			    mediaRecorder.release();  
			    mediaRecorder = null;  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_RECODER && AbAppUtil.hasAllPermissionsGranted(grantResults)) {
		} else {
			Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
			finish();
		}
	}

}