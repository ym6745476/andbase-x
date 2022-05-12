package com.andbase.library.app.activity;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andbase.library.R;

import com.andbase.library.app.base.AbBaseActivity;
import com.andbase.library.camera.AbCameraManager;
import com.andbase.library.utils.AbAppUtil;
import com.andbase.library.utils.AbImageUtil;
import com.andbase.library.utils.AbToastUtil;


import java.io.OutputStream;

/**
 *  Intent intent = new Intent(RecorderActivity.this, CaptureActivity.class);
 *  intent.putExtra("cameraId", 0);
 *  intent.putExtra("orientation", 1);
 *  startActivity(intent);
 */

public class AbCaptureActivity extends AbBaseActivity {

	/** 拍照权限请求 */
	public static int REQUEST_CODE_CAMERA = 1;

	/** AbCameraManager. */
	public AbCameraManager cameraManager = null;
	
	/** 拍照按钮. */
	private Button camBtn;

	/** 操作按钮. */
	private Button okBtn,cancleBtn;
    private TextView flashBtn;

	/** 布局. */
	private FrameLayout camLayout,previewLayout;

	/** 图片结果. */
	private ImageView imgResult;
	
	/** 当前路径. */
	private String  path = null;

    /** 闪光灯. */
    private boolean isFlashOn = false;

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
		
		setContentView(R.layout.ab_activity_capture);

		setToolbarView("",true);

		// 初始化CameraManager
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		cameraManager = AbCameraManager.init(getApplication(),surfaceView);
		cameraManager.cameraId = cameraId;
		cameraManager.orientation = orientation;
		cameraManager.focusMode = 0;

		//UI相关
		camBtn = (Button)this.findViewById(R.id.shot_btn);
        flashBtn = (TextView) this.findViewById(R.id.flash_btn);
		okBtn = (Button)this.findViewById(R.id.ok_btn);
		cancleBtn = (Button)this.findViewById(R.id.cancle_btn);
		camLayout = (FrameLayout)this.findViewById(R.id.cam_layout);
		previewLayout = (FrameLayout)this.findViewById(R.id.preview_layout);
		camLayout.setVisibility(View.VISIBLE);
		previewLayout.setVisibility(View.GONE);
		imgResult = (ImageView)this.findViewById(R.id.imgResult);
		
		if(cameraId==1){
			flashBtn.setVisibility(View.GONE);
		}
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra("path", path);
				setResult(RESULT_OK,intent); 
				finish();
			}
		});
		
		//拍照的回调
		cameraManager.setPictureCallback(new PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				//关闭预览
				cameraManager.stopPreview();
				
				Bitmap cameraBitmap = AbImageUtil.getBitmap(data,1280,720);
				cameraBitmap = AbImageUtil.rotateBitmap(cameraBitmap,90);

				camLayout.setVisibility(View.INVISIBLE);
				previewLayout.setVisibility(View.VISIBLE);
				imgResult.setImageBitmap(cameraBitmap);


                //插入到相册数据库
                try{
                    String  url = insertImage(AbCaptureActivity.this.getContentResolver(), cameraBitmap, "andbase", "andbase");
                    path = getRealFilePath(AbCaptureActivity.this,Uri.parse(url));
                    Log.e("onPictureTaken", "onPictureTaken insertImage："+path);
					AbToastUtil.showToast(AbCaptureActivity.this,"insertImage:"+path);
                }catch (Exception e){
                    e.printStackTrace();
                }

			}
		});
		
		//对焦的控制
		cameraManager.setAutoFocusCallback(new AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if(success){
					//对焦成功
				}else{

				}
                if(cameraManager.focusMode == 1) {
                    cameraManager.startPreview();
                }
			}
		});
		
		
		
		// 预览
		cameraManager.setPreviewCallback(new PreviewCallback() {
			
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				
			}
		});
		
		
		//拍照
		camBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cameraManager.takePicture();
			}
		});
		
		//点击屏幕
		surfaceView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
                if(cameraManager.focusMode == 0){
                    cameraManager.requestAutoFocus(null);
                }else {
                    cameraManager.startPreview();
                }
			}
		});

        //闪光灯
        flashBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isFlashOn){
                    flashBtn.setText("轻触点亮");
                }else{
                    flashBtn.setText("轻触关闭");
                }
                isFlashOn = !isFlashOn;
                cameraManager.toogleFlash();
            }
        });

		cancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imgResult.setImageBitmap(null);
				camLayout.setVisibility(View.VISIBLE);
				previewLayout.setVisibility(View.INVISIBLE);
				
				cameraManager.startPreview();
			}
		});
		
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
		//检查权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			boolean result =  AbAppUtil.requestCameraPermission(AbCaptureActivity.this,REQUEST_CODE_CAMERA);
			if(result){
				startCameraPreview();
			}
		}else{
			startCameraPreview();
		}

		super.onResume();
		
	}

	public void startCameraPreview(){
		cameraManager.setHandler(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == -1){
					AbToastUtil.showToast(AbCaptureActivity.this,(String)msg.obj);
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

    public String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static final String insertImage(ContentResolver cr, Bitmap source,
                                           String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri url = null;
        String stringUrl = null;

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                } finally {
                    imageOut.close();
                }

            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }
}