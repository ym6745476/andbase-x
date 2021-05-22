package com.yanzhenjie.album.app.camera;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;

import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.mvp.BaseActivity;
import com.yanzhenjie.album.util.AlbumUtils;

import java.io.File;


public class CameraActivity extends BaseActivity {

    private static final String INSTANCE_CAMERA_FUNCTION = "INSTANCE_CAMERA_FUNCTION";
    private static final String INSTANCE_CAMERA_FILE_PATH = "INSTANCE_CAMERA_FILE_PATH";
    private static final String INSTANCE_CAMERA_QUALITY = "INSTANCE_CAMERA_QUALITY";
    private static final String INSTANCE_CAMERA_DURATION = "INSTANCE_CAMERA_DURATION";
    private static final String INSTANCE_CAMERA_BYTES = "INSTANCE_CAMERA_BYTES";

    private static final int CODE_PERMISSION_IMAGE = 1;
    private static final int CODE_PERMISSION_VIDEO = 2;

    private static final int CODE_ACTIVITY_TAKE_IMAGE = 1;
    private static final int CODE_ACTIVITY_TAKE_VIDEO = 2;

    public static Action<String> sResult;
    public static Action<String> sCancel;

    private int mFunction;
    private String mCameraFilePath;
    private int mQuality;
    private long mLimitDuration;
    private long mLimitBytes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mFunction = savedInstanceState.getInt(INSTANCE_CAMERA_FUNCTION);
            mCameraFilePath = savedInstanceState.getString(INSTANCE_CAMERA_FILE_PATH);
            mQuality = savedInstanceState.getInt(INSTANCE_CAMERA_QUALITY);
            mLimitDuration = savedInstanceState.getLong(INSTANCE_CAMERA_DURATION);
            mLimitBytes = savedInstanceState.getLong(INSTANCE_CAMERA_BYTES);
        } else {
            Bundle bundle = getIntent().getExtras();
            assert bundle != null;
            mFunction = bundle.getInt(Album.KEY_INPUT_FUNCTION);
            mCameraFilePath = bundle.getString(Album.KEY_INPUT_FILE_PATH);
            mQuality = bundle.getInt(Album.KEY_INPUT_CAMERA_QUALITY);
            mLimitDuration = bundle.getLong(Album.KEY_INPUT_CAMERA_DURATION);
            mLimitBytes = bundle.getLong(Album.KEY_INPUT_CAMERA_BYTES);

            switch (mFunction) {
                case Album.FUNCTION_CAMERA_IMAGE: {
                    if (TextUtils.isEmpty(mCameraFilePath))
                        mCameraFilePath = AlbumUtils.randomJPGPath(this);
                    requestPermission(PERMISSION_TAKE_PICTURE, CODE_PERMISSION_IMAGE);
                    break;
                }
                case Album.FUNCTION_CAMERA_VIDEO: {
                    if (TextUtils.isEmpty(mCameraFilePath))
                        mCameraFilePath = AlbumUtils.randomMP4Path(this);
                    requestPermission(PERMISSION_TAKE_VIDEO, CODE_PERMISSION_VIDEO);
                    break;
                }
                default: {
                    throw new AssertionError("This should not be the case.");
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_CAMERA_FUNCTION, mFunction);
        outState.putString(INSTANCE_CAMERA_FILE_PATH, mCameraFilePath);
        outState.putInt(INSTANCE_CAMERA_QUALITY, mQuality);
        outState.putLong(INSTANCE_CAMERA_DURATION, mLimitDuration);
        outState.putLong(INSTANCE_CAMERA_BYTES, mLimitBytes);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPermissionGranted(int code) {
        switch (code) {
            case CODE_PERMISSION_IMAGE: {
                AlbumUtils.takeImage(this, CODE_ACTIVITY_TAKE_IMAGE, new File(mCameraFilePath));
                break;
            }
            case CODE_PERMISSION_VIDEO: {
                AlbumUtils.takeVideo(this, CODE_ACTIVITY_TAKE_VIDEO, new File(mCameraFilePath), mQuality, mLimitDuration, mLimitBytes);
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    protected void onPermissionDenied(int code) {
        int messageRes;
        switch (mFunction) {
            case Album.FUNCTION_CAMERA_IMAGE: {
                messageRes = R.string.album_permission_camera_image_failed_hint;
                break;
            }
            case Album.FUNCTION_CAMERA_VIDEO: {
                messageRes = R.string.album_permission_camera_video_failed_hint;
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.album_title_permission_failed)
                .setMessage(messageRes)
                .setPositiveButton(R.string.album_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callbackCancel();
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_ACTIVITY_TAKE_IMAGE:
            case CODE_ACTIVITY_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {
                    callbackResult();
                } else {
                    callbackCancel();
                }
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    private void callbackResult() {
        if (sResult != null) sResult.onAction(mCameraFilePath);
        sResult = null;
        sCancel = null;
        finish();
    }

    private void callbackCancel() {
        if (sCancel != null) sCancel.onAction("User canceled.");
        sResult = null;
        sCancel = null;
        finish();
    }
}