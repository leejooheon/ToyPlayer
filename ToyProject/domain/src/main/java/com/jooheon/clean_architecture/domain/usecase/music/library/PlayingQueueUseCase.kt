package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.coroutines.flow.Flow

interface PlayingQueueUseCase {
    fun playingQueue(): Flow<List<Song>>
    suspend fun getAutoPlayWhenQueueChanged(): Pair<Boolean, Song>?
    suspend fun openQueue(vararg song: Song, autoPlayWhenQueueChanged: Boolean)
    suspend fun addToPlayingQueue(vararg song: Song, autoPlayWhenQueueChanged: Boolean)
    suspend fun deletePlayingQueue(vararg song: Song)
    suspend fun clear()
}