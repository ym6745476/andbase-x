package com.upu173.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.neliveplayer.playerkit.R;
import com.netease.neliveplayer.playerkit.common.log.LogUtil;
import com.netease.neliveplayer.playerkit.core.player.LivePlayerImpl;
import com.netease.neliveplayer.playerkit.sdk.LivePlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 播放器控制器.
 */
public class VideoPlayerController extends BaseVideoPlayerController
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener{

    public static final  String TAG = "VideoPlayer";
    private Context context;
    private ImageView thumbImageView;
    private ImageView centerPlayBtn;

    private LinearLayout topLayout;
    private ImageView backBtn;
    private TextView titleText;
    private LinearLayout batteryLayout;
    private ImageView batteryImage;
    private TextView batteryTime;

    private LinearLayout errorLayout;
    private TextView errorText;
    private TextView retryBtn;

    private LinearLayout completedLayout;
    private TextView replayBtn;

    private LinearLayout bottomLayout;
    private ImageView bottomPlayBtn;
    private ImageView fullScreenBtn,cameraBtn;

    private LinearLayout loadingLayout;
    private TextView loadingText;
    private TextView lengthText;

    private boolean topBottomVisible;
    private CountDownTimer dismissTopBottomCountDownTimer;
    /**是否已经注册了电池广播*/
    private boolean hasRegisterBatteryReceiver;
    
    private TextView currentPositionText;
    private TextView durationText;
    private SeekBar seekProgressBar;

    private LinearLayout changeBrightnessLayout;
    private ProgressBar changeBrightnessProgress;

    private LinearLayout changeVolumeLayout;
    private ProgressBar changeVolumeProgress;

    private VideoPlayerOnStateChangeListener videoPlayerOnStateChangeListener;


    public VideoPlayerController(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.video_palyer_controller, this, true);

        centerPlayBtn = (ImageView) findViewById(R.id.center_play_btn);
        thumbImageView = (ImageView) findViewById(R.id.video_thumb);
        topLayout = (LinearLayout) findViewById(R.id.top_layout);
        backBtn = (ImageView) findViewById(R.id.back_btn);
        titleText = (TextView) findViewById(R.id.title_text);


        batteryLayout = (LinearLayout) findViewById(R.id.battery_layout);
        batteryImage = (ImageView) findViewById(R.id.battery_image);
        batteryTime = (TextView) findViewById(R.id.battery_time);


        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        errorText = (TextView) findViewById(R.id.error_text);
        retryBtn = (TextView) findViewById(R.id.retry_btn);
        completedLayout = (LinearLayout) findViewById(R.id.completed_layout);
        replayBtn = (TextView) findViewById(R.id.replay_btn);

        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        bottomPlayBtn = (ImageView) findViewById(R.id.bottom_play_btn);
        fullScreenBtn = (ImageView) findViewById(R.id.full_screen_btn);
        cameraBtn = (ImageView) findViewById(R.id.camera_btn);

        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        loadingText = (TextView) findViewById(R.id.loading_text);
        lengthText = (TextView) findViewById(R.id.length_text);
        
        currentPositionText = (TextView) findViewById(R.id.current_position_text);
        durationText = (TextView) findViewById(R.id.duration_text);
        seekProgressBar = (SeekBar) findViewById(R.id.seek_bar);

        changeBrightnessLayout = (LinearLayout) findViewById(R.id.change_brightness);
        changeBrightnessProgress = (ProgressBar) findViewById(R.id.change_brightness_progress);

        changeVolumeLayout = (LinearLayout) findViewById(R.id.change_volume);
        changeVolumeProgress = (ProgressBar) findViewById(R.id.change_volume_progress);


        backBtn.setOnClickListener(this);
        centerPlayBtn.setOnClickListener(this);
        bottomPlayBtn.setOnClickListener(this);
        fullScreenBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
        retryBtn.setOnClickListener(this);
        replayBtn.setOnClickListener(this);
        seekProgressBar.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
    }


    public void setVideoPlayerOnStateChangeListener(VideoPlayerOnStateChangeListener videoPlayerOnStateChangeListener) {
        this.videoPlayerOnStateChangeListener = videoPlayerOnStateChangeListener;
    }

    @Override
    public void setTitle(String title) {
        titleText.setText(title);
    }

    @Override
    public ImageView imageView() {
        return thumbImageView;
    }

    @Override
    public void setLength(long length) {
        if(length > 0){
            lengthText.setText(PlayerUtil.formatTime(length));
        }else{
            lengthText.setText("");
        }
    }



    public void onIdle(){
        thumbImageView.setVisibility(View.VISIBLE);
        centerPlayBtn.setVisibility(View.VISIBLE);
        lengthText.setVisibility(View.VISIBLE);

        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        completedLayout.setVisibility(View.GONE);
        topLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);

    }

    public void onError(String message){
        errorLayout.setVisibility(View.VISIBLE);
        errorText.setText(message);
        loadingLayout.setVisibility(View.GONE);
    }

    public void onPreparing(){
        loadingLayout.setVisibility(View.VISIBLE);
        loadingText.setText("准备中...");
        thumbImageView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        completedLayout.setVisibility(View.GONE);
        topLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        centerPlayBtn.setVisibility(View.GONE);
        lengthText.setVisibility(View.GONE);
    }

    public void onPrepared(){
        thumbImageView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        completedLayout.setVisibility(View.GONE);
        topLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        centerPlayBtn.setVisibility(View.GONE);
        lengthText.setVisibility(View.GONE);
    }

    /**
     * 播放进度更新
     * @param percent
     */
    protected void onCurrentPlayProgress(long currentPosition, long duration, float percent, long cachedPosition) {
        seekProgressBar.setProgress((int)percent);
        currentPositionText.setText(PlayerUtil.formatTime(currentPosition));
        durationText.setText(PlayerUtil.formatTime(duration));
        batteryTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date()));
    }


    /**
     * 缓冲更新 只到99%
     * @param percent
     */
    protected void onBuffering(int percent) {
        LogUtil.i(TAG,videoPlayer.getCurrentPositionPercent() + "/" + videoPlayer.getCurrentCachedPercent());
        seekProgressBar.setSecondaryProgress(percent);
        //如果缓冲的进度小于当前播放的 就显示
        if(videoPlayer.getCurrentPositionPercent() >= videoPlayer.getCurrentCachedPercent()){
            if(loadingLayout.getVisibility() != View.VISIBLE){
                loadingLayout.setVisibility(View.VISIBLE);
            }
            loadingText.setText(percent +"%");
        }else{
            if(loadingLayout.getVisibility() != View.GONE){
                loadingLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 缓冲完成 100%
     */
    protected void onBufferingEnd() {
        seekProgressBar.setSecondaryProgress(100);
        loadingLayout.setVisibility(View.GONE);
    }


    /**
     * 播放状态改变
     * @param state
     */
    protected void onPlayStateChanged(LivePlayer.STATE state) {
        if(state  == LivePlayer.STATE.IDLE){
            onIdle();
        }else if(state  == LivePlayer.STATE.PREPARING){
            onPreparing();
        }else if(state  == LivePlayer.STATE.PREPARED){
            onPrepared();
        }else if(state  == LivePlayer.STATE.PLAYING){
            loadingLayout.setVisibility(View.GONE);
            bottomPlayBtn.setImageResource(R.drawable.ic_player_pause);
            startDismissTopBottomTimer();
        }else if(state  == LivePlayer.STATE.PAUSED){
            loadingLayout.setVisibility(View.GONE);
            bottomPlayBtn.setImageResource(R.drawable.ic_player_start);
            cancelDismissTopBottomTimer();
        }else if(state  == LivePlayer.STATE.ERROR){
            setTopBottomVisible(false);
            topLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.VISIBLE);
        }else if(state  == LivePlayer.STATE.STOPPED){
            setTopBottomVisible(false);
            thumbImageView.setVisibility(View.VISIBLE);
            completedLayout.setVisibility(View.VISIBLE);
        }
        if(this.videoPlayerOnStateChangeListener != null){
            this.videoPlayerOnStateChangeListener.onPlayStateChanged(state);
        }
    }

    public void onCompletion(){
        if(this.videoPlayerOnStateChangeListener != null){
            this.videoPlayerOnStateChangeListener.onCompletion();
        }
    }

    /**
     * 播放模式改变
     * @param playMode
     */
    protected void onPlayModeChanged(int playMode) {
        switch (playMode) {
            case VideoPlayer.MODE_NORMAL:
                backBtn.setVisibility(View.GONE);
                fullScreenBtn.setImageResource(R.drawable.ic_player_enlarge);
                fullScreenBtn.setVisibility(View.VISIBLE);
                cameraBtn.setVisibility(View.GONE);
                batteryLayout.setVisibility(View.GONE);
                if (hasRegisterBatteryReceiver) {
                    context.unregisterReceiver(batterReceiver);
                    hasRegisterBatteryReceiver = false;
                }
                break;
            case VideoPlayer.MODE_FULL_SCREEN:
                backBtn.setVisibility(View.VISIBLE);
                fullScreenBtn.setVisibility(View.VISIBLE);
                fullScreenBtn.setImageResource(R.drawable.ic_player_shrink);
                batteryLayout.setVisibility(View.VISIBLE);
                cameraBtn.setVisibility(View.VISIBLE);
                if (!hasRegisterBatteryReceiver) {
                    context.registerReceiver(batterReceiver,
                            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    hasRegisterBatteryReceiver = true;
                }
                break;
            case VideoPlayer.MODE_TINY_WINDOW:
                backBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 电池状态即电量变化广播接收器
     */
    private BroadcastReceiver batterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                // 充电中
                batteryImage.setImageResource(R.drawable.battery_charging);
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                // 充电完成
                batteryImage.setImageResource(R.drawable.battery_full);
            } else {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int percentage = (int) (((float) level / scale) * 100);
                if (percentage <= 10) {
                    batteryImage.setImageResource(R.drawable.battery_10);
                } else if (percentage <= 20) {
                    batteryImage.setImageResource(R.drawable.battery_20);
                } else if (percentage <= 50) {
                    batteryImage.setImageResource(R.drawable.battery_50);
                } else if (percentage <= 80) {
                    batteryImage.setImageResource(R.drawable.battery_80);
                } else if (percentage <= 100) {
                    batteryImage.setImageResource(R.drawable.battery_100);
                }
            }
        }
    };

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelDismissTopBottomTimer();
        seekProgressBar.setProgress(0);
        seekProgressBar.setSecondaryProgress(0);
        centerPlayBtn.setVisibility(View.VISIBLE);
        thumbImageView.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.GONE);
        fullScreenBtn.setImageResource(R.drawable.ic_player_enlarge);
        lengthText.setVisibility(View.VISIBLE);
        topLayout.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        completedLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == centerPlayBtn) {
            if (videoPlayer.getPlayerState() == LivePlayer.STATE.IDLE) {
                videoPlayer.start();
            }
        } else if (v == backBtn) {
            if (videoPlayer.currentMode == videoPlayer.MODE_FULL_SCREEN) {
                videoPlayer.exitFullScreen();
            } else if (videoPlayer.currentMode == videoPlayer.MODE_TINY_WINDOW) {
                videoPlayer.exitTinyWindow();
            }
        } else if (v == bottomPlayBtn) {
            if (videoPlayer.getPlayerState()==LivePlayer.STATE.PLAYING) {
                videoPlayer.pause();
            } else if (videoPlayer.getPlayerState()==LivePlayer.STATE.PAUSED) {
                videoPlayer.resumeStart();
            }
        } else if (v == fullScreenBtn) {
            if (videoPlayer.currentMode == VideoPlayer.MODE_NORMAL || videoPlayer.currentMode == VideoPlayer.MODE_TINY_WINDOW) {
                videoPlayer.enterFullScreen();
            } else if (videoPlayer.currentMode == VideoPlayer.MODE_FULL_SCREEN) {
                videoPlayer.exitFullScreen();
            }
        } else if (v == cameraBtn) {
            getSnapshot();
        }  else if (v == retryBtn) {
            videoPlayer.start();
        } else if (v == replayBtn) {
            retryBtn.performClick();
        } else if (v == this) {
            if (videoPlayer.getPlayerState() == LivePlayer.STATE.PLAYING || videoPlayer.getPlayerState() == LivePlayer.STATE.PAUSED) {
                setTopBottomVisible(!topBottomVisible);
            }
        }
    }

    public void getSnapshot() {
        if (videoPlayer.mediaInfo == null) {
            LogUtil.i(TAG, "mediaInfo is null,截图不成功");
            Toast.makeText(context,"截图不成功",Toast.LENGTH_LONG).show();
        } else if (videoPlayer.mediaInfo.getVideoDecoderMode().equals("MediaCodec")) {
            LogUtil.i(TAG, "hardware decoder unsupport snapshot ,截图不成功");
            Toast.makeText(context,"截图不支持硬件解码",Toast.LENGTH_LONG).show();
        } else {
            Bitmap bitmap = videoPlayer.getSnapshot();

            PackageInfo info = getPackageInfo(context);
            String downloadRootPath = File.separator + "download" + File.separator + info.packageName;
            File root = Environment.getExternalStorageDirectory();
            File downloadDir = new File(root.getAbsolutePath() + downloadRootPath);
            if(!downloadDir.exists()){
                downloadDir.mkdirs();
            }
            String downloadRootDir = downloadDir.getPath();

            //默认下载图片文件目录.
            String imageFilePath = downloadRootDir + File.separator + "images" + File.separator + "video_" + System.currentTimeMillis() + ".png";
            File imageFile = new File(imageFilePath);
            try {
                imageFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),imageFilePath, imageFile.getName(), null);
                //通知图库更新
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageFilePath)));
                LogUtil.i(TAG,"添加到图库：" + imageFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context,"截图成功",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 设置top、bottom的显示和隐藏
     * @param visible true显示，false隐藏.
     */
    private void setTopBottomVisible(boolean visible) {
        topLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        bottomLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        topBottomVisible = visible;
        if (visible) {
            if (!(videoPlayer.getPlayerState()==LivePlayer.STATE.PAUSED)) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    /**
     * 开启top、bottom自动消失的timer
     */
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (dismissTopBottomCountDownTimer == null) {
            dismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        dismissTopBottomCountDownTimer.start();
    }

    /**
     * 取消top、bottom自动消失的timer
     */
    private void cancelDismissTopBottomTimer() {
        if (dismissTopBottomCountDownTimer != null) {
            dismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (videoPlayer.getPlayerState() == LivePlayer.STATE.PAUSED) {
            videoPlayer.start();
        }
        long position = (long) (videoPlayer.getDuration() * seekBar.getProgress() / 100f);
        videoPlayer.seekTo(position);
        startDismissTopBottomTimer();
    }

    private float downX;
    private float downY;
    private boolean needChangeVolume;
    private boolean needChangeBrightness;
    private static final int THRESHOLD = 80;
    private float downBrightness;
    private float downVolume;


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 只有全屏的时候才能亮度、声音
        if (!(videoPlayer.currentMode == VideoPlayer.MODE_FULL_SCREEN)) {
            return false;
        }
        // 只有在播放、暂停的时候 调整亮度和声音
        if (videoPlayer.getPlayerState() == LivePlayer.STATE.PLAYING
                || videoPlayer.getPlayerState() == LivePlayer.STATE.PAUSED) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = x;
                    downY = y;
                    needChangeVolume = false;
                    needChangeBrightness = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - downX;
                    float deltaY = y - downY;
                    float absDeltaY = Math.abs(deltaY);
                    if (!needChangeVolume && !needChangeBrightness) {
                        if (absDeltaY >= THRESHOLD) {
                            if (downX < getWidth() * 0.5f) {
                                // 左侧改变亮度
                                needChangeBrightness = true;
                                downBrightness = PlayerUtil.scanForActivity(context)
                                        .getWindow().getAttributes().screenBrightness;
                            } else {
                                // 右侧改变声音
                                needChangeVolume = true;
                                LivePlayerImpl livePlayer = (LivePlayerImpl)videoPlayer.player;
                                downVolume = livePlayer.getVolume();
                            }
                        }
                    }

                    if (needChangeBrightness) {
                        deltaY = -deltaY;
                        float deltaBrightness = deltaY * 3 / getHeight();
                        float newBrightness = downBrightness + deltaBrightness;
                        newBrightness = Math.max(0, Math.min(newBrightness, 1));
                        float newBrightnessPercentage = newBrightness;
                        WindowManager.LayoutParams params = PlayerUtil.scanForActivity(context).getWindow().getAttributes();
                        params.screenBrightness = newBrightnessPercentage;
                        PlayerUtil.scanForActivity(context).getWindow().setAttributes(params);
                        int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
                        showChangeBrightness(newBrightnessProgress);
                    }
                    if (needChangeVolume) {
                        LivePlayerImpl livePlayer = (LivePlayerImpl)videoPlayer.player;
                        deltaY = -deltaY;
                        float deltaVolume = deltaY * 3 / getHeight();
                        float newVolume = downVolume + deltaVolume;
                        newVolume = Math.max(0, Math.min(newVolume,1));
                        livePlayer.setVolume(newVolume);
                        int newVolumeProgress = (int) (100f * newVolume);
                        showChangeVolume(newVolumeProgress);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (needChangeBrightness) {
                        hideChangeBrightness();
                        return true;
                    }
                    if (needChangeVolume) {
                        hideChangeVolume();
                        return true;
                    }
                    break;
            }
        }else{
            hideChangeBrightness();
            hideChangeVolume();
            return false;
        }

        return false;
    }


    protected void showChangeVolume(int newVolumeProgress) {
        changeVolumeLayout.setVisibility(View.VISIBLE);
        changeVolumeProgress.setProgress(newVolumeProgress);
    }

    protected void hideChangeVolume() {
        changeVolumeLayout.setVisibility(View.GONE);
    }

    protected void showChangeBrightness(int newBrightnessProgress) {
        changeBrightnessLayout.setVisibility(View.VISIBLE);
        changeBrightnessProgress.setProgress(newBrightnessProgress);
    }

    protected void hideChangeBrightness() {
        changeBrightnessLayout.setVisibility(View.GONE);
    }

    /**
     * 获取包信息.
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
