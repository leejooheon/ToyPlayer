package com.jooheon.toyplayer.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val defaultSettingsUseCase: DefaultSettingsUseCase
): ViewModel() {
    val isDarkTheme = defaultSettingsUseCase.flowIsDarkTheme()

    fun updateIsDarkTheme(isDarkTheme: Boolean) =
        viewModelScope.launch {
            defaultSettingsUseCase.updateIsDarkTheme(isDarkTheme)
        }

}