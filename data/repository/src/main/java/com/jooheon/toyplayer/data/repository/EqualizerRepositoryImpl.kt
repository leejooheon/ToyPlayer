package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.equalizer.EqualizerDataSource
import com.jooheon.toyplayer.data.music.RemoteEqualizerDataSource
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.domain.repository.api.EqualizerRepository
import kotlinx.coroutines.flow.Flow

class EqualizerRepositoryImpl(
    private val remoteEqualizerDataSource: RemoteEqualizerDataSource,
    private val equalizerDataSource: EqualizerDataSource,
): EqualizerRepository {
    override suspend fun getBandEqualizerPresets(type: EqualizerType): List<Preset> {
        return when(type) {
            EqualizerType.BAND_03 -> remoteEqualizerDataSource.getBand03EqualizerPresets()
            EqualizerType.BAND_05 -> remoteEqualizerDataSource.getBand05EqualizerPresets()
            EqualizerType.BAND_10 -> remoteEqualizerDataSource.getBand10EqualizerPresets()
            EqualizerType.BAND_15 -> remoteEqualizerDataSource.getBand15EqualizerPresets()
            EqualizerType.BAND_31 -> remoteEqualizerDataSource.getBand31EqualizerPresets()
        }
    }

    override fun flowAllPresets(): Flow<List<Preset>> {
        return equalizerDataSource.getAllPresets()
    }
    override fun flowPresets(type: EqualizerType): Flow<List<Preset>> {
        return equalizerDataSource.getPresets(type)
    }
    override fun flowPreset(id: Int): Flow<Preset?> {
        return equalizerDataSource.getPreset(id)
    }

    override suspend fun count(): Int {
        return equalizerDataSource.count()
    }
    override suspend fun countByType(type: EqualizerType): Int {
        return equalizerDataSource.countByType(type)
    }

    override suspend fun updatePreset(preset: Preset) {
        equalizerDataSource.updatePreset(preset)
    }

    override suspend fun insertPreset(preset: Preset) {
        equalizerDataSource.insertPreset(preset)
    }

    override suspend fun deletePreset(preset: Preset) {
        equalizerDataSource.deletePreset(preset)
    }
}