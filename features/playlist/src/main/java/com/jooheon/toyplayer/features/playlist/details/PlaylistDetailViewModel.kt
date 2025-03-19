package com.jooheon.toyplayer.features.playlist.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailEvent
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val playerController: PlayerController,
): ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistDetailUiState.default)
    val uiState = _uiState.asStateFlow()

    internal fun dispatch(
        context: Context,
        event: PlaylistDetailEvent
    ) = viewModelScope.launch {
        when(event) {
            is PlaylistDetailEvent.OnPlayAllClick -> onPlay(context, event.shuffle)
        }
    }

    internal fun loadData(playlistId: Int) = viewModelScope.launch {
        val result = playlistUseCase.getPlaylist(playlistId)

        when(result) {
            is Result.Success -> _uiState.emit(PlaylistDetailUiState(playlist = result.data))
            is Result.Error -> { /** TODO: handle error **/ }
        }
    }

    private fun onPlay(context: Context, shuffle: Boolean) {
        val playlist = uiState.value.playlist

        val startIndex = if(shuffle) {
            playerController.shuffle(true)
            (playlist.songs.indices).random()
        } else 0

        playerController.sendCustomCommand(
            context = context,
            command = CustomCommand.Play(
                playlistId = playlist.id,
                startIndex = startIndex,
                playWhenReady = true
            ),
            listener = {
                Timber.d("sendCustomCommandResult: OnContentClick $it")
            }
        )
    }
}