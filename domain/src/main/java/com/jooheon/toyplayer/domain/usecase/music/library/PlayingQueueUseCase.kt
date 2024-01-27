package com.jooheon.toyplayer.domain.usecase.music.library

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.RepeatMode
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import com.jooheon.toyplayer.domain.entity.music.Song
import kotlinx.coroutines.flow.Flow

interface PlayingQueueUseCase {
    suspend fun repeatModeChanged(repeatMode: Int)
    suspend fun shuffleModeChanged(shuffleEnabled: Boolean)
    suspend fun repeatMode(): RepeatMode
    suspend fun shuffleMode(): ShuffleMode
    suspend fun getRecentMediaItemKey(): Long
    suspend fun setRecentMediaItemKey(key: Long)
    suspend fun playingQueue(): Flow<Resource<List<Song>>>
    suspend fun getPlayingQueue(): List<Song>
    suspend fun setPlayingQueue(songs: List<Song>): Boolean
    suspend fun clear()
}