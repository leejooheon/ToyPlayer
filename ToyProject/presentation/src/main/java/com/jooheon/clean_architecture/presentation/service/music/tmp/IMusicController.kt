package com.jooheon.clean_architecture.presentation.service.music.tmp

import com.jooheon.clean_architecture.domain.entity.Entity
import kotlinx.coroutines.CoroutineScope


interface IMusicController {
    fun loadMusic(scope: CoroutineScope)
    fun updateQueueSong(songs: List<Entity.Song>)

    suspend fun play(song: Entity.Song)
    suspend fun stop()
//    fun resume()
//    fun pause()
//    fun previous()
//    fun next()
//    fun forward()
//    fun backward()
//    fun changePlaybackMode()
//    fun snapTo(duration: Long)
//    fun updateSong(song: Entity.Song)
//    fun playAll(songs: List<Entity.Song>)
//    fun setShuffled(shuffle: Boolean)
//    fun hideBottomMusicPlayer()
//    fun showBottomMusicPlayer()
}
