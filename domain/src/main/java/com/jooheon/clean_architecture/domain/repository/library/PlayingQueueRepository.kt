package com.jooheon.clean_architecture.domain.repository.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song

interface PlayingQueueRepository {
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)
    suspend fun getRepeatMode(): RepeatMode
    suspend fun getShuffleMode(): ShuffleMode
    suspend fun getPlayingQueuePosition(): Int
    suspend fun setPlayingQueuePosition(position: Int)
    suspend fun getPlayingQueue(): Resource<List<Song>>
    suspend fun updatePlayingQueue(songs: List<Song>)
    suspend fun clear()
}