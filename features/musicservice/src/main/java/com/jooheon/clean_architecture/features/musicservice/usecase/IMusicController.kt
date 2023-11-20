package com.jooheon.clean_architecture.features.musicservice.usecase

import androidx.annotation.FloatRange
import androidx.media3.common.MediaItem
import androidx.media3.common.Player

interface IMusicController {
    suspend fun setMediaItems(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        playWhenReady: Boolean,
    )
    suspend fun addMediaItems(
        mediaItems: List<MediaItem>,
        addNext: Boolean,
        playWhenReady: Boolean,
    )
    suspend fun removeMeidaItems(
        mediaItemsIndices: List<Int>,
    )

    suspend fun play(
        index: Int,
        seekTo: Long,
        playWhenReady: Boolean,
    )
    suspend fun stop()
    suspend fun pause()
    suspend fun snapTo(duration: Long, fromUser: Boolean)
    suspend fun previous()
    suspend fun next()
    suspend fun changeRepeatMode(@Player.RepeatMode repeatMode: Int)
    suspend fun changeShuffleMode(shuffleModeEnabled: Boolean)
    suspend fun changePlaybackSpeed(@FloatRange(from = 0.1, to = 1.0) playbackSpeed: Float)

    fun releaseMediaBrowser()
}