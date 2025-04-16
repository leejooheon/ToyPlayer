package com.jooheon.toyplayer.features.playlist.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.controller.SnackbarController
import com.jooheon.toyplayer.features.common.controller.SnackbarEvent
import com.jooheon.toyplayer.features.common.menu.SongMenuHandler
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistEvent
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val songMenuHandler: SongMenuHandler,
): ViewModel() {
    val uiState: StateFlow<PlaylistUiState> =
        playlistUseCase.flowAllPlaylists()
            .map { PlaylistUiState(playlists = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlaylistUiState.default,
            )

    internal fun dispatch(event: PlaylistEvent) = viewModelScope.launch {
        when(event) {
            is PlaylistEvent.OnDeletePlaylist -> deletePlaylist(event.id)
            is PlaylistEvent.OnAddPlaylist -> {
                if(event.id == Playlist.default.id) {
                    songMenuHandler.make(Playlist.default.copy(name = event.name), emptyList())
                } else {
                    updatePlaylistName(event.id, event.name)
                }
            }

            is PlaylistEvent.OnNavigatePlaylist -> { /** nothing **/ }
        }
    }

    private suspend fun deletePlaylist(id: Int) {
        playlistUseCase.getPlaylist(id)
            .onSuccess { playlistUseCase.deletePlaylist(it) }
            .onError {
                val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
                SnackbarController.sendEvent(event)
            }
    }

    private suspend fun updatePlaylistName(
        id: Int,
        name: String
    ) {
        playlistUseCase.updateName(id, name)
            .onSuccess {
                val event = SnackbarEvent(UiText.StringResource(Strings.apply))
                SnackbarController.sendEvent(event)
            }
            .onError {
                val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
                SnackbarController.sendEvent(event)
            }
    }
}