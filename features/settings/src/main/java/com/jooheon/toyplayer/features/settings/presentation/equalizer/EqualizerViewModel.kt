package com.jooheon.toyplayer.features.settings.presentation.equalizer

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.core.system.audio.AudioOutputObserver
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.EqualizerType.Companion.toType
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.domain.usecase.EqualizerUseCase
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.features.common.controller.SnackbarController
import com.jooheon.toyplayer.features.common.controller.SnackbarEvent
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiEvent
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val equalizerUseCase: EqualizerUseCase,
    private val playerSettingsUseCase: PlayerSettingsUseCase,
    private val audioOutputObserver: AudioOutputObserver
): ViewModel() {
    private val equalizerTypes = EqualizerType.entries

    private val presetFlows: List<Flow<List<Preset>>> = equalizerTypes.map { equalizerUseCase.flowPresets(it) }
    private val combinedPresetsFlow = combine(presetFlows) { results ->
        equalizerTypes.zip(results.toList())
            .map { (type, presets) -> EqualizerUiState.PresetGroup(type, presets) }
    }
    private val soundGroupFlow = combine(
        audioOutputObserver.observeSystemVolume(),
        playerSettingsUseCase.flowBassBoost(),
        playerSettingsUseCase.flowVolume(),
        playerSettingsUseCase.flowChannelBalance()
    ) { systemVolume, bassBoost, playerVolume, channelBalance ->
        EqualizerUiState.SoundGroup(
            bassBoost = bassBoost,
            systemVolume = systemVolume,
            playerVolume = playerVolume,
            channelBalance = channelBalance,
        )
    }

    internal val uiState = combine(
        combinedPresetsFlow,
        soundGroupFlow,
        playerSettingsUseCase.flowEqualizerPreset(),
    ) { presetGroups, soundGroup, selectedPreset ->
        buildEqualizerUiState(
            presetGroups = presetGroups,
            soundGroup = soundGroup,
            selectedPreset = selectedPreset
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EqualizerUiState.default
    )

    internal fun dispatch(event: EqualizerUiEvent) = viewModelScope.launch {
        when(event) {
            is EqualizerUiEvent.OnPresetSelected -> playerSettingsUseCase.setEqualizerPreset(event.preset)
            is EqualizerUiEvent.OnTypeSelected -> onTypeSelected(event.type)
            is EqualizerUiEvent.OnGainsChanged -> onGainsChanged(event.gains)
            is EqualizerUiEvent.OnPresetSave -> onPresetSave(event.preset)
            is EqualizerUiEvent.OnPresetUpdate -> onPresetUpdate(event.preset)
            is EqualizerUiEvent.OnPresetDelete -> onPresetDelete(event.preset)
            is EqualizerUiEvent.OnSettingClick -> navigateToEqualizer(event.context)
            is EqualizerUiEvent.OnBassBoostChanged -> playerSettingsUseCase.setBassBoost(event.value)
            is EqualizerUiEvent.OnPlayerVolumeChanged -> playerSettingsUseCase.setVolume(event.value)
            is EqualizerUiEvent.OnChannelBalanceChanged -> playerSettingsUseCase.setChannelBalance(event.value)
            is EqualizerUiEvent.OnSystemVolumeChanged -> onSystemVolumeChanged(event.value)
        }
    }

    private fun onSystemVolumeChanged(value: Int) {
        val state = uiState.value
        if(state.soundGroup.systemVolume.first == value) return
        audioOutputObserver.setVolume(value)
    }

    private suspend fun onPresetUpdate(preset: Preset) = withContext(Dispatchers.IO) {
        equalizerUseCase
            .updatePresetName(preset)
            .onSuccess { playerSettingsUseCase.setEqualizerPreset(preset) }
            .onError { sendError() }
    }

    private suspend fun onPresetDelete(preset: Preset) = withContext(Dispatchers.IO) {
        equalizerUseCase
            .deletePreset(preset)
            .onSuccess { playerSettingsUseCase.setEqualizerPreset(Preset.default) }
    }

    private suspend fun onPresetSave(preset: Preset) = withContext(Dispatchers.IO) {
        equalizerUseCase
            .insertPreset(preset)
            .onSuccess { playerSettingsUseCase.setEqualizerPreset(preset) }
            .onError { sendError() }
    }

    private suspend fun onGainsChanged(gains: List<Float>) = withContext(Dispatchers.IO) {
        val state = uiState.value
        val preset = if(state.selectedPreset.isCustom) {
            state.selectedPreset
        } else {
            state.presetGroups
                .firstOrNull { it.type == gains.size.toType() }
                ?.presets
                ?.firstOrNull { it.isCustomPreset() }
                ?: run {
                    sendError()
                    return@withContext
                }
        }

        val newPreset = preset.copy(gains = gains)

        equalizerUseCase
            .updatePreset(newPreset)
            .onSuccess { playerSettingsUseCase.setEqualizerPreset(newPreset) }
            .onError { sendError() }
    }

    private suspend fun onTypeSelected(type: EqualizerType) {
        val state = uiState.value
        val preset = state.presetGroups
            .firstOrNull { it.type == type }
            ?.presets
            ?.firstOrNull()
            ?: run {
                val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
                SnackbarController.sendEvent(event)
                return
            }
        playerSettingsUseCase.setEqualizerPreset(preset)
    }

    private suspend fun navigateToEqualizer(context: Context) {
        suspendCancellableCoroutine { continuation ->
            playerController.sendCustomCommand(
                context = context,
                command = CustomCommand.GetAudioSessionId,
                listener = {
                    continuation.resume(it)
                }
            )
        }.onSuccess {
            val audioSessionId = it.getInt(CustomCommand.GetAudioSessionId.KEY).defaultZero()

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
        }.onError {
            val event = SnackbarEvent(UiText.StringResource(Strings.setting_no_audio_id))
            SnackbarController.sendEvent(event)
        }
    }

    private fun buildEqualizerUiState(
        presetGroups: List<EqualizerUiState.PresetGroup>,
        soundGroup: EqualizerUiState.SoundGroup,
        selectedPreset: Preset
    ): EqualizerUiState {
        val actualSelectedPreset = if (selectedPreset == Preset.default) {
            presetGroups
                .firstOrNull { it.type == EqualizerType.default }
                ?.presets
                ?.firstOrNull()
                ?: Preset.default
        } else {
            selectedPreset
        }

        return EqualizerUiState(
            presetGroups = presetGroups,
            soundGroup = soundGroup,
            selectedPreset = actualSelectedPreset
        )
    }

    private suspend fun sendError() {
        val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
        SnackbarController.sendEvent(event)
    }
}