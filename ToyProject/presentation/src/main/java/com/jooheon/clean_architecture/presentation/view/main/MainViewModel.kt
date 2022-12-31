package com.jooheon.clean_architecture.presentation.view.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicUseCase: MusicUseCase,
): BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    private val _lastSearchedOwner = mutableStateOf("")
    val lastSearchedOwner = _lastSearchedOwner

    private var _isDoubleBackPressed = mutableStateOf(true) // FIXME: 2번연속했을떄 안되넴
    val isDoubleBackPressed = _isDoubleBackPressed

    private val _floatingActionClicked = Channel<Unit>()
    val floatingActionClicked = _floatingActionClicked.receiveAsFlow()

    private val _floatingButtonState = mutableStateOf(false)
    val floatingButtonState = _floatingButtonState

    private val _navigateToSettingScreen = Channel<Unit>()
    val navigateToSettingScreen = _navigateToSettingScreen.receiveAsFlow()

    fun onNavigationClicked() {
        Log.d(TAG, "onNavigationClicked")
    }

    fun onFavoriteClicked() {
        Log.d(TAG, "onFavoriteClicked")
    }

    fun onSearchClicked() {
        Log.d(TAG, "onSearchClicked")
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

    fun onBackPressed() {
        viewModelScope.launch {
            _isDoubleBackPressed.value = false
            delay(2000)
            _isDoubleBackPressed.value = true
        }
    }
}