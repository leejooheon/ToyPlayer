package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.PlaybackException
import com.jooheon.toyplayer.domain.model.common.errors.NetworkError
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackError
import com.jooheon.toyplayer.domain.model.common.errors.RootError
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PlaybackErrorUseCase(
    private val musicStateHolder: MusicStateHolder,
) {
    private var recentPlaybackError: RootError? = null

    private val _autoPlayChannel = Channel<Unit>()
    internal val autoPlayChannel = _autoPlayChannel.receiveAsFlow()

    private val _seekToDefaultChannel = Channel<Unit>()
    internal val seekToDefaultChannel = _seekToDefaultChannel.receiveAsFlow()

    internal fun initialize(scope: CoroutineScope) {
        collectPlaybackError(scope)
    }

    private fun collectPlaybackError(scope: CoroutineScope) = scope.launch {
        musicStateHolder.playbackError.collectLatest { exception ->
            recentPlaybackError = when (exception.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> NetworkError.NoInternet
                PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> {
                    PlaybackError.Behind
                }
                else -> PlaybackError.UnKnown(exception.errorCode)
            }
//            val message = exception.localizedMessage ?: "some error occurred"
//            context.showToast(message)
        }
    }
}