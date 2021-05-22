package com.upu173.player;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 控制器抽象类
 */
public abstract class BaseVideoPlayerController
        extends FrameLayout implements View.OnTouchListener {

    private Context context;
    protected VideoPlayer videoPlayer;


    public BaseVideoPlayerController(Context context) {
        super(context);
        this.context = context;
        this.setOnTouchListener(this);
    }

    public void setVideoPlayer(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    /**
     * 设置播放的视频的标题
     *
     * @param title 视频标题
     */
    public abstract void setTitle(String title);


    /**
     * 视频底图ImageView控件
     * @return 底图ImageView
     */
    public abstract ImageView imageView();

    /**
     * 设置总时长.
     */
    public abstract void setLength(long length);

    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    protected abstract void reset();


}
