package com.jooheon.toyplayer.features.settings.presentation.equalizer.model

import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset


data class EqualizerUiState(
    val presetGroups: List<PresetGroup>,
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

    companion object {
        val default = EqualizerUiState(
            presetGroups = emptyList(),
            selectedPreset = Preset.default,
        )

        val preview = EqualizerUiState(
            presetGroups = listOf(PresetGroup.preview),
            selectedPreset = Preset.preview,
        )
    }
}