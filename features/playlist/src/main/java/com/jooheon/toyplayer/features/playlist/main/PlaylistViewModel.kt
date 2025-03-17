package com.jooheon.toyplayer.features.playlist.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.compose.SnackbarController
import com.jooheon.toyplayer.features.common.compose.SnackbarEvent
import com.jooheon.toyplayer.features.common.compose.components.dropdown.DropDownMenuEvent
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
        val result = playlistUseCase.getAllPlaylist()

        when(result) {
            is Result.Success -> {
                _uiState.emit(
                    PlaylistUiState(playlists = result.data)
                )
            }
            is Result.Error -> {
                 // TODO: handle error
            }
        }
    }

    internal fun dispatch(event: PlaylistEvent) = viewModelScope.launch {
        when(event) {
            is PlaylistEvent.OnPlaylistClick -> { /** nothing **/ }
            is PlaylistEvent.OnDropDownMenuClick -> deletePlaylist(event.playlist)
            is PlaylistEvent.OnAddPlaylist -> insertPlaylist(event.title)
        }
    }
    internal fun dispatch(event: DropDownMenuEvent) = viewModelScope.launch {
        when(event) {
            is DropDownMenuEvent.OnDelete -> {}
            is DropDownMenuEvent.OnSaveAsFile -> {}
            is DropDownMenuEvent.OnChangeName -> { /** nothing **/ }
        }
    }

    private suspend fun deletePlaylist(playlist: Playlist) {
        playlistUseCase.deletePlaylists(playlist)
        loadData()
    }

    private suspend fun insertPlaylist(name: String) {
        if(playlistUseCase.checkValidName(name)) {
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
                val event = SnackbarEvent(UiText.StringResource(Strings.some_error))
                SnackbarController.sendEvent(event)
            }
        } else {
            val event = SnackbarEvent(UiText.StringResource(Strings.error_playlist, name))
            SnackbarController.sendEvent(event)
        }
    }
}