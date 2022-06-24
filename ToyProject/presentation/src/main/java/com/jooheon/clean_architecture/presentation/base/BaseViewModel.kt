package com.jooheon.clean_architecture.presentation.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.presentation.common.AlertDialogResource
import com.jooheon.clean_architecture.presentation.utils.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected abstract val TAG: String

    private val _loadingState = Channel<Boolean>(0)
    val loadingState = _loadingState.receiveAsFlow()

    private val _alertDialogState = Channel<AlertDialogResource?>(0)
    val alertDialogState = _alertDialogState.receiveAsFlow()

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
            _alertDialogState.send(resource)
        }
    }

    fun dismissAlertDialog() {
        viewModelScope.launch {
            _alertDialogState.send(null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}