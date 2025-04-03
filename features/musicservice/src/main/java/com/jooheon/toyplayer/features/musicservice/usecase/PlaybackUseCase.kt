package com.jooheon.toyplayer.features.musicservice.usecase

import android.media.audiofx.BassBoost
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.findExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(UnstableApi::class)
class PlaybackUseCase(
    private val scope: CoroutineScope,
    private val musicStateHolder: MusicStateHolder,
    private val playlistUseCase: PlaylistUseCase,
    private val playerSettingsUseCase: PlayerSettingsUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase
) {
    private var bassBoost: BassBoost? = null
    private var bassBoostSessionId: Int? = null

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

        playerSettingsUseCase.flowBassBoost()
            .onEach { value ->
                val sessionId = player.findExoPlayer()?.audioSessionId.default(C.AUDIO_SESSION_ID_UNSET)
                if (sessionId != C.AUDIO_SESSION_ID_UNSET) {
                    if (bassBoost == null || bassBoostSessionId != sessionId) {
                        bassBoost?.release()
                        bassBoostSessionId = sessionId
                        bassBoost = BassBoost(0, sessionId)
                            .apply { enabled = true }
                    }

                    bassBoost?.setStrength((value * 10).coerceIn(0, 1000).toShort())
                }
            }
            .flowOn(Dispatchers.Main)
            .launchIn(this@launch)
    }
}