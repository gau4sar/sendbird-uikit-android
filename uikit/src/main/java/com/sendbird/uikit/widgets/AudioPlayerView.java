package com.sendbird.uikit.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.sendbird.uikit.R;
import com.sendbird.uikit.databinding.SbViewAudioPlayerBinding;

public class AudioPlayerView extends FrameLayout implements AudioManager.AudioChangeListener {

    private SbViewAudioPlayerBinding binding;
    private android.net.Uri uri;
    private final AudioManager audioManager = AudioManager.getInstance();

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.sbDuration.setProgress(audioManager.getProgress(), true);
            } else {
                binding.sbDuration.setProgress(audioManager.getProgress());
            }
            progressHandler.postDelayed(progressRunnable, 100);
        }
    };

    private final Handler progressHandler = new Handler();

    public AudioPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public AudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.sb_message_file_style);
    }

    public AudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null);
    }

    private AudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, ViewGroup parent) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, parent);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, ViewGroup parent) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MessageView_File, defStyleAttr, 0);
        try {
            if (parent == null) {
                parent = this;
            }
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.sb_view_audio_player, parent, true);
            binding.sbDuration.setMax(100);
            binding.sbDuration.setProgress(0);
        } finally {
            a.recycle();
        }
    }

    public void setupUi(boolean isMyMessage) {
        if (isMyMessage) {
            // Audio background
            binding.btnPlay.setBackgroundResource(R.drawable.bg_my_audio_play);
            binding.btnPlay.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary_400));

            // Progress
            binding.sbDuration.getProgressDrawable().setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.background_50),
                    PorterDuff.Mode.SRC_IN
            );
        } else {
            // Audio background
            binding.btnPlay.setBackgroundResource(R.drawable.bg_other_audio_play);
            binding.btnPlay.setColorFilter(ContextCompat.getColor(getContext(), R.color.background_50));

            // Progress
            binding.sbDuration.getProgressDrawable().setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.primary_400),
                    PorterDuff.Mode.SRC_IN
            );
        }
    }

    public void bind(Uri uri) {
        this.uri = uri;
        initListeners();
    }

    private void updatePlayState(boolean isPlaying) {
        if (audioManager.isUriPlaying(uri)) {
            Log.e("nt.dung", "isPlaying: " + isPlaying);
            if (isPlaying) {
                startProgress();
                binding.btnPlay.setImageResource(R.drawable.ic_pause);
            } else {
                stopProgress();
                binding.btnPlay.setImageResource(R.drawable.ic_play);
            }
        } else {
            stopProgress();
            binding.sbDuration.setProgress(0);
            binding.btnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void initListeners() {
        audioManager.registerAudioChangeListener(this);
        binding.btnPlay.setOnClickListener(v -> audioManager.togglePlay(uri));

        Log.e("nt.dung", "isUriPlaying: " + (audioManager.isUriPlaying(uri)) + ", isPlaying: " + audioManager.isPlaying());
        updatePlayState(audioManager.isPlaying());
    }

    @Override
    protected void onDetachedFromWindow() {
        audioManager.unregisterAudioChangeListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onAudioChanged(Uri uriPlaying, boolean isPlaying) {
        updatePlayState(isPlaying);
    }

    @Override
    public void onStateEnded(Uri uriPlaying) {
        if (audioManager.isUriPlaying(uri)) {
            stopProgress();
            binding.sbDuration.setProgress(0);
            binding.btnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void onIsPlayingChanged(Uri uriPlaying, boolean isPlaying) {
        if (audioManager.isUriPlaying(uri)) {
//            if (isPlaying) startProgress();
//            else stopProgress();
        }
    }

    private void startProgress() {
        progressHandler.post(progressRunnable);
    }

    private void stopProgress() {
        progressHandler.removeCallbacks(progressRunnable);
    }
}