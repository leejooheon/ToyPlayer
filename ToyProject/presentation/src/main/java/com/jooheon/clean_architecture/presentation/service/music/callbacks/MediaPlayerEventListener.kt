package com.jooheon.clean_architecture.presentation.service.music.callbacks

import android.util.Log
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.jooheon.clean_architecture.presentation.service.music.MusicService

class MediaPlayerEventListener constructor(private val musicService: MusicService): Player.Listener {
    private val TAG = MusicService::class.java.simpleName
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        Log.d(TAG, "onPlaybackStateChanged - $playbackState")
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        Log.d(TAG, "onPlayWhenReadyChanged - ${playWhenReady}, ${reason}")
        if (reason == Player.STATE_READY && !playWhenReady)
            musicService.stopForeground(false)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Log.d(TAG, "onPlayerError - ${error.message}")
    }
}