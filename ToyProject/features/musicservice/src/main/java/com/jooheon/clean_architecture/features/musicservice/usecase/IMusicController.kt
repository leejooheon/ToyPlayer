package com.jooheon.clean_architecture.features.musicservice.usecase

import com.jooheon.clean_architecture.domain.entity.music.Song


interface IMusicController {
    fun loadPlaylist(
        onPlayListLoaded: ((MutableList<Song>) -> Unit)? = null
    )

    fun play(song: Song)
    suspend fun resume()
    suspend fun stop()
    suspend fun pause()
    suspend fun snapTo(duration: Long, fromUser: Boolean)
    suspend fun previous()
    suspend fun next()
    suspend fun changeRepeatMode()
    suspend fun changeShuffleMode()
    suspend fun changeSkipDuration()
    suspend fun refresh()
}
