package com.andbase.library.app.global;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import androidx.annotation.RequiresPermission;

/**
 *   播放声音
 */
public final class AbAudioManager{

    private static final long VIBRATE_DURATION = 200L;
    private Activity activity;
    private MediaPlayer mediaPlayer;
    private float audioVolume = 0.10f;
    private boolean playAudio;

    public AbAudioManager(Activity activity) {
        this.activity = activity;
        playAudio = true;
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(completionListener);
    }

    public void initAudio(int raw) {
        mediaPlayer.reset();
        if (playAudio) {

            AssetFileDescriptor file = activity.getResources().openRawResourceFd(raw);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(audioVolume, audioVolume);
                mediaPlayer.prepare();
            } catch (Exception e) {
                mediaPlayer = null;
            }
        }
    }

    private final MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    public void playBeepSoundAndVibrate() {
        if (playAudio && mediaPlayer != null) {
            mediaPlayer.start();
        }
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATE_DURATION);
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    public void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void release(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public float getAudioVolume() {
        return audioVolume;
    }

    public void setAudioVolume(float audioVolume) {
        this.audioVolume = audioVolume;
    }
}