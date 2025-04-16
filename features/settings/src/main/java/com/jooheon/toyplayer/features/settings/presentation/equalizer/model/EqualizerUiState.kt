package com.jooheon.toyplayer.features.settings.presentation.equalizer.model

import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset


data class EqualizerUiState(
    val presetGroups: List<PresetGroup>,
    val soundGroup: SoundGroup,
    val selectedPreset: Preset,
) {
    data class PresetGroup(
        val type: EqualizerType,
        val presets: List<Preset>
    ) {
        companion object {
            val default = PresetGroup(
                type = EqualizerType.default,
                presets = emptyList()
            )

            val preview = PresetGroup(
                type = EqualizerType.default,
                presets = listOf(Preset.preview, Preset.preview, Preset.preview)
            )
        }
    }

    data class SoundGroup(
        val bassBoost: Int,
        val systemVolume: Pair<Int, Int>,
        val playerVolume: Float,
        val channelBalance: Float,
    ) {
        companion object {
            val default = SoundGroup(
                bassBoost = 0,
                systemVolume = 0 to 15,
                playerVolume = 0f,
                channelBalance = 0f,
            )
            val preview = SoundGroup(
                bassBoost = 50,
                systemVolume = 7 to 15,
                playerVolume = 0.5f,
                channelBalance = 0f,
            )
        }
    }

    companion object {
        val default = EqualizerUiState(
            presetGroups = emptyList(),
            soundGroup = SoundGroup.default,
            selectedPreset = Preset.default,
        )

        val preview = EqualizerUiState(
            presetGroups = listOf(PresetGroup.preview),
            soundGroup = SoundGroup.preview,
            selectedPreset = Preset.preview,
        )
    }
}