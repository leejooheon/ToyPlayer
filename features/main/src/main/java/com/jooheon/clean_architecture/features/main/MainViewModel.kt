package com.jooheon.clean_architecture.features.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.toyproject.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.main.model.MainScreenEvent
import com.jooheon.clean_architecture.features.main.model.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    private val _mainScreenState = MutableStateFlow(MainScreenState.default)
    val mainScreenState = _mainScreenState.asStateFlow()

    private var _isDoubleBackPressed = MutableStateFlow(true)
    val isDoubleBackPressed = _isDoubleBackPressed.asStateFlow()

    private val _floatingActionClicked = Channel<Unit>()
    val floatingActionClicked = _floatingActionClicked.receiveAsFlow()

    private val _floatingButtonState = mutableStateOf(false)
    val floatingButtonState = _floatingButtonState

    private val _navigateToSettingScreen = Channel<Unit>()
    val navigateToSettingScreen = _navigateToSettingScreen.receiveAsFlow()

    fun dispatch(mainScreenEvent: MainScreenEvent) = viewModelScope.launch {
        when(mainScreenEvent) {
            is MainScreenEvent.OnSettingIconClick -> _navigateToSettingScreen.send(Unit)
            is MainScreenEvent.OnFavoriteIconCLick -> { /** TODO **/ }
            is MainScreenEvent.OnSearchIconClick -> { /** TODO **/ }
        }
    }

    fun onNavigationClicked() {
        Timber.d("onNavigationClicked")
    }

    fun onFavoriteClicked() = viewModelScope.launch(Dispatchers.IO) {
        Timber.d("onFavoriteClicked")
    }

    fun onSearchClicked() {
        Timber.d("onSearchClicked")
    }

    fun onSettingClicked() = viewModelScope.launch(Dispatchers.Main) {
        _navigateToSettingScreen.send(Unit)
    }

    fun onFloatingButtonClicked() {
        _floatingButtonState.value = !(_floatingButtonState.value)
        viewModelScope.launch {
            _floatingActionClicked.send(Unit)
        }
    }

    private fun onBackPressed() = viewModelScope.launch {
        _mainScreenState.update { it.copy(doubleBackPressedState = false) }
        delay(2000)
        _mainScreenState.update { it.copy(doubleBackPressedState = true) }
    }
}