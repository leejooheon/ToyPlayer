package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset
import kotlinx.coroutines.flow.Flow

interface EqualizerRepository {
    suspend fun getBandEqualizerPresets(type: EqualizerType): List<Preset>

    fun flowAllPresets(): Flow<List<Preset>>
    fun flowPresets(type: EqualizerType): Flow<List<Preset>>
    fun flowPreset(id: Int): Flow<Preset?>

    suspend fun count(): Int
    suspend fun countByType(type: EqualizerType): Int

    suspend fun updatePreset(preset: Preset)
    suspend fun insertPreset(preset: Preset)
    suspend fun deletePreset(preset: Preset)
}