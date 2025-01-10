package com.jooheon.toyplayer.domain.repository

import com.jooheon.toyplayer.domain.entity.music.RepeatMode
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import kotlinx.coroutines.flow.Flow

interface PlaybackSettingsRepository {
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)
    suspend fun setSkipDuration(duration: Long)
    suspend fun setPlayingQueueKey(key: Long)

    fun flowRepeatMode(): Flow<RepeatMode>
    fun flowShuffleMode(): Flow<ShuffleMode>
    fun flowSkipDuration(): Flow<Long>
    suspend fun getPlayingQueueKey(): Long
}