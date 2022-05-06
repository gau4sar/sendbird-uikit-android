package com.sendbird.uikit.widgets;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class AudioPlayer {

    private static AudioPlayer sInstance = null;
    private ExoPlayer exoPlayer;

    private AudioPlayer() {
    }

    public static AudioPlayer getInstance() {
        if (sInstance == null) {
            sInstance = new AudioPlayer();
        }
        return sInstance;
    }

    public void init(Context context) {
        exoPlayer = new ExoPlayer.Builder(context).build();
    }

    public void start(Uri uri) {
        exoPlayer.setMediaItem(MediaItem.fromUri(uri));
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

    public void stop() {
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.stop();
    }
}
