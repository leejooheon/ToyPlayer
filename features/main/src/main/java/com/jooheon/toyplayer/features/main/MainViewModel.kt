package com.jooheon.toyplayer.features.main

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.common.base.BaseViewModel
import com.jooheon.toyplayer.features.main.model.MainScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    fun dispatch(mainScreenEvent: MainScreenEvent) = viewModelScope.launch {
        when(mainScreenEvent) {
            is MainScreenEvent.OnSettingIconClick -> onSettingClicked()
            is MainScreenEvent.OnFavoriteIconCLick -> onFavoriteClicked()
            is MainScreenEvent.OnSearchIconClick -> onSearchClicked()
            is MainScreenEvent.OnPermissionGranted -> onFavoriteClicked()
        }
    }

    private fun onNavigationClicked() {
        Timber.d("onNavigationClicked")
    }

    private fun onFavoriteClicked() {
        Timber.d("onFavoriteClicked")
    }

    private fun onSearchClicked() {
        Timber.d("onSearchClicked")
    }

    private suspend fun onSettingClicked() {
        _navigateTo.send(ScreenNavigation.Setting.Main)
    }
}