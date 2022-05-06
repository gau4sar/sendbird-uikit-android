package com.sendbird.uikit.widgets;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.sendbird.uikit.R;
import com.sendbird.uikit.databinding.SbViewAudioPlayerBinding;

public class AudioPlayerView extends FrameLayout {

    private SbViewAudioPlayerBinding binding;
    private android.net.Uri uri;

    public AudioPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public AudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.sb_message_user_style);
    }

    public AudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null);
    }

    private AudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, ViewGroup parent) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, parent);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, ViewGroup parent) {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.sb_view_audio_player, parent, true);
        initListeners();
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void update() {
        if (uri == AudioManager.getInstance().getUriPlaying()) {
            binding.sbDuration.setProgress(AudioManager.getInstance().getProgress());
            binding.btnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            binding.sbDuration.setProgress(0);
            binding.btnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void initListeners() {
        binding.btnPlay.setOnClickListener(v -> AudioManager.getInstance().togglePlay(uri));
    }
}