package com.netease.neliveplayer.playerkit.sdk.extension;


import com.netease.neliveplayer.playerkit.sdk.LivePlayerObserver;
import com.netease.neliveplayer.playerkit.sdk.model.MediaInfo;

/**
 * 播放器状态回调观察者
 * <p>
 * @author netease
 */

public abstract class SimpleLivePlayerObserver implements LivePlayerObserver {
    @Override
    public void onPreparing() {

    }

    @Override
    public void onPrepared(MediaInfo mediaInfo) {

    }

    @Override
    public void onError(int code, int extra) {

    }

    @Override
    public void onFirstVideoRendered() {

    }

    @Override
    public void onFirstAudioRendered() {

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingEnd() {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onVideoDecoderOpen(int value){

    }
}
