package com.jooheon.toyplayer.data.equalizer

import com.jooheon.toyplayer.data.equalizer.dao.EqualizerDao
import com.jooheon.toyplayer.data.equalizer.dao.data.PresetEntity.Companion.toPresetEntity
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EqualizerDataSource @Inject constructor(
    private val dao: EqualizerDao,
) {
    fun getAllPresets(): Flow<List<Preset>> {
        return dao.getAllPresets()
            .map { presets -> presets.map { it.toPreset() } }
    }
    fun getPresets(type: EqualizerType): Flow<List<Preset>> {
        return dao.getPresets(type)
            .map { presets -> presets.map { it.toPreset() } }
    }

    fun getPreset(id: Int): Flow<Preset?> {
        return dao.get(id)
            .map { it?.toPreset() }
    }

    suspend fun count() = dao.count()
    suspend fun countByType(type: EqualizerType) = dao.countByType(type)

    suspend fun updatePreset(preset: Preset) {
        dao.update(preset.toPresetEntity())
    }

    suspend fun insertPreset(preset: Preset) {
        dao.insert(preset.toPresetEntity())
    }

    suspend fun deletePreset(preset: Preset) {
        dao.delete(preset.toPresetEntity())
    }
}