package com.sendbird.uikit.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.FileMessage;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.consts.StringSet;
import com.sendbird.uikit.fragments.PhotoViewFragment;
import com.sendbird.uikit.utils.MessageUtils;

public class VideoViewActivity extends AppCompatActivity {

    public static Intent newIntent(@NonNull Context context, @NonNull String url) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra(StringSet.KEY_VIDEO_URL, url);
        return intent;
    }

    private ExoPlayer exoPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SendBirdUIKit.isDarkMode() ? R.style.SendBird_Dark : R.style.SendBird);
        setContentView(R.layout.sb_video_activity);

        final Intent intent = getIntent();
        final String videoUrl = intent.getStringExtra(StringSet.KEY_VIDEO_URL);

        StyledPlayerView playerView = findViewById(R.id.playerView);
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.setPlayWhenReady(true);
        playerView.setPlayer(exoPlayer);
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)));
        exoPlayer.prepare();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        exoPlayer.play();
    }

    @Override
    protected void onDestroy() {
        exoPlayer.stop();
        exoPlayer.release();
        super.onDestroy();
    }
}
