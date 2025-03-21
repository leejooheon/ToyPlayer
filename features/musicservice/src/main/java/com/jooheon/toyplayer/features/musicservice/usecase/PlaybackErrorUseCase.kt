package com.jooheon.toyplayer.features.musicservice.usecase

import android.content.Context
import androidx.media3.common.PlaybackException
import com.jooheon.toyplayer.core.network.NetworkConnectivityObserver
import com.jooheon.toyplayer.domain.model.common.FailureStatus
import com.jooheon.toyplayer.features.common.extension.showToast
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaybackErrorUseCase(
    private val context: Context,
    private val musicStateHolder: MusicStateHolder,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
) {
    private var recentPlaybackError: FailureStatus? = null

    private val _autoPlayChannel = Channel<Unit>()
    internal val autoPlayChannel = _autoPlayChannel.receiveAsFlow()

    private val _seekToDefaultChannel = Channel<Unit>()
    internal val seekToDefaultChannel = _seekToDefaultChannel.receiveAsFlow()

    internal fun initialize(scope: CoroutineScope) {
        collectPlaybackError(scope)
        collectNetworkConnectivity(scope)
    }

    private fun collectPlaybackError(scope: CoroutineScope) = scope.launch {
        musicStateHolder.playbackError.collectLatest { exception ->
            recentPlaybackError = when (exception.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> FailureStatus.NO_INTERNET
                PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> {
                    _seekToDefaultChannel.send(Unit)
                    FailureStatus.OTHER
                }
                else -> FailureStatus.OTHER
            }
//            val message = exception.localizedMessage ?: "some error occurred"
//            context.showToast(message)
        }
    }

    private fun collectNetworkConnectivity(scope: CoroutineScope) = scope.launch {
        networkConnectivityObserver.observe().collectLatest {
            Timber.d("networkConnectivityObserver: $it")

            if(recentPlaybackError == FailureStatus.NO_INTERNET &&
                it == NetworkConnectivityObserver.Status.Available) {
                Timber.w("networkConnectivityObserver: auto play!!!")

                recentPlaybackError = null

                if(musicStateHolder.isPlaying.value) return@collectLatest

                _autoPlayChannel.send(Unit)
            }
        }
    }
}