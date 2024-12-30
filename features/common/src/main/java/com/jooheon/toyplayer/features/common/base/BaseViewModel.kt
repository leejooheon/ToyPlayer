package com.jooheon.toyplayer.features.common.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.features.common.compose.data.AlertDialogResource
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.core.strings.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected abstract val TAG: String

    protected val _navigateTo = Channel<ScreenNavigation>()
    val navigateTo = _navigateTo.receiveAsFlow()

    private val _loadingState = Channel<Boolean>()
    val loadingState = _loadingState.receiveAsFlow()

    private val _alertDialogChannel = Channel<AlertDialogResource?>(0)
    val alertDialogFlow = _alertDialogChannel.receiveAsFlow()

    protected fun <T: Any> handleResponse(response: Resource<T>?) {
        if(response is Resource.Loading) {
            handleLoadingState(true)
        } else {
            handleLoadingState(false)
        }

        if(response is Resource.Failure) {
            val content = UiText.DynamicString("code: " + response.code.toString() + "\n" + "message: " + response.message)
            handleAlertDialogState(content)
        }
    }

    protected fun handleLoadingState(state: Boolean) {
        viewModelScope.launch {
            _loadingState.send(state)
        }
    }

    protected fun handleAlertDialogState(content: UiText) {
        viewModelScope.launch {
            val resource = AlertDialogResource(content)
            _alertDialogChannel.send(resource)
        }
    }

    protected open fun onOkButtonClicked() {
        Log.d(TAG, "onOkButtonClicked")
    }

    open fun dismissAlertDialog() {
        viewModelScope.launch {
            _alertDialogChannel.send(null)
            onOkButtonClicked()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}