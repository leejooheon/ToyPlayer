package com.jooheon.toyplayer.features.playlist.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.PlaybackSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailEvent
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val playbackSettingsUseCase: PlaybackSettingsUseCase,
    private val playerController: PlayerController,
): ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistDetailUiState.default)
    val uiState = _uiState.asStateFlow()

    internal fun dispatch(
        event: PlaylistDetailEvent
    ) = viewModelScope.launch {
        when(event) {
            is PlaylistDetailEvent.OnPlayAllClick -> onPlayAll(event.shuffle)
            is PlaylistDetailEvent.OnDelete -> onDelete(event.song)
        }
    }

    internal fun loadData(playlistId: Int) = viewModelScope.launch {
        val result = playlistUseCase.getPlaylist(playlistId)

        when(result) {
            is Result.Success -> _uiState.emit(PlaylistDetailUiState(playlist = result.data))
            is Result.Error -> { /** TODO: handle error **/ }
        }
    }

    private suspend fun onDelete(song: Song) {
        val playlist = uiState.value.playlist
        playlistUseCase.updatePlaylists(
            playlist.copy(
                songs = playlist.songs.filter { it.key() != song.key() }
            )
        )

        loadData(playlist.id)
    }

    private suspend fun onPlayAll(shuffle: Boolean) {
        val playlist = uiState.value.playlist

        val startIndex = if(shuffle) (playlist.songs.indices).random() else 0
        playbackSettingsUseCase.setLastEnqueuedPlaylistName(playlist.name)
        playerController.enqueue(
            songs = playlist.songs,
            startIndex = startIndex,
            playWhenReady = true,
        )
        playerController.shuffle(shuffle)
    }
}