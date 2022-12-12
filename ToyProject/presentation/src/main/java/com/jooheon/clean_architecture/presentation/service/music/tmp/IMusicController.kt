package com.jooheon.clean_architecture.presentation.service.music.tmp

import com.jooheon.clean_architecture.domain.entity.Entity
import kotlinx.coroutines.CoroutineScope


interface IMusicController {
    fun loadMusic(scope: CoroutineScope)
    fun updateQueueSong(songs: List<Entity.Song>)

    suspend fun play(song: Entity.Song)
    suspend fun stop()
    suspend fun pause()
    suspend fun snapTo(duration: Long)
    suspend fun previous()
    suspend fun next()
//    fun forward()
//    fun backward()
//    fun changePlaybackMode()
//    fun updateSong(song: Entity.Song)
//    fun playAll(songs: List<Entity.Song>)
//    fun setShuffled(shuffle: Boolean)
//    fun hideBottomMusicPlayer()
//    fun showBottomMusicPlayer()
}
