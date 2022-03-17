package com.jooheon.clean_architecture.presentation.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected abstract val TAG: String

    private val _loadingState = MutableSharedFlow<Boolean>(0)
    val loadingState = _loadingState

    private val _alertDialogState = MutableSharedFlow<String>(0)
    val alertDialogState = _alertDialogState

    protected fun <T: Any> handleResponse(response: Resource<T>?) {
        if(response is Resource.Loading) {
            handleLoadingState(true)
        } else {
            handleLoadingState(false)
        }

        if(response is Resource.Failure) {
            val content = "code: " + response.code.toString() + "\n" + "message: " + response.message
            handleAlertDialogState(content)
        }
    }

    protected fun handleLoadingState(state: Boolean) {
        viewModelScope.launch {
            _loadingState.emit(state)
        }
    }

    protected fun handleAlertDialogState(content: String) {
        viewModelScope.launch {
            _alertDialogState.emit(content)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}