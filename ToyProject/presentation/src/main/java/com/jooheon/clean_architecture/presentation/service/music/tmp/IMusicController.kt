package com.jooheon.clean_architecture.presentation.service.music.tmp

import android.net.Uri
import com.jooheon.clean_architecture.domain.entity.Entity
import kotlinx.coroutines.CoroutineScope

interface IMusicController {
    fun loadMusic(scope: CoroutineScope)
    fun updateQueueSong(songs: List<Entity.Song>)

    suspend fun play(song: Entity.Song)
    suspend fun play(_uri: Uri?)
    suspend fun stop()
    suspend fun pause()
    suspend fun snapTo(duration: Long, fromUser: Boolean)
    suspend fun previous()
    suspend fun next()
    suspend fun changeRepeatMode()
    suspend fun changeShuffleMode()
    suspend fun changeSkipDuration()
}
