package com.jooheon.toyplayer.features.setting.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.usecase.setting.SettingUseCase
import com.jooheon.toyplayer.features.common.base.BaseViewModel
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.setting.model.SettingScreenEvent
import com.jooheon.toyplayer.features.setting.model.SettingScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingUseCase: SettingUseCase
): BaseViewModel() {
    override val TAG = SettingViewModel::class.java.simpleName

    val _sharedState = MutableStateFlow(SettingScreenState.default)
    val sharedState = _sharedState.asStateFlow()

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    private val _navigateToSystemEqualizer = Channel<Int>()
    val navigateToSystemEqualizer = _navigateToSystemEqualizer.receiveAsFlow()

    init {
        initState()
        Log.d(TAG, "initialize ${TAG}")
    }

    fun dispatch(
        context: Context,
        event: SettingScreenEvent,
    ) = viewModelScope.launch{
        when(event) {
            is SettingScreenEvent.OnEqualizerScreenClick -> { /** Nothing **/ }
            is SettingScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back.route)
            is SettingScreenEvent.OnThemeScreenClick -> _navigateTo.send(ScreenNavigation.Setting.Theme.route)
            is SettingScreenEvent.OnLanguageScreenClick -> _navigateTo.send(ScreenNavigation.Setting.Language.route)
            is SettingScreenEvent.OnSkipDurationScreenClick -> {
                _sharedState.update {
                    it.copy(showSkipDurationDialog = event.isShow)
                }
            }
            is SettingScreenEvent.OnSkipDurationChanged -> {
                settingUseCase.setSkipForwardBackward(event.data)
                _sharedState.update { it.copy(skipDuration = event.data) }
            }
            is SettingScreenEvent.OnLanguageChanged -> {
                settingUseCase.setLanguage(event.language)
                _sharedState.update { it.copy(language = event.language) }
                SettingScreenEvent.changeLanguage(
                    context = context,
                    language = sharedState.value.language
                )
            }
            is SettingScreenEvent.OnThemeChanged -> {
                settingUseCase.setTheme(event.theme)
                _sharedState.update { it.copy(theme = event.theme) }
            }
        }
    }
    private fun initState() = viewModelScope.launch {
        _sharedState.value = sharedState.value.copy(
            language = settingUseCase.getLanguage(),
            theme = settingUseCase.getTheme(),
            skipDuration = settingUseCase.getSkipForwardBackward()
        )
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}