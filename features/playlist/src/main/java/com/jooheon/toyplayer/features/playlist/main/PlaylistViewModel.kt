package com.jooheon.toyplayer.features.playlist.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.commonui.controller.SnackbarController
import com.jooheon.toyplayer.features.commonui.controller.SnackbarEvent
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistEvent
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistUiState.default)
    val uiState = _uiState.asStateFlow()

    internal fun loadData() = viewModelScope.launch {
        playlistUseCase.getAllPlaylist()
            .onSuccess {
                _uiState.emit(PlaylistUiState(playlists = it))
            }
            .onError {
                val event = SnackbarEvent(
                    UiText.StringResource(Strings.some_error)
                )
                SnackbarController.sendEvent(event)
            }
    }

    internal fun dispatch(event: PlaylistEvent) = viewModelScope.launch {
        when(event) {
            is PlaylistEvent.OnPlaylistClick -> { /** nothing **/ }
            is PlaylistEvent.OnDeletePlaylist -> deletePlaylist(event.id)
            is PlaylistEvent.OnAddPlaylist -> {
                if(event.id == Playlist.default.id) insertPlaylist(event.name)
                else replacePlaylist(event.id, event.name)
            }
        }
    }

    private suspend fun deletePlaylist(id: Int) {
        playlistUseCase.getPlaylist(id)
            .onSuccess {
                playlistUseCase.deletePlaylists(it)
                loadData()
            }
            .onError {
                val event = SnackbarEvent(
                    UiText.StringResource(Strings.some_error)
                )
                SnackbarController.sendEvent(event)
            }
    }

    private suspend fun replacePlaylist(
        id: Int,
        name: String
    ) {
        playlistUseCase.getPlaylist(id)
            .onSuccess {
                playlistUseCase.updatePlaylists(it.copy(name = name))
                loadData()
            }
            .onError {
                val event = SnackbarEvent(
                    UiText.StringResource(Strings.some_error)
                )
                SnackbarController.sendEvent(event)
            }
    }

    private suspend fun insertPlaylist(name: String) {
        if(!playlistUseCase.checkValidName(name)) {
            val event = SnackbarEvent(
                UiText.StringResource(
                    Strings.error_playlist,
                    name
                )
            )
            SnackbarController.sendEvent(event)
            return
        }

        playlistUseCase.nextPlaylistIdOrNull()?.let {
            val playlist = Playlist(
                id = it,
                name = name,
                thumbnailUrl = "",
                songs = emptyList()
            )
            playlistUseCase.insertPlaylists(playlist)
            loadData()
        } ?: run {
            val event = SnackbarEvent(
                UiText.StringResource(Strings.some_error)
            )
            SnackbarController.sendEvent(event)
        }
    }
}