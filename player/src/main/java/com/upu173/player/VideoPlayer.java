package com.upu173.player;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Map;

import com.netease.neliveplayer.playerkit.core.player.LivePlayerImpl;
import com.netease.neliveplayer.playerkit.sdk.LivePlayer;
import com.netease.neliveplayer.playerkit.sdk.PlayerManager;
import com.netease.neliveplayer.playerkit.sdk.VodPlayer;
import com.netease.neliveplayer.playerkit.sdk.VodPlayerObserver;
import com.netease.neliveplayer.playerkit.sdk.constant.CauseCode;
import com.netease.neliveplayer.playerkit.sdk.model.MediaInfo;
import com.netease.neliveplayer.playerkit.sdk.model.StateInfo;
import com.netease.neliveplayer.playerkit.sdk.model.VideoBufferStrategy;
import com.netease.neliveplayer.playerkit.sdk.model.VideoOptions;
import com.netease.neliveplayer.playerkit.sdk.model.VideoScaleMode;
import com.upu173.player.receiver.Observer;
import com.upu173.player.receiver.PhoneCallStateObserver;

/**
 * 播放器
 */
public class VideoPlayer extends FrameLayout{

    public static final  String TAG = "VideoPlayer";
    private Context context;
    private FrameLayout container;
    protected VodPlayer player;
    protected String url;
    protected VideoPlayerController videoPlayerController;
    private VideoTextureView textureView;
    /** 获得的视频信息*/
    public MediaInfo mediaInfo;
    /** 当前缓冲的百分比*/
    public float currentCachedPercent;
    /** 是后台暂停状态*/
    protected boolean isPauseInBackgroud = true;
    /** 硬解码开关*/
    private boolean isHardware = false;
    /** 当前播放器状态*/
    private LivePlayer.STATE state = LivePlayer.STATE.IDLE;

    /** 当前播放器模式*/
    public int currentMode = 1;

    /**
     * 普通模式
     **/
    public static final int MODE_NORMAL = 1;
    /**
     * 全屏模式
     **/
    public static final int MODE_FULL_SCREEN = 2;
    /**
     * 小窗口模式
     **/
    public static final int MODE_TINY_WINDOW = 3;

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init() {
        this.container = new FrameLayout(context);
        //this.container.setBackgroundColor(Color.rgb(90,90,90));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(container, params);
    }


    public void setUp(String url, Map<String, String> headers) {
        this.url = url;

    }

    public void setController(VideoPlayerController controller) {
        this.container.removeView(videoPlayerController);
        this.videoPlayerController = controller;
        this.videoPlayerController.reset();
        this.videoPlayerController.setVideoPlayer(this);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.container.addView(controller, params);
    }

    private void addTextureView() {
        this.textureView = new VideoTextureView(context);
        this.container.removeView(textureView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        container.addView(textureView, 0, params);
    }

    public Bitmap getSnapshot(){
        return player.getSnapshot();
    }


    private void initPlayer() {
        VideoOptions options = new VideoOptions();
        options.hardwareDecode = isHardware;
        /**
         * isPlayLongTimeBackground 控制退到后台或者锁屏时是否继续播放，开发者可根据实际情况灵活开发,我们的示例逻辑如下：
         * 使用软件解码：
         * isPlayLongTimeBackground 为 false 时，直播进入后台停止播放，进入前台重新拉流播放
         * isPlayLongTimeBackground 为 true 时，直播进入后台不做处理，继续播放,
         *
         * 使用硬件解码：
         * 直播进入后台停止播放，进入前台重新拉流播放
         */
        options.isPlayLongTimeBackground = !isPauseInBackgroud;
        options.bufferStrategy = VideoBufferStrategy.ANTI_JITTER;
        player = PlayerManager.buildVodPlayer(context, url, options);
        player.setupRenderView(textureView, VideoScaleMode.FIT);

    }

    public void start() {
        VideoPlayerManager.instance().setCurrentVideoPlayer(this);
        addTextureView();
        initPlayer();

        player.registerPlayerObserver(playerObserver, true);
        player.start();
        LivePlayerImpl livePlayer = (LivePlayerImpl)player;
        livePlayer.setVolume(0.5f);

    }

    /**
     * 恢复播放
     */
    public void resumeStart() {
        player.start();
    }

    public void pause() {
        player.pause();
    }

    public void seekTo(long position) {
        player.seekTo(position);
    }

    public void release() {
        if (player == null) {
            return;
        }
        Log.i(TAG, "releasePlayer");
        player.registerPlayerObserver(playerObserver, false);
        PhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, false);
        player.setupRenderView(null, VideoScaleMode.NONE);
        textureView.releaseSurface();
        textureView = null;
        player.stop();
        player = null;
        state = LivePlayer.STATE.IDLE;
        videoPlayerController.onIdle();
    }


    private VodPlayerObserver playerObserver = new VodPlayerObserver() {

        @Override
        public void onCurrentPlayProgress(long currentPosition, long duration, float percent, long cachedPosition) {
            videoPlayerController.onCurrentPlayProgress(currentPosition,duration,percent,cachedPosition);
        }

        @Override
        public void onSeekCompleted() {
            //用户通过seekbar拖动完成，之后应该是缓冲状态
            Log.i(TAG, "onSeekCompleted");
        }

        @Override
        public void onCompletion() {
            Log.i(TAG, "onCompletion");
            videoPlayerController.onCompletion();
        }

        @Override
        public void onAudioVideoUnsync() {
            //showToast("音视频不同步");
        }

        @Override
        public void onNetStateBad() {
            Log.i(TAG, "onNetStateBad");
        }

        @Override
        public void onDecryption(int ret) {
        }

        @Override
        public void onPreparing() {
            videoPlayerController.onPreparing();
        }

        @Override
        public void onPrepared(MediaInfo info) {
            mediaInfo = info;
            videoPlayerController.onPrepared();
        }

        @Override
        public void onError(int code, int extra) {
            if (code == CauseCode.CODE_VIDEO_PARSER_ERROR) {
                videoPlayerController.onError("视频解析出错");
            } else {
                videoPlayerController.onError("播放错误"+code);
            }
        }

        @Override
        public void onFirstVideoRendered() {
            //showToast("视频第一帧已解析");
        }

        @Override
        public void onFirstAudioRendered() {
            //showToast("音频第一帧已解析");
        }

        @Override
        public void onBufferingStart() {
            //mBuffer.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBufferingEnd() {
            videoPlayerController.onBufferingEnd();
        }

        @Override
        public void onBuffering(int percent) {
            currentCachedPercent = percent;
            Log.d(TAG, "缓冲中..." + percent + "%");
            videoPlayerController.onBuffering(percent);
        }

        @Override
        public void onVideoDecoderOpen(int value) {
            //showToast("使用解码类型：" + (value == 1 ? "硬件解码" : "软解解码"));
        }

        @Override
        public void onStateChanged(StateInfo stateInfo) {
            state = stateInfo.getState();
            videoPlayerController.onPlayStateChanged(state);
            Log.i(TAG,"当前状态：" + state);
        }


        @Override
        public void onHttpResponseInfo(int code, String header) {
            Log.i(TAG, "onHttpResponseInfo,code:" + code + " header:" + header);
        }
    };

    /**
     * 获取当前状态
     * @return
     */
    public LivePlayer.STATE getPlayerState(){
        return this.state;
    }


    /**
     * 获取当前位置
     * @return
     */
    public long getCurrentPosition(){
        return player.getCurrentPosition();
    }

    /**
     * 获取当前位置
     * @return
     */
    public float getCurrentPositionPercent(){
        return player.getCurrentPositionPercent();
    }

    /**
     * 获取当前位置
     * @return
     */
    public float getCurrentCachedPercent(){
        return currentCachedPercent;
    }


    /**
     * 获取总时长
     * @return
     */
    public long getDuration(){
        return player.getDuration();
    }

    //处理与电话逻辑
    private Observer<Integer> localPhoneObserver = new Observer<Integer>() {

        @Override
        public void onEvent(Integer phoneState) {
            if (phoneState == TelephonyManager.CALL_STATE_IDLE) {
                player.start();
            } else if (phoneState == TelephonyManager.CALL_STATE_RINGING) {
                player.stop();
            } else {
                Log.i(TAG, "localPhoneObserver onEvent " + phoneState);
            }

        }
    };


    /**
     * 全屏，将mContainer(内部包含mTextureView和mController)从当前容器中移除，并添加到android.R.content中.
     * 切换横屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期
     */
    public void enterFullScreen() {
        if (currentMode == MODE_FULL_SCREEN) return;

        // 隐藏ActionBar、状态栏，并横屏
        PlayerUtil.hideActionBar(context);
        PlayerUtil.scanForActivity(context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ViewGroup contentView = (ViewGroup) PlayerUtil.scanForActivity(context).findViewById(android.R.id.content);
        if (currentMode == MODE_TINY_WINDOW) {
            contentView.removeView(container);
        } else {
            this.removeView(container);
        }
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(container, params);
        currentMode = MODE_FULL_SCREEN;
        videoPlayerController.onPlayModeChanged(currentMode);
        Log.d(TAG,"MODE_FULL_SCREEN");
    }

    /**
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     * 切换竖屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期.
     *
     * @return true退出全屏.
     */
    public boolean exitFullScreen() {
        if (currentMode == MODE_FULL_SCREEN) {
            PlayerUtil.showActionBar(context);
            PlayerUtil.scanForActivity(context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ViewGroup contentView = (ViewGroup) PlayerUtil.scanForActivity(context).findViewById(android.R.id.content);
            contentView.removeView(container);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(container, params);

            currentMode = MODE_NORMAL;
            videoPlayerController.onPlayModeChanged(currentMode);
            Log.d(TAG,"MODE_NORMAL");
            return true;
        }
        return false;
    }

    /**
     * 进入小窗口播放，小窗口播放的实现原理与全屏播放类似。
     */
    public void enterTinyWindow() {
        if (currentMode == MODE_TINY_WINDOW) return;
        this.removeView(container);

        ViewGroup contentView = (ViewGroup) PlayerUtil.scanForActivity(context)
                .findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) (PlayerUtil.getScreenWidth(context) * 0.6f),
                (int) (PlayerUtil.getScreenWidth(context) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = PlayerUtil.dp2px(context, 8f);
        params.bottomMargin = PlayerUtil.dp2px(context, 8f);

        contentView.addView(container, params);

        currentMode = MODE_TINY_WINDOW;
        videoPlayerController.onPlayModeChanged(currentMode);
        Log.d(TAG,"MODE_TINY_WINDOW");
    }

    /**
     * 退出小窗口播放
     */
    public boolean exitTinyWindow() {
        if (currentMode == MODE_TINY_WINDOW) {
            ViewGroup contentView = (ViewGroup) PlayerUtil.scanForActivity(context)
                    .findViewById(android.R.id.content);
            contentView.removeView(container);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(container, params);

            currentMode = MODE_NORMAL;
            videoPlayerController.onPlayModeChanged(currentMode);
            Log.d(TAG,"MODE_NORMAL");
            return true;
        }
        return false;
    }

}
