package com.jooheon.toyplayer.features.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.features.splash.model.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SplashViewModel @Inject constructor(): ViewModel() {
    private val _state = Channel<SplashState>()
    val state = _state.receiveAsFlow()

    internal fun initialize(context: Context) {
        viewModelScope.launch {
            prepareMediaItems(context)
        }
    }

    private fun prepareMediaItems(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(100.milliseconds) // do something
            }
            _state.send(SplashState.Done)
        }
    }
}