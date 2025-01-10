package com.jooheon.toyplayer.features.common.base

import androidx.lifecycle.ViewModel
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel : ViewModel() {
    protected abstract val TAG: String

    protected val _navigateTo = Channel<ScreenNavigation>()
    val navigateTo = _navigateTo.receiveAsFlow()

    protected val _loadingState = Channel<Boolean>()
    val loadingState = _loadingState.receiveAsFlow()
}