package com.jooheon.toyplayer.domain.repository.library

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.RepeatMode
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import com.jooheon.toyplayer.domain.entity.music.Song

interface PlayingQueueRepository {
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)
    suspend fun getRepeatMode(): RepeatMode
    suspend fun getShuffleMode(): ShuffleMode
    suspend fun getPlayingQueueKey(): Long
    suspend fun setPlayingQueueKey(key: Long)
    suspend fun getPlayingQueue(): Resource<List<Song>>
    suspend fun updatePlayingQueue(songs: List<Song>)
    suspend fun clear()
}