package com.jooheon.clean_architecture.presentation.service.music.playback

import android.content.Context
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.service.music.player.MultiPlayer

class PlaybackManager(val context: Context) {
    var playback: Playback? = null

    init {
        playback = createLocalPlayback()
    }


    fun play(onNotInitialized: () -> Unit) {
        if (playback != null && !playback!!.isPlaying) {
            if (!playback!!.isInitialized) {
                onNotInitialized()
            } else {
                playback?.start()
            }
        }
    }

    fun pause(force: Boolean, onPause: () -> Unit) {
        if (playback != null && playback!!.isPlaying) {
            playback?.pause()
            onPause()
        }
    }

    fun setDataSource(
        song: Entity.Song,
        force: Boolean,
        completion: (success: Boolean) -> Unit,
    ) {
        playback?.setDataSource(song, force, completion)
    }

    fun setCallbacks(callbacks: Playback.PlaybackCallbacks) {
        playback?.callbacks = callbacks
    }

    private fun createLocalPlayback(): Playback {
        return MultiPlayer(context)
    }
}