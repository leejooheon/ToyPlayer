package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.Player
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaybackUseCase(
    private val musicStateHolder: MusicStateHolder,
    private val playlistUseCase: com.jooheon.toyplayer.domain.usecase.PlaylistUseCase,
    private val playbackSettingsUseCase: com.jooheon.toyplayer.domain.usecase.PlaybackSettingsUseCase,
) {
    internal fun initialize(player: Player?, scope: CoroutineScope) {
        collectMediaItem(scope)
        collectPlayingQueue(scope)
        collectPlaybackOptions(scope)
    }

    private fun collectPlayingQueue(scope: CoroutineScope) = scope.launch {
        musicStateHolder.mediaItems.collectLatest {
            val songs = it.map { it.toSong() }
            playlistUseCase.setPlayingQueue(songs)
        }
    }

    private fun collectMediaItem(scope: CoroutineScope) = scope.launch {
        musicStateHolder.mediaItem.collectLatest { mediaItem ->
            mediaItem ?: return@collectLatest
            val key = mediaItem.mediaId.toLongOrNull() ?: return@collectLatest
            playbackSettingsUseCase.setRecentMediaItemKey(key)
        }
    }

    private fun collectPlaybackOptions(scope: CoroutineScope) = scope.launch {
        launch {
            musicStateHolder.shuffleMode.collectLatest {
                playbackSettingsUseCase.shuffleModeChanged(it)
            }
        }

        launch {
            musicStateHolder.repeatMode.collectLatest {
                playbackSettingsUseCase.repeatModeChanged(it)
            }
        }
    }
}