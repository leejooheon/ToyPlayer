package com.jooheon.toyplayer.domain.repository.api

import kotlinx.coroutines.flow.Flow

interface PlayerSettingsRepository {
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)
    suspend fun setVolume(volume: Float)
    fun flowRepeatMode(): Flow<Int>
    fun flowShuffleMode(): Flow<Boolean>
    fun flowVolume(): Flow<Float>
}