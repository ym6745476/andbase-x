package com.andbase.library.app.model;

import android.widget.ImageView;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/5/17 13:27
 * Email 396196516@qq.com
 * Info 帧动画
 */
public class AbFrameAnimation {

    private boolean isRepeat;

    private AnimationListener animationListener;

    private ImageView imageView;

    private int[] drawableResIds;

    /**
     * 每帧动画的播放间隔数组
     */
    private int[] durations;

    /**
     * 每帧动画的播放间隔
     */
    private int duration;

    /**
     * 下一遍动画播放的延迟时间
     */
    private int delay;

    private int lastFrame;

    private boolean next;

    private boolean pause;

    private int currentSelect;

    private int currentFrame;

    private static final int SELECTED_A = 1;

    private static final int SELECTED_B = 2;

    private static final int SELECTED_C = 3;

    private static final int SELECTED_D = 4;

    private Runnable runnable;


    /**
     * @param iv       播放动画的控件
     * @param drawableResIds 播放的图片数组
     * @param duration 每帧动画的播放间隔(毫秒)
     * @param isRepeat 是否循环播放
     */
    public AbFrameAnimation(ImageView iv, int[] drawableResIds, int duration, boolean isRepeat) {
        this.imageView = iv;
        this.drawableResIds = drawableResIds;
        this.duration = duration;
        this.lastFrame = drawableResIds.length - 1;
        this.isRepeat = isRepeat;
        this.currentSelect = SELECTED_D;
    }

    /**
     * @param iv        播放动画的控件
     * @param drawableResIds 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param isRepeat  是否循环播放
     */
    public AbFrameAnimation(ImageView iv, int[] drawableResIds, int[] durations, boolean isRepeat) {
        this.imageView = iv;
        this.drawableResIds = drawableResIds;
        this.durations = durations;
        this.lastFrame = drawableResIds.length - 1;
        this.isRepeat = isRepeat;
        this.currentSelect = SELECTED_C;
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param drawableResIds 播放的图片数组
     * @param duration  每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public AbFrameAnimation(ImageView iv, int[] drawableResIds, int duration, int delay) {
        this.imageView = iv;
        this.drawableResIds = drawableResIds;
        this.duration = duration;
        this.delay = delay;
        this.lastFrame = drawableResIds.length - 1;
        this.currentSelect = SELECTED_B;
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param drawableResIds 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public AbFrameAnimation(ImageView iv, int[] drawableResIds, int[] durations, int delay) {
        this.imageView = iv;
        this.drawableResIds = drawableResIds;
        this.durations = durations;
        this.delay = delay;
        this.lastFrame = drawableResIds.length - 1;
        this.currentSelect = SELECTED_A;
    }

    private void playByDurationsAndDelay(final int i) {
        pause = false;
        runnable = new Runnable() {

            @Override
            public void run() {
                if (pause) {   // 暂停和播放需求
                    currentSelect = SELECTED_A;
                    currentFrame = i;
                    return;
                }
                if (0 == i) {
                    if (animationListener != null) {
                        animationListener.onAnimationStart();
                    }
                }
                imageView.setBackgroundResource(drawableResIds[i]);
                if (i == lastFrame) {
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat();
                    }
                    next = true;
                    playByDurationsAndDelay(0);
                } else {
                    playByDurationsAndDelay(i + 1);
                }
            }
        };
        imageView.postDelayed(runnable, next && delay > 0 ? delay : durations[i]);

    }

    private void playAndDelay(final int i) {
        pause = false;
        runnable = new Runnable() {

            @Override
            public void run() {
                if (pause) {
                    if (pause) {
                        currentSelect = SELECTED_B;
                        currentFrame = i;
                        return;
                    }
                    return;
                }
                next = false;
                if (0 == i) {
                    if (animationListener != null) {
                        animationListener.onAnimationStart();
                    }
                }
                imageView.setBackgroundResource(drawableResIds[i]);
                if (i == lastFrame) {
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat();
                    }
                    next = true;
                    playAndDelay(0);
                } else {
                    playAndDelay(i + 1);
                }
            }
        };
        imageView.postDelayed(runnable, next && delay > 0 ? delay : duration);

    }

    private void playByDurations(final int i) {
        pause = false;
        runnable = new Runnable() {

            @Override
            public void run() {
                if (pause) {
                    if (pause) {
                        currentSelect = SELECTED_C;
                        currentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (animationListener != null) {
                        animationListener.onAnimationStart();
                    }
                }
                imageView.setBackgroundResource(drawableResIds[i]);
                if (i == lastFrame) {
                    if (isRepeat) {
                        if (animationListener != null) {
                            animationListener.onAnimationRepeat();
                        }
                        playByDurations(0);
                    } else {
                        if (animationListener != null) {
                            animationListener.onAnimationEnd();
                        }
                    }
                } else {

                    playByDurations(i + 1);
                }
            }
        };
        imageView.postDelayed(runnable, durations[i]);

    }

    private void play(final int i) {
        pause = false;
        runnable = new Runnable() {

            @Override
            public void run() {
                if (pause) {
                    if (pause) {
                        currentSelect = SELECTED_D;
                        currentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (animationListener != null) {
                        animationListener.onAnimationStart();
                    }
                }
                imageView.setBackgroundResource(drawableResIds[i]);
                if (i == lastFrame) {

                    if (isRepeat) {
                        if (animationListener != null) {
                            animationListener.onAnimationRepeat();
                        }
                        play(0);
                    } else {
                        if (animationListener != null) {
                            animationListener.onAnimationEnd();
                        }
                    }

                } else {

                    play(i + 1);
                }
            }
        };

        imageView.postDelayed(runnable, duration);
    }

    public interface AnimationListener {

        void onAnimationStart();


        void onAnimationEnd();


        void onAnimationRepeat();
    }

    public void setAnimationListener(AnimationListener listener) {
        this.animationListener = listener;
    }

    public void release() {
        pauseAnimation();
    }

    public void pauseAnimation() {
        this.pause = true;
        if(runnable!= null){
            imageView.removeCallbacks(runnable);
        }
    }

    public boolean isPause() {
        return this.pause;
    }

    public void startAnimation() {
        pauseAnimation();

        switch (currentSelect) {
            case SELECTED_A:
                playByDurationsAndDelay(currentFrame);
                break;
            case SELECTED_B:
                playAndDelay(currentFrame);
                break;
            case SELECTED_C:
                playByDurations(currentFrame);
                break;
            case SELECTED_D:
                play(currentFrame);
                break;
            default:
                break;
        }
    }

}
