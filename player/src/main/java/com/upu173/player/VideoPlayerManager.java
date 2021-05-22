package com.upu173.player;

import android.content.Context;
import android.util.Log;

import com.netease.neliveplayer.playerkit.sdk.LivePlayer;
import com.netease.neliveplayer.playerkit.sdk.PlayerManager;
import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.model.NEDynamicLoadingConfig;
import com.netease.neliveplayer.sdk.model.NESDKConfig;

import java.util.Map;

/**
 * 视频播放器管理器.
 */
public class VideoPlayerManager {

    private VideoPlayer videoPlayer;

    private static VideoPlayerManager videoPlayerManager;

    private VideoPlayerManager() {
    }

    public static synchronized VideoPlayerManager instance() {
        if (videoPlayerManager == null) {
            videoPlayerManager = new VideoPlayerManager();
        }
        return videoPlayerManager;
    }

    public void init(final Context context){
        NESDKConfig config = new NESDKConfig();
        //如果需要应用层上报播放数据，那么设置dataUploadListener，默认SDK内部上报播放数据
        config.dataUploadListener = new NELivePlayer.OnDataUploadListener() {
            @Override
            public boolean onDataUpload(String url, String data) {
                //Log.d("VideoPlayerManager", "onDataUpload url:" + url + ", data:" + data);
                return true;
            }

            @Override
            public boolean onDocumentUpload(String url, Map<String, String> params, Map<String, String> filepaths) {
                //Log.d("VideoPlayerManager", "onDataUpload url:" + url + ", params:" + params+",filepaths:"+filepaths);
                return  true;
            }
        };

        //如果需要开启动态加载功能，那么设置dynamicLoadingConfig，默认关闭
        config.dynamicLoadingConfig = new NEDynamicLoadingConfig();
        config.dynamicLoadingConfig.isDynamicLoading = false;
        //使用Armeabiv7a架构示例，使用其他架构可以参考修改
        config.dynamicLoadingConfig.isArmeabiv7a = true;
        config.dynamicLoadingConfig.armeabiv7aUrl = "客户服务器地址" + "LivePlayer_Android_SDK_" + PlayerManager.getSDKInfo(context).version + "_" + "armeabi-v7a" + ".zip";
        //动态加载准备完成时回调
        config.dynamicLoadingConfig.onDynamicLoadingListener = new NELivePlayer.OnDynamicLoadingListener() {
            @Override
            public void onDynamicLoading(final NEDynamicLoadingConfig.ArchitectureType type, final boolean isCompleted) {

                Log.d("VideoPlayerManager", "type:" + type + "，isCompleted:" + isCompleted);
            }
        };
        NELivePlayer.init(context,config);
    }

    public VideoPlayer getCurrentVideoPlayer() {
        return videoPlayer;
    }

    public void setCurrentVideoPlayer(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    public void stopVideoPlayer() {
        if (videoPlayer != null && (videoPlayer.getPlayerState() == LivePlayer.STATE.PLAYING)) {
            videoPlayer.pause();
        }
    }

    public void resumeVideoPlayer() {
        if (videoPlayer != null && (videoPlayer.getPlayerState() == LivePlayer.STATE.PAUSED)) {
            videoPlayer.resumeStart();
        }
    }

    public void releaseVideoPlayer() {
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    public boolean onBackPressd() {
        if (videoPlayer != null) {
            if (videoPlayer.currentMode == VideoPlayer.MODE_FULL_SCREEN) {
                return videoPlayer.exitFullScreen();
            } else if (videoPlayer.currentMode == VideoPlayer.MODE_TINY_WINDOW) {
                return videoPlayer.exitTinyWindow();
            }
        }
        return false;
    }
}
