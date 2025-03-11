package com.jooheon.toyplayer.features.playlist.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
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
            is PlaylistEvent.OnPlaylistClick -> onPlaylistClick(event.playlist)
            is PlaylistEvent.OnAddPlaylist -> insertPlaylist(event.title)
            is PlaylistEvent.OnDropDownMenuClick -> {}
        }
    }

    private fun onPlaylistClick(playlist: Playlist) {

    }
    private fun insertPlaylist(title: String) {

    }
}