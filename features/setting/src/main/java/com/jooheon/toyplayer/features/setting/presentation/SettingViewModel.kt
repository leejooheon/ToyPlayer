package com.jooheon.toyplayer.features.setting.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.features.common.base.BaseViewModel
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.usecase.SettingsUseCase
import com.jooheon.toyplayer.features.setting.model.SettingScreenEvent
import com.jooheon.toyplayer.features.setting.model.SettingScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
): BaseViewModel() {
    override val TAG = SettingViewModel::class.java.simpleName

    private val _sharedState = MutableStateFlow(SettingScreenState.default)
    val sharedState = _sharedState.asStateFlow()

    init {
        initState()
    }

    fun dispatch(
        context: Context,
        event: SettingScreenEvent,
    ) = viewModelScope.launch {
        when(event) {
            is SettingScreenEvent.OnEqualizerScreenClick -> { /** Nothing **/ }
            is SettingScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back)
            is SettingScreenEvent.OnThemeScreenClick -> _navigateTo.send(ScreenNavigation.Setting.Theme)
            is SettingScreenEvent.OnLanguageScreenClick -> _navigateTo.send(ScreenNavigation.Setting.Language)
            is SettingScreenEvent.OnSkipDurationScreenClick -> {
                _sharedState.update {
                    it.copy(showSkipDurationDialog = event.isShow)
                }
            }
            is SettingScreenEvent.OnSkipDurationChanged -> {
                _sharedState.update { it.copy(skipDuration = event.data) }
            }
            is SettingScreenEvent.OnThemeChanged -> {
                settingsUseCase.setTheme(event.theme)
                _sharedState.update { it.copy(theme = event.theme) }
            }
        }
    }
    private fun initState() = viewModelScope.launch {
//        _sharedState.value = sharedState.value.copy(
//            language = settingsUseCase.getLanguage(),
//            theme = settingsUseCase.getTheme(),
//            skipDuration = settingsUseCase.getSkipForwardBackward()
//        )
    }
}