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
import timber.log.Timber

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
            .onEach {
                if(player.playbackState == Player.STATE_IDLE) return@onEach
                Timber.d("mediaItem: ${it.mediaId}")
                defaultSettingsUseCase.setLastPlayedMediaId(it.mediaId)
            }
            .flowOn(Dispatchers.Main)
            .launchIn(this@launch)

        playerSettingsUseCase.flowRepeatMode()
            .onEach { player.repeatMode = it }
            .flowOn(Dispatchers.Main)
            .launchIn(this@launch)

        playerSettingsUseCase.flowShuffleMode()
            .onEach { player.shuffleModeEnabled = it }
            .flowOn(Dispatchers.Main)
            .launchIn(this@launch)

        playerSettingsUseCase.flowVolume()
            .onEach { player.volume = it }
            .flowOn(Dispatchers.Main)
            .launchIn(this@launch)
    }
}