package com.jooheon.toyplayer.domain.repository.api

import kotlinx.coroutines.flow.Flow

interface PlayerSettingsRepository {
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)
    suspend fun setVolume(volume: Float)
    suspend fun flowRepeatMode(): Flow<Int>
    suspend fun flowShuffleMode(): Flow<Boolean>
    suspend fun flowVolume(): Flow<Float>
}