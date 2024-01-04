package com.jooheon.toyplayer.domain.usecase.setting

import com.jooheon.toyplayer.domain.entity.Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeStateFlow @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val settingUseCase: SettingUseCase
) {
    init {
        update()
    }

    private val _themeState = MutableStateFlow(Entity.SupportThemes.AUTO)
    val themeState = _themeState.asStateFlow()

    fun update() {
        applicationScope.launch(Dispatchers.Main) {
            val theme = settingUseCase.getTheme()
            _themeState.emit(theme)
        }
    }
}