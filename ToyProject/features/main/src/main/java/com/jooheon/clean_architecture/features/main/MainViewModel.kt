package com.jooheon.clean_architecture.features.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.usecase.subway.SubwayUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val subwayUseCase: SubwayUseCase,
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

    fun onBackPressed() {
        viewModelScope.launch {
            _isDoubleBackPressed.value = false
            delay(2000)
            _isDoubleBackPressed.value = true
        }
    }
}