package com.andbase.library.view.listener;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 15:29
 * Email 396196516@qq.com
 * Info 进度事件监听器
 */
public interface AbOnProgressListener {

    /**
     * 进度.
     *
     * @param progress the progress
     */
    public void onProgress(int progress);

    /**
     * 完成.
     */
    public void onComplete();
}
