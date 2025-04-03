package com.jooheon.toyplayer.domain.repository.api

import kotlinx.coroutines.flow.Flow

interface PlayerSettingsRepository {
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)
    suspend fun setVolume(volume: Float)
    suspend fun setEqualizerPreset(preset: String)
    suspend fun setChannelBalance(channelBalance: Float)
    suspend fun setBassBoost(bassBoost: Int)

    fun flowRepeatMode(): Flow<Int>
    fun flowShuffleMode(): Flow<Boolean>
    fun flowVolume(): Flow<Float>
    fun flowEqualizerPreset(): Flow<String>
    fun flowChannelBalance(): Flow<Float>
    fun flowBassBoost(): Flow<Int>
}