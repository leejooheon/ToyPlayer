package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.coroutines.flow.Flow

interface PlayingQueueUseCase {
    suspend fun repeatModeChanged(repeatMode: Int)
    suspend fun shuffleModeChanged(shuffleEnabled: Boolean)
    suspend fun repeatMode(): RepeatMode
    suspend fun shuffleMode(): ShuffleMode
    suspend fun getPlayingQueueKey(): Long
    suspend fun setPlayingQueueKey(key: Long)
    suspend fun playingQueue(): Flow<Resource<List<Song>>>
    suspend fun getPlayingQueue(): List<Song>
    suspend fun updatePlayingQueue(songs: List<Song>): Boolean
    suspend fun clear()
}