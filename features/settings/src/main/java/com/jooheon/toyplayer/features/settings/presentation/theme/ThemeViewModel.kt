package com.jooheon.toyplayer.features.settings.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
): ViewModel() {

    internal fun dispatch(isDarkTheme: Boolean) = viewModelScope.launch {
        defaultSettingsUseCase.updateIsDarkTheme(isDarkTheme)
    }
}