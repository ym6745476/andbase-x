package com.upu173.player;

import com.netease.neliveplayer.playerkit.sdk.LivePlayer;

public interface VideoPlayerOnStateChangeListener {

    void onCompletion();
    void onPlayStateChanged(LivePlayer.STATE state);

}
