package com.jooheon.toyplayer.features.musicservice.data

import android.media.session.PlaybackState
import com.jooheon.toyplayer.domain.model.music.Song

import kotlinx.serialization.Serializable

@Serializable
data class MusicState(
    val currentPlayingMusic: Song,
    val playbackState: Int,
) {
    fun isLoading(): Boolean {
        return playbackState in listOf(PlaybackState.STATE_BUFFERING)
    }
    fun isPlaying(): Boolean {
        return playbackState in listOf(PlaybackState.STATE_PLAYING, PlaybackState.STATE_BUFFERING)
    }

    companion object {
        val default = MusicState(
            currentPlayingMusic = Song.default,
            playbackState = PlaybackState.STATE_NONE,
        )
        val preview = MusicState(
            currentPlayingMusic = Song.preview,
            playbackState = PlaybackState.STATE_PLAYING,
        )
    }
}