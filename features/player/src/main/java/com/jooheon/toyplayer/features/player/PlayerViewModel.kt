package com.jooheon.toyplayer.features.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.controller.SnackbarController
import com.jooheon.toyplayer.features.common.controller.SnackbarEvent
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.player.model.PlayerEvent
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicStateHolder: MusicStateHolder,
    private val playerController: PlayerController,
    private val playlistUseCase: PlaylistUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
): ViewModel() {
    val uiState: StateFlow<PlayerUiState> =
        combine(
            musicStateHolder.musicState,
            playlistUseCase.flowAllPlaylists(),
        ) { musicState, playlists ->
            musicState to playlists
        }.map { (musicState, playlists) ->
            val playingQueue = playlists.firstOrNull { it.id == Playlist.PlayingQueuePlaylistId.first }

            val pagerModel = PlayerUiState.PagerModel(
                items = playingQueue?.songs.defaultEmpty(),
                playedName = defaultSettingsUseCase.lastEnqueuedPlaylistName(),
                playedThumbnailImage = playingQueue?.songs?.firstOrNull()?.imageUrl.defaultEmpty(),
            )

            PlayerUiState(
                pagerModel = pagerModel,
                musicState = musicState,
                playlists = playlists.filter { it.songs.isNotEmpty() },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerUiState.default,
        )


    val autoPlaybackProperty = AtomicBoolean(false)

    init {
        collectStates()
    }

    private fun collectStates() = viewModelScope.launch {
        launch {
            musicStateHolder.playbackError.collectLatest {
                val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
                SnackbarController.sendEvent(event)
            }
        }
    }

    internal fun dispatch(event: PlayerEvent) = viewModelScope.launch {
        when(event) {
            is PlayerEvent.OnPlaylistClick -> onPlaylistClick(event.playlist, event.index)
            is PlayerEvent.OnPlayAutomatic -> onPlayAutomatically(event.context)
            is PlayerEvent.OnPlayPauseClick -> playerController.playPause()
            is PlayerEvent.OnNextClick -> playerController.seekToNext()
            is PlayerEvent.OnPreviousClick -> playerController.seekToPrevious()
            is PlayerEvent.OnSwipe -> playerController.playAtIndex(event.index)
            is PlayerEvent.OnFavoriteClick -> onFavoriteClick(event.song)

            is PlayerEvent.OnNavigateSettingClick  -> { /** nothing **/ }
            is PlayerEvent.OnNavigatePlaylistClick -> { /** nothing **/ }
            is PlayerEvent.OnNavigateLibraryClick -> { /** nothing **/ }
            is PlayerEvent.OnScreenTouched -> { /** nothing **/ }
        }
    }

    private suspend fun onPlaylistClick(playlist: Playlist, index: Int) {
        when(playlist.id) {
            Playlist.PlayingQueuePlaylistId.first -> playerController.playAtIndex(index)
            else -> {
                defaultSettingsUseCase.setLastEnqueuedPlaylistName(playlist.name)
                playerController.enqueue(
                    mediaId = MediaId.Playlist(playlist.id.toString()),
                    songs = playlist.songs,
                    startIndex = index,
                    playWhenReady = true,
                )
            }
        }
    }

    private suspend fun onPlayAutomatically(context: Context) {
        playerController.sendCustomCommand(
            context = context,
            command = CustomCommand.PrepareRecentQueue(true),
            listener = {
                Timber.d("PrepareRecentQueue: $it")
            }
        )
    }

    private suspend fun onFavoriteClick(song: Song) {

    }
}