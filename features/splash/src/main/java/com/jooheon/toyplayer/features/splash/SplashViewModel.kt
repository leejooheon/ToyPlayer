package com.jooheon.toyplayer.features.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.splash.model.SplashEvent
import com.jooheon.toyplayer.features.splash.model.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val playerController: PlayerController
): ViewModel() {
    private val _state = Channel<SplashState>()
    val state = _state.receiveAsFlow()

    internal fun initialize() {
        viewModelScope.launch {
            _state.send(SplashState.Default)
        }
    }

    internal fun dispatch(event: SplashEvent) = viewModelScope.launch {
        when(event) {
            is SplashEvent.CheckNetwork -> {
                _state.send(SplashState.NetworkAvailable(true))
            }
            is SplashEvent.ServiceCheck -> {
                _state.send(SplashState.ServiceAvailable(true))
            }
            is SplashEvent.Update -> {
                prepareMediaItems(event.context)
                _state.send(SplashState.Done)
            }
        }
    }

    private suspend fun prepareMediaItems(
        context: Context,
    ) = withContext(Dispatchers.IO) {
        val mediaItems = suspendCancellableCoroutine { continuation ->
            playerController.getMusicListFuture(
                context = context,
                mediaId = MediaId.Root,
                listener = { mediaItems ->
                    continuation.resume(mediaItems)
                }
            )
        }
        Timber.d("prepareMediaItems: ${mediaItems.size}")
    }
}