package com.jooheon.toyplayer.features.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.player.model.PlayerEvent
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicStateHolder: MusicStateHolder,
    private val playerController: PlayerController,
    private val playlistUseCase: PlaylistUseCase,
): ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState.default)
    val uiState = _uiState.asStateFlow()

    val autoPlaybackProperty = AtomicBoolean(false)
    private val playlist = MutableStateFlow(Playlist.default) // FIXME

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
                playlist, // TODO: 다른거로 바꿔야함
                musicStateHolder.mediaItems
            ) { playlist, mediaItems ->
                playlist to mediaItems
            }.collectLatest { (playlist, mediaItems) ->
                val state = uiState.value
                val model = PlayerUiState.PagerModel(
                    items = mediaItems.map { it.toSong() },
                    currentPlaylist = playlist
                )
                _uiState.emit(
                    state.copy(pagerModel = model)
                )
            }
        }
    }

    internal fun dispatch(event: PlayerEvent) = viewModelScope.launch {
        when(event) {
            is PlayerEvent.OnContentClick -> {
                val state = uiState.value
                val playlist = state.contentModels
                    .firstOrNull { it.playlist.id == event.playlistId }
                    ?.let { it.playlist }
                    ?: return@launch

                playerController.enqueue(
                    songs = playlist.songs,
                    startIndex = playlist.songs.indexOfFirst { it.key() == event.song.key() },
                    addNext = false,
                    playWhenReady = true
                )

                this@PlayerViewModel.playlist.emit(playlist)
            }
            is PlayerEvent.OnPlayAutomatic -> {
                playerController.enqueue(
                    songs = event.playlist.songs,
                    startIndex = 0,
                    addNext = false,
                    playWhenReady = true
                )

                this@PlayerViewModel.playlist.emit(event.playlist)
            }
            is PlayerEvent.OnPlayPauseClick -> playerController.playPause()
            is PlayerEvent.OnSwipe -> playerController.play(event.song)
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
            is Result.Success -> result.data.reversed().filter { it.songs.isNotEmpty() }
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