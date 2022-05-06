package com.sendbird.uikit.widgets;

import android.net.Uri;

public class AudioManager {
    private static AudioManager sInstance = null;
    private Uri uriPlaying;
    private int progress;

    private AudioManager() {

    }

    public static AudioManager getInstance() {
        if (sInstance == null) {
            sInstance = new AudioManager();
        }
        return sInstance;
    }

    public void togglePlay(Uri uri) {
        if (uriPlaying == null) uriPlaying = uri;
        else uriPlaying = null;
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
