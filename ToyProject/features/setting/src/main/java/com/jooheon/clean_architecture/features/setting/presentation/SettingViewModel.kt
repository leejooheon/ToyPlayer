package com.jooheon.clean_architecture.features.setting.presentation

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import com.jooheon.clean_architecture.domain.usecase.setting.ThemeStateFlow
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.setting.model.SettingScreenEvent
import com.jooheon.clean_architecture.features.setting.model.SettingScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingUseCase: SettingUseCase,
    private val themeStateFlow: ThemeStateFlow
): BaseViewModel() {
    override val TAG = SettingViewModel::class.java.simpleName
    var state by mutableStateOf(SettingScreenState.default)
        private set

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    private val _navigateToSystemEqualizer = Channel<Int>()
    val navigateToSystemEqualizer = _navigateToSystemEqualizer.receiveAsFlow()

    init {
        initState()
    }

    fun dispatch(
        context: Context,
        event: SettingScreenEvent,
        newState: SettingScreenState
    ) = viewModelScope.launch{
        when(event) {
            SettingScreenEvent.GoToBack ->
                _navigateTo.send(ScreenNavigation.Back.route)
            SettingScreenEvent.GoToThemeScreen ->
                _navigateTo.send(ScreenNavigation.Setting.Theme.route)
            SettingScreenEvent.GoToLanguageScreen ->
                _navigateTo.send(ScreenNavigation.Setting.Language.route)
            SettingScreenEvent.ShowSkipDurationDialog -> state = newState
            SettingScreenEvent.GoToEqualizer -> { /** it will be later **/ }

            SettingScreenEvent.SkipDurationChanged -> {
                state = newState
                settingUseCase.setSkipForwardBackward(state.skipDuration)
            }
            SettingScreenEvent.LanguageChanged -> {
                state = newState
                settingUseCase.setLanguage(state.language)

                SettingScreenEvent.changeLanguage(
                    context = context,
                    language = state.language
                )
            }
            SettingScreenEvent.ThemeChanged -> {
                state = newState
                settingUseCase.setTheme(state.theme)
                themeStateFlow.update()
            }
        }
    }
//    fun onEqualizerClick(context: Context, audioSessionId: Int) = viewModelScope.launch {
//        if (audioSessionId == AudioEffect.ERROR_BAD_VALUE) {
//            val content = UiText.StringResource(R.string.no_audio_ID).asString(context)
//            context.showToast(content)
//        } else {
//            _navigateToSystemEqualizer.send(audioSessionId)
//        }
//    }
    private fun initState() = viewModelScope.launch {

        state = state.copy(
            language = settingUseCase.getLanguage(),
            theme = settingUseCase.getTheme(),
            skipDuration = settingUseCase.getSkipForwardBackward()
        )
    }
}