package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.coroutines.flow.Flow

interface PlayingQueueUseCase {
    fun playingQueue(): Flow<List<Song>>
    suspend fun openQueue(vararg song: Song)
    suspend fun addToPlayingQueue(vararg song: Song)
    suspend fun deletePlayingQueue(vararg song: Song)
    suspend fun clear()
}