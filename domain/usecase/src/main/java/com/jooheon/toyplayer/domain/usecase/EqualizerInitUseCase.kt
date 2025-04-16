package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.domain.repository.api.EqualizerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EqualizerInitUseCase @Inject constructor(
    private val equalizerRepository: EqualizerRepository,
){
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        EqualizerType.entries.forEach {
            val count = equalizerRepository.countByType(it)
            if (count == 0) {
                val defaultPreset = Preset(
                    id = 0, // 0으로 넣으면 auto generate됨.
                    name = Preset.CUSTOM_PRESET_NAME,
                    gains = List(it.frequencies().size) { 0f },
                    type = it,
                    isCustom = true,
                )
                equalizerRepository.insertPreset(defaultPreset)
            }
        }
    }
}