package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.domain.repository.api.EqualizerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EqualizerUseCase @Inject constructor(
    private val equalizerRepository: EqualizerRepository,
) {
    fun flowPreset(id: Int) = equalizerRepository.flowPreset(id)
    fun flowAllPresets() = equalizerRepository.flowAllPresets()
    fun flowPresets(type: EqualizerType): Flow<List<Preset>> = equalizerRepository
        .flowPresets(type)
        .map { getEqualizerPresets(type) + it }

    private suspend fun getEqualizerPresets(type: EqualizerType): List<Preset> {
        return equalizerRepository.getBandEqualizerPresets(type)
    }

    suspend fun updatePreset(preset: Preset): Result<Unit, PlaybackDataError> {
        equalizerRepository.updatePreset(preset)
        return Result.Success(Unit)
    }

    suspend fun updatePresetName(preset: Preset): Result<Unit, PlaybackDataError> {
        return validatePresetName(preset.name)
            .onSuccess { equalizerRepository.updatePreset(preset) }
            .onError { Result.Error(it) }
    }

    suspend fun insertPreset(preset: Preset): Result<Unit, PlaybackDataError> {
        return validatePresetName(preset.name)
            .onSuccess { equalizerRepository.insertPreset(preset) }
            .onError { Result.Error(it) }
    }

    suspend fun deletePreset(preset: Preset): Result<Unit, PlaybackDataError> {
        equalizerRepository.deletePreset(preset)
        return Result.Success(Unit)
    }

    private fun validatePresetName(name: String): Result<Unit, PlaybackDataError> {
        return when {
            name == Preset.CUSTOM_PRESET_NAME ->
                Result.Error(PlaybackDataError.InvalidData("Preset name cannot be '${Preset.CUSTOM_PRESET_NAME}'"))

            name.isBlank() ->
                Result.Error(PlaybackDataError.Empty)

            else -> Result.Success(Unit)
        }
    }
}
