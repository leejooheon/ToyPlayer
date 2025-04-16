package com.jooheon.toyplayer.features.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.usecase.EqualizerInitUseCase
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.splash.model.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val equalizerInitUseCase: EqualizerInitUseCase,
): ViewModel() {
    private val _state = Channel<SplashState>()
    val state = _state.receiveAsFlow()

    internal fun initialize(context: Context) {
        viewModelScope.launch {
            equalizerInitUseCase.invoke()
            prepareMediaItems(context)
        }
    }

    private suspend fun prepareMediaItems(context: Context) {
        suspendCancellableCoroutine { continuation ->
            playerController.getMusicListFuture(
                context = context,
                mediaId = MediaId.Root,
                listener = { continuation.resume(it) }
            )
        }
        _state.send(SplashState.Done)
    }
}