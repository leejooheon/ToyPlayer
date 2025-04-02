package com.jooheon.toyplayer.features.settings.presentation.equalizer.model

import android.content.Context
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset

sealed interface EqualizerUiEvent {
    data class OnPresetSelected(val preset: Preset) : EqualizerUiEvent
    data class OnTypeSelected(val type: EqualizerType) : EqualizerUiEvent
    data class OnGainsChanged(val gains: List<Float>) : EqualizerUiEvent
    data class OnPresetSave(val preset: Preset): EqualizerUiEvent
    data class OnPresetUpdate(val preset: Preset): EqualizerUiEvent
    data class OnPresetDelete(val preset: Preset): EqualizerUiEvent
    data class OnSettingClick(val context: Context): EqualizerUiEvent
}