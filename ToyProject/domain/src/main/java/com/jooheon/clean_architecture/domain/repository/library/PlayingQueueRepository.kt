package com.jooheon.clean_architecture.domain.repository.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.coroutines.flow.Flow

interface PlayingQueueRepository {
    suspend fun getPlayingQueuePosition(): Int
    suspend fun setPlayingQueuePosition(position: Int)
    suspend fun getPlayingQueue(): Resource<List<Song>>
    suspend fun updatePlayingQueue(songs: List<Song>)
    suspend fun clear()
}