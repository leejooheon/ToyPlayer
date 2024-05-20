package com.jooheon.toyplayer.features.musicplayer.presentation.common.music

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.RepeatMode
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.common.base.BaseViewModel
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

open class AbsMusicPlayerViewModel (
    private val musicStateHolder: MusicStateHolder,
    private val playerController: PlayerController,
    private val playbackEventUseCase: PlaybackEventUseCase
): BaseViewModel() {
    override val TAG = AbsMusicPlayerViewModel::class.java.simpleName

    private val _musicPlayerState = MutableStateFlow(MusicPlayerState.default)
    val musicPlayerState = _musicPlayerState.asStateFlow()

    init {
        collectMusicState()
    }

    fun dispatch(event: MusicPlayerEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlayerEvent.OnPlayingQueueClick -> _navigateTo.send(ScreenNavigation.Music.PlayingQueue)
            else -> playbackEventUseCase.dispatch(playerController, event)
        }
    }

    private fun collectMusicState() = viewModelScope.launch {
        launch {
            musicStateHolder.musicState.collectLatest { musicState ->
                _musicPlayerState.update {
                    it.copy(
                        musicState = musicState
                    )
                }
            }
        }
        launch {
            musicStateHolder.mediaItems.collectLatest { mediaItems ->
                _musicPlayerState.update {
                    it.copy(
                        playingQueue = mediaItems.map { it.toSong() }
                    )
                }
            }
        }
        launch {
            musicStateHolder.repeatMode.collectLatest { repeatMode ->
                _musicPlayerState.update {
                    it.copy(
                        repeatMode = RepeatMode.getByValue(repeatMode)
                    )
                }
            }
        }
        launch {
            musicStateHolder.shuffleMode.collectLatest { shuffleMode ->
                _musicPlayerState.update {
                    it.copy(
                        shuffleMode = ShuffleMode.getByValue(shuffleMode)
                    )
                }
            }
        }
    }

    protected suspend fun getMusicList(context: Context, mediaId: MediaId): List<Song> = withContext(Dispatchers.IO) {
        val songs = suspendCancellableCoroutine { continuation ->
            playerController.getMusicListFuture(
                context = context,
                mediaId = mediaId,
                listener = { mediaItems ->
                    val songs = mediaItems.map { it.toSong() }
                    continuation.resume(songs)
                }
            )
        }

        return@withContext songs
    }
}