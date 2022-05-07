package com.sendbird.uikit.widgets;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

public class AudioPlayer {

    public interface AudioPlayerListener {
        void onStateEnded();
        void onIsPlayingChanged(boolean isPlaying);
    }


    private static AudioPlayer sInstance = null;
    private ExoPlayer exoPlayer;
    private AudioPlayerListener listener;

    private final Player.Listener playStateListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            switch (playbackState) {
                case ExoPlayer.STATE_ENDED:
                    listener.onStateEnded();
                    break;
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            listener.onIsPlayingChanged(isPlaying);
        }
    };

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
        exoPlayer.addListener(playStateListener);
    }

    public void setListener(AudioPlayerListener listener) {
        this.listener = listener;
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

    public void pause() {
        if (exoPlayer.getMediaItemCount() > 0) {
            exoPlayer.pause();
        }
    }

    public void resume() {
        if (exoPlayer.getMediaItemCount() > 0) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public long getCurrentProgress() {
        long currentPosition = exoPlayer.getCurrentPosition();
        long duration = exoPlayer.getDuration();
        return 100 * currentPosition / duration;
    }


}
