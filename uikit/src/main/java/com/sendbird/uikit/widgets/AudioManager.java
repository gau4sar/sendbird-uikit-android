package com.sendbird.uikit.widgets;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;

import java.util.ArrayList;
import java.util.List;

public class AudioManager {
    public interface AudioChangeListener {
        void onAudioChanged();
    }

    private static AudioManager sInstance = null;
    private Uri uriPlaying;
    private int progress;

    private final List<AudioChangeListener> listeners = new ArrayList<>();
    private AudioPlayer player = AudioPlayer.getInstance();
    
    private AudioManager() {
    }

    public static AudioManager getInstance() {
        if (sInstance == null) {
            sInstance = new AudioManager();
        }
        return sInstance;
    }

    public void registerAudioChangeListener(AudioChangeListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterAudioChangeListener(AudioChangeListener listener) {
        this.listeners.remove(listener);
    }

    public void togglePlay(Uri uri) {
        if (uriPlaying != uri) {
            uriPlaying = uri;
            player.stop();
            player.start(uriPlaying);
        }
        else {
            uriPlaying = null;
            player.stop();
        }

        for (AudioChangeListener listener: listeners) {
            listener.onAudioChanged();
        }
    }

    public Uri getUriPlaying() {
        return uriPlaying;
    }

    public void setUriPlaying(Uri uriPlaying) {
        this.uriPlaying = uriPlaying;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
