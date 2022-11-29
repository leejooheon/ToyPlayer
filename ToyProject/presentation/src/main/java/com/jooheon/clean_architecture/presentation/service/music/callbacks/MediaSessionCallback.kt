package com.jooheon.clean_architecture.presentation.service.music.callbacks

import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.jooheon.clean_architecture.presentation.service.music.MusicService

class MediaSessionCallback: MediaSessionCompat.Callback() {
    private val TAG = MusicService::class.java.simpleName
    override fun onPlay() {
        super.onPlay()
        Log.d(TAG, "MediaSessionCallback - onPlay")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MediaSessionCallback - onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MediaSessionCallback - onPause")
    }
}