package com.jooheon.toyplayer.features.settings.presentation.main

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.processphoenix.ProcessPhoenix
import com.jooheon.toyplayer.domain.model.audio.AudioUsage
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.settings.presentation.language.LanguageType
import com.jooheon.toyplayer.features.settings.presentation.main.model.SettingsUiEvent
import com.jooheon.toyplayer.features.settings.presentation.main.model.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val playerSettingsUseCase: PlayerSettingsUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
): ViewModel() {
    private val languageFlow = MutableStateFlow(LanguageType.current())

    val uiState: StateFlow<SettingsUiState> =
        combine(
            playerSettingsUseCase.flowVolume(),
            defaultSettingsUseCase.flowAudioUsage(),
            languageFlow,
        ) { volume, audioUsage, language ->
            Triple(volume, audioUsage, language)
        }.map { (volume, audioUsage, language) ->
            SettingsUiState(
                models = SettingsUiState.Model.getSettingListItems(),
                currentLanguageType = language,
                volume = volume,
                audioUsage = audioUsage,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState.default,
        )

    internal fun loadLanguage() = viewModelScope.launch {
        languageFlow.emit(LanguageType.current())
    }

    internal fun dispatch(
        context: Context,
        event: SettingsUiEvent,
    ) = viewModelScope.launch {
        when(event) {
            is SettingsUiEvent.OnLanguageSelected -> onLanguageSelected(context, event.type)
            is SettingsUiEvent.OnVolumeChanged -> onVolumeChanged(event.volume)
            is SettingsUiEvent.OnAudioUsageChanged -> onAudioUsageChanged(context, event.audioUsage)
            SettingsUiEvent.OnNavigateEqualizer -> { /** nothing **/ }
            SettingsUiEvent.OnLanguageDialog -> { /** nothing **/ }
            SettingsUiEvent.OnNavigateTheme -> { /** nothing **/ }
            SettingsUiEvent.OnVolumeDialog -> { /** nothing **/ }
            SettingsUiEvent.OnAudioUsageDialog -> { /** nothing **/ }
            SettingsUiEvent.OnNavigateOpenSourceLicense -> { /** nothing **/ }
        }
    }

    private suspend fun onAudioUsageChanged(context: Context, audioUsage: AudioUsage) {
        defaultSettingsUseCase.setAudioUsage(audioUsage)
        ProcessPhoenix.triggerRebirth(context)
    }

    private fun onLanguageSelected(context: Context, languageType: LanguageType) {
        val language = languageType.code

        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val appLocale = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)

        playerController.getMusicListFuture(
            context = context,
            mediaId = MediaId.Root,
            listener = { /** update playlist name **/ }
        )
    }

    private suspend fun onVolumeChanged(volume: Float) {
        playerSettingsUseCase.setVolume(volume)
    }
}