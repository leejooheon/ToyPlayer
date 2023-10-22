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
    suspend fun getPlayingQueuePosition(): Int
    suspend fun setPlayingQueuePosition(position: Int)
    suspend fun getPlayingQueue(): Flow<Resource<List<Song>>>
    suspend fun updatePlayingQueue(vararg song: Song): Boolean
    suspend fun clear()
}