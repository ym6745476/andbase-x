package com.andbase.library.view.listener;

import android.view.View;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 15:26
 * Email 396196516@qq.com
 * Info 焦点事件监听器
 */
public interface AbOnFocusChangeListener {

    /**
     * 焦点
     * @param view
     * @param hasFocus
     */
    public void onFocusChange(View view, boolean hasFocus);
}
