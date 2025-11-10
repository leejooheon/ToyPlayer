package com.jooheon.toyplayer.features.settings.presentation.main.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Accessible
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.SpatialAudio
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.audio.AudioUsage
import com.jooheon.toyplayer.features.settings.R
import com.jooheon.toyplayer.features.settings.presentation.language.LanguageType

data class SettingsUiState(
    val models: List<Model>,
    val currentLanguageType: LanguageType,
    val volume: Float,
    val audioUsage: AudioUsage
) {
    companion object {
        val default = SettingsUiState(
            models = emptyList(),
            currentLanguageType = LanguageType.default,
            volume = 0f,
            audioUsage = AudioUsage.MEDIA
        )
    }

    enum class DialogState {
        NONE,
        LANGUAGE,
        VOLUME,
        AUDIO_USAGE,
        ;
    }

    class Model(
        val event: SettingsUiEvent,
        val title: UiText,
        val iconImageVector: ImageVector,
    ) {
        companion object {
            fun getSettingListItems(): List<Model> {
                return listOf(
                    Model(
                        event = SettingsUiEvent.OnLanguageDialog,
                        title = UiText.StringResource(R.string.setting_language),
                        iconImageVector = Icons.Outlined.Language
                    ),
                    Model(
                        event = SettingsUiEvent.OnNavigateTheme,
                        title = UiText.StringResource(R.string.setting_theme),
                        iconImageVector = Icons.Outlined.WbSunny
                    ),
                    Model(
                        event = SettingsUiEvent.OnNavigateEqualizer,
                        title = UiText.StringResource(R.string.setting_equalizer),
                        iconImageVector = Icons.Outlined.Equalizer
                    ),
                    Model(
                        event = SettingsUiEvent.OnVolumeDialog,
                        title = UiText.StringResource(R.string.setting_volume),
                        iconImageVector = Icons.AutoMirrored.Outlined.VolumeUp
                    ),
                    Model(
                        event = SettingsUiEvent.OnAudioUsageDialog,
                        title = UiText.StringResource(R.string.setting_audio_usage),
                        iconImageVector = Icons.Outlined.SpatialAudio
                    ),
                    Model(
                        event = SettingsUiEvent.OnNavigateOpenSourceLicense,
                        title = UiText.StringResource(R.string.setting_opensource_license),
                        iconImageVector = Icons.Default.FolderOpen
                    ),
                )
            }
        }
    }
}