package com.jooheon.toyplayer.domain.repository.api

import kotlinx.coroutines.flow.Flow

interface PlayerSettingsRepository {
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)
    suspend fun setVolume(volume: Float)
    suspend fun setEqualizerPreset(preset: String)
    fun flowRepeatMode(): Flow<Int>
    fun flowShuffleMode(): Flow<Boolean>
    fun flowVolume(): Flow<Float>
    fun flowEqualizerPreset(): Flow<String>
}