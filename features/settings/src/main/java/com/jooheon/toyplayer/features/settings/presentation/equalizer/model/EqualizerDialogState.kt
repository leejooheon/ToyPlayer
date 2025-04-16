package com.jooheon.toyplayer.features.settings.presentation.equalizer.model

import com.jooheon.toyplayer.domain.model.music.Preset

internal data class EqualizerDialogState(
    val type: Type,
    val preset: Preset
) {
    enum class Type { None, Delete, SaveOrEdit }

    companion object {
        val default = EqualizerDialogState(
            type = Type.None,
            preset = Preset.default,
        )
    }
}