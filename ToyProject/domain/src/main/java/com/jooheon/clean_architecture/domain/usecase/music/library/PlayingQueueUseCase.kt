package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.coroutines.flow.Flow

interface PlayingQueueUseCase {
    suspend fun getPlayingQueuePosition(): Int
    suspend fun setPlayingQueuePosition(position: Int)
    suspend fun getPlayingQueue(): Flow<Resource<List<Song>>>
    suspend fun openPlayingQueue(vararg song: Song)
    suspend fun clear()
}