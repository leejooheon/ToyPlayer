package com.jooheon.toyplayer.features.playlist.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
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
): ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistDetailUiState.default)
    val uiState = _uiState.asStateFlow()

    internal fun dispatch(event: PlaylistDetailEvent) {

    }

    internal fun loadData(playlistId: Int) = viewModelScope.launch {
        val result = playlistUseCase.getPlaylist(playlistId)

        when(result) {
            is Result.Success -> {
                _uiState.emit(
                    PlaylistDetailUiState(
                        playlist = result.data)
                )
            }
            is Result.Error -> { /** TODO: handle error **/ }
        }
    }
}