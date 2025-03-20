package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.Player
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaybackUseCase(
    private val scope: CoroutineScope,
    private val musicStateHolder: MusicStateHolder,
    private val playlistUseCase: PlaylistUseCase,
    private val playerSettingsUseCase: PlayerSettingsUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase
) {
    internal fun collectStates(player: Player?) = scope.launch {
        player ?: return@launch

        launch {
            musicStateHolder.mediaItem.collectLatest { mediaItem ->
                mediaItem ?: return@collectLatest
                defaultSettingsUseCase.setLastPlayedMediaId(mediaItem.mediaId)
            }
        }

        launch {
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

        launch {
            playerSettingsUseCase.flowRepeatMode().collectLatest {
                player.repeatMode = it
            }
        }

        launch {
            playerSettingsUseCase.flowShuffleMode().collectLatest {
                player.shuffleModeEnabled = it
            }
        }
        launch {
            playerSettingsUseCase.flowVolume().collectLatest {
                player.volume = it
            }
        }
    }
}