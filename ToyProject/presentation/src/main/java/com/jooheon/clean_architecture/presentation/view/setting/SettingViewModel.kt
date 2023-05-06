package com.jooheon.clean_architecture.presentation.view.setting

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Forward5
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.music.SkipForwardBackward
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import com.jooheon.clean_architecture.presentation.common.showToast
import com.jooheon.clean_architecture.presentation.utils.UiText
import com.jooheon.clean_architecture.presentation.view.navigation.ScreenNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingUseCase: SettingUseCase
):BaseViewModel() {
    override val TAG = SettingViewModel::class.java.simpleName

    private val _navigateToSettingScreen = Channel<SettingData>()
    val navigateToSettingDetailScreen = _navigateToSettingScreen.receiveAsFlow()

    private val _navigateToSystemEqualizer = Channel<Int>()
    val navigateToSystemEqualizer = _navigateToSystemEqualizer.receiveAsFlow()

    private val _localizedState = MutableStateFlow(Entity.SupportLaunguages.AUTO)
    val localizedState = _localizedState.asStateFlow()

    private val _themeState = MutableStateFlow(Entity.SupportThemes.DYNAMIC_LIGHT)
    val themeState = _themeState.asStateFlow()

    private val _skipState = MutableStateFlow(SkipForwardBackward.FIVE_SECOND)
    val skipState = _skipState.asStateFlow()

    init {
        updateLanguageState()
        updateThemeState()
        updateSkipState()
    }
    fun onSettingItemClick(data: SettingData) = viewModelScope.launch(Dispatchers.Main) {
        _navigateToSettingScreen.send(data)
    }

    fun onLanguageItemClick(language: Entity.SupportLaunguages) = viewModelScope.launch(Dispatchers.Main) {
        settingUseCase.setLanguage(language)
        updateLanguageState()
    }

    fun onThemeItemClick(theme: Entity.SupportThemes) = viewModelScope.launch(Dispatchers.Main) {
        settingUseCase.setTheme(theme)
        updateThemeState()
    }
    fun onSkipItemClick(skip: SkipForwardBackward) = viewModelScope.launch(Dispatchers.Main) {
        settingUseCase.setSkipForwardBackward(skip)
        updateSkipState()
    }

    fun onEqualizerClick(context: Context, audioSessionId: Int) = viewModelScope.launch {
        if (audioSessionId == AudioEffect.ERROR_BAD_VALUE) {
            val content = UiText.StringResource(R.string.no_audio_ID).asString(context)
            context.showToast(content)
        } else {
            _navigateToSystemEqualizer.send(audioSessionId)
        }
    }
    fun parseRoute(action: SettingAction) = when(action) {
        SettingAction.LAUGUAGE -> ScreenNavigation.Setting.Launguage.route
        SettingAction.THEME -> ScreenNavigation.Setting.Theme.route
        else -> null
    }
    fun showableTheme(theme: Entity.SupportThemes): Boolean {
        val supportDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S // 12 이상일때
        if(theme.code.contains("dynamic") ) {
            return supportDynamicColor
        } else {
            return true
        }
    }
    private fun updateLanguageState() = viewModelScope.launch {
        val language = settingUseCase.getLanguage()
        _localizedState.update { language }
    }
    private fun updateThemeState() = viewModelScope.launch {
        val theme = settingUseCase.getTheme()
        _themeState.update { theme }
    }
    private fun updateSkipState() = viewModelScope.launch {
        val skip = settingUseCase.getSkipForwardBackward()
        _skipState.update { skip }
    }
    fun getSettingList(context: Context): List<SettingData> {
        return listOf(
            SettingData(
                action = SettingAction.LAUGUAGE,
                title = UiText.StringResource(R.string.setting_language).asString(context),
                value = UiText.StringResource(R.string.setting_language_desc).asString(context),
                showValue = false,
                iconImageVector = Icons.Outlined.Language
            ),
            SettingData(
                action = SettingAction.THEME,
                title = UiText.StringResource(R.string.setting_theme).asString(context),
                value = parseThemeText(context),
                showValue = true,
                iconImageVector = Icons.Outlined.WbSunny
            ),
            SettingData(
                action = SettingAction.SKIP_DURATION,
                title = UiText.StringResource(R.string.setting_skip_duration).asString(context),
                value =  UiText.StringResource(
                    resId = R.string.n_second,
                    args = arrayOf(skipState.value.toInteger())
                ).asString(context),
                showValue = true,
                iconImageVector = Icons.Outlined.Forward5
            ),
            SettingData(
                action = SettingAction.EQUALIZER,
                title = UiText.StringResource(R.string.setting_equalizer).asString(context),
                value = "",
                showValue = false,
                iconImageVector = Icons.Outlined.Equalizer
            ),
        )
    }
    private fun parseThemeText(context: Context) =  when(themeState.value) {
        Entity.SupportThemes.AUTO -> UiText.StringResource(R.string.setting_follow_system).asString(context)
        Entity.SupportThemes.DARK -> UiText.StringResource(R.string.setting_theme_dark).asString(context)
        Entity.SupportThemes.LIGHT -> UiText.StringResource(R.string.setting_theme_light).asString(context)
        Entity.SupportThemes.DYNAMIC_DARK -> UiText.StringResource(R.string.setting_theme_dynamic_dark).asString(context)
        Entity.SupportThemes.DYNAMIC_LIGHT -> UiText.StringResource(R.string.setting_theme_dynamic_light).asString(context)
    }
}

data class SettingData(
    val action: SettingAction,
    val title: String,
    val value: String,
    val showValue: Boolean,
    val iconImageVector: ImageVector,
)