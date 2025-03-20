package com.jooheon.toyplayer.features.settings.presentation.main.model

import com.jooheon.toyplayer.features.settings.presentation.language.LanguageType

sealed interface SettingsUiEvent {
    data object OnNavigateTheme: SettingsUiEvent
    data object OnLanguageDialog: SettingsUiEvent
    data object OnNavigateEqualizer: SettingsUiEvent
    data object OnVolumeDialog: SettingsUiEvent
    data object OnNavigateOpenSourceLicense: SettingsUiEvent

    data class OnLanguageSelected(val type: LanguageType): SettingsUiEvent
    data class OnVolumeChanged(val volume: Float): SettingsUiEvent
}