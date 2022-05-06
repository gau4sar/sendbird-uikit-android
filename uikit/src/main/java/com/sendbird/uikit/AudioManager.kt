package com.sendbird.uikit

import android.net.Uri

object AudioManager {
    var uriPlaying: Uri? = null
    var progress: Int = 0

    fun togglePlay(uri: Uri) {
        uriPlaying = if (uriPlaying == uri) null
        else uri
    }
}