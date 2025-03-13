package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.Player
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.usecase.PlaybackSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaybackUseCase(
    private val musicStateHolder: MusicStateHolder,
    private val playlistUseCase: PlaylistUseCase,
    private val playbackSettingsUseCase: PlaybackSettingsUseCase,
) {
    internal fun initialize(player: Player?, scope: CoroutineScope) {
        collectMediaItem(scope)
        collectPlayingQueue(scope)
        collectPlaybackOptions(scope)
    }

    private fun collectPlayingQueue(scope: CoroutineScope) = scope.launch {
        musicStateHolder.mediaItems.collectLatest { mediaItems ->
            playlistUseCase
                .getPlaylist(MediaId.PlayingQueue.hashCode())
                .onSuccess { playlist ->
                    val songs = mediaItems.map { it.toSong() }
                    playlistUseCase.insertPlaylists(playlist.copy(songs = songs))
                }
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