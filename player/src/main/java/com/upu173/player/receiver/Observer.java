package com.upu173.player.receiver;

import java.io.Serializable;

public interface Observer<T> extends Serializable {

    /**
     * 通知产生后的回调函数
     *
     * @param t 事件参数
     */
    public void onEvent(T t);
}