package com.jooheon.clean_architecture.features.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.usecase.subway.SubwayUseCase
import com.jooheon.clean_architecture.toyproject.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.main.model.MainScreenEvent
import com.jooheon.clean_architecture.features.main.model.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val subwayUseCase: SubwayUseCase,
): BaseViewModel() {
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
        Log.d(TAG, "onNavigationClicked")
    }

    fun onFavoriteClicked() = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "onFavoriteClicked")
        val stationName = "판교"
        subwayUseCase
            .getStationInfo(stationName)
            .onEach { resource ->
                if(resource is Resource.Success) {
                    Log.d(TAG, "Success: ${resource.value}")
                } else if(resource is Resource.Failure) {
                    Log.d(TAG, "failure: ${resource.message}")
                } else if(resource is Resource.Loading){
                    Log.d(TAG, "Loading")
                }
                
                handleResponse(resource)
            }.launchIn(viewModelScope)
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

    private fun onBackPressed() = viewModelScope.launch {
        _mainScreenState.update { it.copy(doubleBackPressedState = false) }
        delay(2000)
        _mainScreenState.update { it.copy(doubleBackPressedState = true) }
    }
}