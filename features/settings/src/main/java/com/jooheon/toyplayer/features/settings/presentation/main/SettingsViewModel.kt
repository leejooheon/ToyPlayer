package com.jooheon.toyplayer.features.settings.presentation.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.audiofx.AudioEffect
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.features.commonui.controller.SnackbarController
import com.jooheon.toyplayer.features.commonui.controller.SnackbarEvent
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.settings.presentation.main.model.SettingsUiEvent
import com.jooheon.toyplayer.features.settings.presentation.language.LanguageType
import com.jooheon.toyplayer.features.settings.presentation.main.model.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val musicStateHolder: MusicStateHolder,
    private val playerSettingsUseCase: PlayerSettingsUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState.default)
    internal val uiState = _uiState.asStateFlow()

    private var audioSessionId: Int = 0

    init {
        collectStates()
    }

    internal fun loadData(
        context: Context
    ) = viewModelScope.launch(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            playerController.sendCustomCommand(
                context = context,
                command = CustomCommand.GetAudioSessionId,
                listener = {
                    continuation.resume(it)
                }
            )
        }.onSuccess {
            audioSessionId = it.getInt(CustomCommand.GetAudioSessionId.KEY).defaultZero()
        }

        _uiState.update {
            it.copy(
                models = SettingsUiState.Model.getSettingListItems(),
                currentLanguageType = LanguageType.current(),
            )
        }
    }

    internal fun dispatch(
        context: Context,
        event: SettingsUiEvent,
    ) = viewModelScope.launch {
        when(event) {
            is SettingsUiEvent.OnLanguageSelected -> onLanguageSelected(context, event.type)
            is SettingsUiEvent.OnVolumeChanged -> onVolumeChanged(event.volume)
            SettingsUiEvent.OnNavigateEqualizer -> navigateToEqualizer(context)
            SettingsUiEvent.OnLanguageDialog -> { /** nothing **/ }
            SettingsUiEvent.OnNavigateTheme -> { /** nothing **/ }
            SettingsUiEvent.OnVolumeDialog -> { /** nothing **/ }
            SettingsUiEvent.OnNavigateOpenSourceLicense -> { /** nothing **/ }
        }
    }

    private fun collectStates() = viewModelScope.launch {
        launch {
            musicStateHolder.volume.collectLatest { volume ->
                _uiState.update {
                    it.copy(
                        volume = volume
                    )
                }
            }
        }
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
    }

    private suspend fun onVolumeChanged(volume: Float) {
        playerSettingsUseCase.setVolume(volume)
    }

    private suspend fun navigateToEqualizer(context: Context) {
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
            putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
            putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
            putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val event = SnackbarEvent(UiText.StringResource(Strings.equalizer_error))
            SnackbarController.sendEvent(event)
        }
    }
}