package com.jooheon.toyplayer.features.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.PlaybackSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.player.model.PlayerEvent
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicStateHolder: MusicStateHolder,
    private val playerController: PlayerController,
    private val playlistUseCase: PlaylistUseCase,
    private val playbackSettingsUseCase: PlaybackSettingsUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(PlayerUiState.default)
    val uiState = _uiState.asStateFlow()

    val autoPlaybackProperty = AtomicBoolean(false)

    init {
        collectStates()
    }

    private fun collectStates() = viewModelScope.launch {
        launch {
            musicStateHolder.musicState.collectLatest {
                val state = uiState.value
                _uiState.emit(
                    state.copy(musicState = it)
                )
            }
        }
        launch {
            combine(
                playbackSettingsUseCase.flowPlaylistId(),
                musicStateHolder.mediaItems
            ) { playlistId, mediaItems ->
                playlistId to mediaItems
            }.collectLatest { (playlistId, mediaItems) ->
                val result = playlistUseCase.getPlaylist(playlistId)
                val playlist = when(result) {
                    is Result.Success -> result.data
                    is Result.Error -> Playlist.default
                }

                val state = uiState.value
                val newPagerModel = state.pagerModel.copy(
                    items = mediaItems.map { it.toSong() },
                    currentPlaylist = playlist
                )

                val contentModels = state.contentModels.toMutableList()

                contentModels
                    .indexOfFirst { it.playlist.id == Playlist.PlayingQueuePlaylistId.first }
                    .takeIf { it != -1 }
                    ?.let {
                        val oldModel = contentModels.removeAt(it)
                        val newPlaylist = oldModel.playlist.copy(songs = playlist.songs)
                        val newModel = oldModel.copy(playlist = newPlaylist)
                        contentModels.add(it, newModel)
                    }

                _uiState.emit(
                    state.copy(
                        contentModels = contentModels,
                        pagerModel = newPagerModel,
                    )
                )
            }
        }
    }

    internal fun dispatch(context: Context, event: PlayerEvent) = viewModelScope.launch {
        when(event) {
            is PlayerEvent.OnContentClick -> {
                if(event.playlistId == playbackSettingsUseCase.playlistId()) {
                    playerController.playAtIndex(event.index)
                } else {
                    playerController.sendCustomCommand(
                        context = context,
                        command = CustomCommand.Play(
                            playlistId = event.playlistId,
                            startIndex = event.index,
                            playWhenReady = true
                        ),
                        listener = {
                            Timber.d("sendCustomCommandResult: OnContentClick $it")
                        }
                    )
                }
            }
            is PlayerEvent.OnPlayAutomatic -> {
                playerController.sendCustomCommand(
                    context = context,
                    command = CustomCommand.PlayAutomatic,
                    listener = {
                        Timber.d("sendCustomCommandResult: OnPlayAutomatic $it")
                    }
                )
            }
            is PlayerEvent.OnPlayPauseClick -> playerController.playPause()
            is PlayerEvent.OnNextClick -> playerController.seekToNext()
            is PlayerEvent.OnPreviousClick -> playerController.seekToPrevious()
            is PlayerEvent.OnSwipe -> playerController.playAtIndex(event.index)
            is PlayerEvent.OnSettingClick  -> { /** nothing **/ }
            is PlayerEvent.OnScreenTouched -> { /** nothing **/ }
            is PlayerEvent.OnPlaylistClick -> { /** nothing **/ }
            is PlayerEvent.OnLibraryClick -> { /** nothing **/ }
        }
    }

    internal fun loadData() = viewModelScope.launch {
        _uiState.emit(
            uiState.value.copy(isLoading = true)
        )

        val result = playlistUseCase.getAllPlaylist()
        val playlists = when(result) {
            is Result.Success -> result.data.filter { it.songs.isNotEmpty() }
            is Result.Error -> emptyList()
        }

        val models = playlists.map {
            PlayerUiState.ContentModel(
                playlist = it,
                requirePermission = false
            )
        }

        _uiState.emit(
            uiState.value.copy(
                contentModels = models,
                isLoading = false,
            )
        )
    }
}