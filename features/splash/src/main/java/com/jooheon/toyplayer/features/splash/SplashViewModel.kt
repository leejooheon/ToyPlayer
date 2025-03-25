package com.jooheon.toyplayer.features.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.splash.model.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val playerController: PlayerController
): ViewModel() {
    private val _state = Channel<SplashState>()
    val state = _state.receiveAsFlow()

    internal fun initialize(context: Context) {
        viewModelScope.launch {
            prepareMediaItems(context)
        }
    }

    private fun prepareMediaItems(context: Context) {
        playerController.getMusicListFuture(
            context = context,
            mediaId = MediaId.Root,
            listener = {
                viewModelScope.launch {
                    _state.send(SplashState.Done)
                }
            }
        )
    }
}