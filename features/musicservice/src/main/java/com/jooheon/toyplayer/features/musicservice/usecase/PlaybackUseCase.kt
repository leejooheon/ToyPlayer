package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.Player
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

        musicStateHolder.mediaItem
            .onEach { defaultSettingsUseCase.setLastPlayedMediaId(it.mediaId) }
            .flowOn(Dispatchers.IO)
            .launchIn(this@launch)

        musicStateHolder.mediaItems
            .onEach { mediaItems ->
                playlistUseCase.insert(
                    id = Playlist.PlayingQueue.id,
                    songs = mediaItems.map { it.toSong() },
                    reset = true,
                )
            }
            .flowOn(Dispatchers.IO)
            .launchIn(this@launch)

        playerSettingsUseCase.flowRepeatMode()
            .flowOn(Dispatchers.Main)
            .onEach { player.repeatMode = it }
            .launchIn(this@launch)

        playerSettingsUseCase.flowShuffleMode()
            .flowOn(Dispatchers.Main)
            .onEach { player.shuffleModeEnabled = it }
            .launchIn(this@launch)

        playerSettingsUseCase.flowVolume()
            .flowOn(Dispatchers.Main)
            .onEach { player.volume = it }
            .launchIn(this@launch)
    }
}