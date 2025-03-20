package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.Player
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.ShuffleMode
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
        scope.launch { restore(player) }

        collectMediaItem(scope)
        collectPlayingQueue(scope)
        collectPlaybackOptions(scope)
    }

    private suspend fun restore(player: Player?) {
        player ?: return
        player.repeatMode = playbackSettingsUseCase.repeatMode().ordinal
        player.shuffleModeEnabled = playbackSettingsUseCase.shuffleMode() == ShuffleMode.SHUFFLE
    }

    private fun collectPlayingQueue(scope: CoroutineScope) = scope.launch {
        musicStateHolder.mediaItems.collectLatest { mediaItems ->
            playlistUseCase
                .getPlayingQueue()
                .onSuccess { playlist ->
                    val songs = mediaItems.map { it.toSong() }
                    playlistUseCase.insertPlaylists(
                        playlist.copy(
                            thumbnailUrl = songs.firstOrNull()?.imageUrl.defaultEmpty(),
                            songs = songs
                        )
                    )
                }
        }
    }

    private fun collectMediaItem(scope: CoroutineScope) = scope.launch {
        musicStateHolder.mediaItem.collectLatest { mediaItem ->
            mediaItem ?: return@collectLatest
            playbackSettingsUseCase.setLastPlayedMediaId(mediaItem.mediaId)
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