package com.sendbird.uikit.widgets

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sendbird.uikit.AudioManager
import com.sendbird.uikit.R
import com.sendbird.uikit.databinding.SbViewAudioPlayerBinding

class AudioPlayerView(context: Context) : FrameLayout(context) {

    private var binding: SbViewAudioPlayerBinding = SbViewAudioPlayerBinding.inflate(LayoutInflater.from(getContext()))

    var uri: Uri? = null

    init {
        initListeners()
    }

    fun update() {
        if (uri.isPlaying()) {
            binding.sbDuration.progress = AudioManager.progress
            binding.btnPlay.setImageResource(R.drawable.ic_pause)
        } else {
            binding.sbDuration.progress = 0
            binding.btnPlay.setImageResource(R.drawable.ic_play)
        }
    }

    private fun initListeners() {
        binding.btnPlay.setOnClickListener {
            uri?.let {
                AudioManager.togglePlay(it)
            }
        }
    }

    private fun Uri?.isPlaying() : Boolean {
        return this == AudioManager.uriPlaying && AudioManager.uriPlaying != null
    }
}