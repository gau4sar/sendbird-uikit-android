package com.sendbird.uikit.widgets;

import android.net.Uri;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.ArrayList;
import java.util.List;

public class AudioManager implements AudioPlayer.AudioPlayerListener, LifecycleObserver {

    public interface AudioChangeListener {
        void onAudioChanged(Uri uriPlaying, boolean isPlaying);
        void onStateEnded(Uri uriPlaying);
        void onIsPlayingChanged(Uri uriPlaying, boolean isPlaying);
    }

    private static AudioManager sInstance = null;
    private Uri uriPlaying;

    private final List<AudioChangeListener> listeners = new ArrayList<>();
    private final AudioPlayer player = AudioPlayer.getInstance();
    
    private AudioManager() {
        player.setListener(this);
    }

    public static AudioManager getInstance() {
        if (sInstance == null) {
            sInstance = new AudioManager();
        }
        return sInstance;
    }

    public void attachLifecycle(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    public void detachLifecycle(Lifecycle lifecycle) {
        stop();
        lifecycle.removeObserver(this);
    }

    public void registerAudioChangeListener(AudioChangeListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterAudioChangeListener(AudioChangeListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyAudioChanged(boolean isPlaying) {
        for (AudioChangeListener listener: listeners) {
            listener.onAudioChanged(uriPlaying, isPlaying);
        }
    }

    public void togglePlay(Uri uri) {
        if (uriPlaying != uri) {
            uriPlaying = uri;
            player.stop();
            player.start(uriPlaying);
            notifyAudioChanged(true);
        } else {
            player.toggle();
            notifyAudioChanged(player.isPlaying());
        }
    }

    private void stop() {
        uriPlaying = null;
        player.stop();
        notifyAudioChanged(false);
    }

    public boolean isUriPlaying(Uri uri) {
        return uriPlaying != null && uri != null &&  uriPlaying.toString().equals(uri.toString());
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public int getProgress() {
        return (int) player.getCurrentProgress();
    }

    @Override
    public void onStateEnded() {
        for (AudioChangeListener listener: listeners) {
            stop();
            listener.onStateEnded(uriPlaying);
        }
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        for (AudioChangeListener listener: listeners) {
            listener.onIsPlayingChanged(uriPlaying, isPlaying);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        player.pause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        player.resume();
    }
}
