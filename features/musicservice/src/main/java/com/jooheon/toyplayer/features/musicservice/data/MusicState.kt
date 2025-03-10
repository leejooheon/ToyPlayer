package com.jooheon.toyplayer.features.musicservice.data

import android.media.session.PlaybackState
import androidx.media3.common.C
import com.jooheon.toyplayer.domain.model.music.Song

import kotlinx.serialization.Serializable

@Serializable
data class MusicState(
    val currentPlayingMusic: Song = Song.default,
    val playbackState: Int = PlaybackState.STATE_NONE,
    val timePassed: Long = C.TIME_UNSET
) {
    fun isLoading(): Boolean {
        return playbackState in listOf(PlaybackState.STATE_BUFFERING)
    }
    fun isPlaying(): Boolean {
        return playbackState in listOf(PlaybackState.STATE_PLAYING, PlaybackState.STATE_BUFFERING)
    }

    companion object {
        val default = MusicState()
        val preview = MusicState(
            currentPlayingMusic = Song.preview,
            playbackState = PlaybackState.STATE_PLAYING,
            timePassed = 1000L
        )
    }
}