package com.jooheon.clean_architecture.features.musicservice.usecase

import androidx.media3.common.MediaItem
import com.jooheon.clean_architecture.domain.entity.music.Song


interface IMusicController {
    fun mediaItemPosition(): Int
    suspend fun getPlayingQueue(): List<Song>
    suspend fun openPlayingQueue(songs: List<Song>, startIndex: Int)
    suspend fun addToPlayingQueue(songs: List<Song>, position: Int)
    suspend fun deleteFromPlayingQueue(songIndexList: List<Int>, deletedSongs: List<Song>)
    suspend fun play(index: Int)
    suspend fun resume()
    suspend fun stop()
    suspend fun pause()
    suspend fun snapTo(duration: Long, fromUser: Boolean)
    suspend fun previous()
    suspend fun next()
    suspend fun changeRepeatMode(repeatMode: Int)
    suspend fun changeShuffleMode(shuffleModeEnabled: Boolean)
    suspend fun changeSkipDuration()
}
