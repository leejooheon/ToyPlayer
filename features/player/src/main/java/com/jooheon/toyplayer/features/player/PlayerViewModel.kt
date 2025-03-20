package com.jooheon.toyplayer.features.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.player.model.PlayerEvent
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicStateHolder: MusicStateHolder,
    private val playerController: PlayerController,
    private val playlistUseCase: PlaylistUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
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
            musicStateHolder.mediaItems.collectLatest { mediaItems ->
                val songs = mediaItems.map { it.toSong() }
                val state = uiState.value
                val newPagerModel = state.pagerModel.copy(
                    items = songs,
                    playedName = defaultSettingsUseCase.lastEnqueuedPlaylistName(),
                    playedThumbnailImage = songs.firstOrNull()?.imageUrl.defaultEmpty(),
                )

                val playlists = state.playlists.toMutableList()
                playlists
                    .indexOfFirst { it.id == Playlist.PlayingQueuePlaylistId.first }
                    .takeIf { it != -1 }
                    ?.let {
                        val oldModel = playlists.removeAt(it)
                        val newPlaylist = oldModel.copy(songs = songs)
                        playlists.add(it, newPlaylist)
                    }

                _uiState.emit(
                    state.copy(
                        playlists = playlists,
                        pagerModel = newPagerModel,
                    )
                )
            }
        }
    }

    internal fun dispatch(event: PlayerEvent) = viewModelScope.launch {
        when(event) {
            is PlayerEvent.OnPlaylistClick -> playlistClick(event.playlist, event.index)
            is PlayerEvent.OnPlayAutomatic -> playAutomatically()
            is PlayerEvent.OnPlayPauseClick -> playerController.playPause()
            is PlayerEvent.OnNextClick -> playerController.seekToNext()
            is PlayerEvent.OnPreviousClick -> playerController.seekToPrevious()
            is PlayerEvent.OnSwipe -> playerController.playAtIndex(event.index)

            is PlayerEvent.OnNavigateSettingClick  -> { /** nothing **/ }
            is PlayerEvent.OnNavigatePlaylistClick -> { /** nothing **/ }
            is PlayerEvent.OnNavigateLibraryClick -> { /** nothing **/ }
            is PlayerEvent.OnScreenTouched -> { /** nothing **/ }
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

        _uiState.emit(
            uiState.value.copy(
                playlists = playlists,
                isLoading = false,
            )
        )
    }

    private suspend fun playlistClick(playlist: Playlist, index: Int) {
        when(playlist.id) {
            Playlist.PlayingQueuePlaylistId.first -> playerController.playAtIndex(index)
            else -> {
                defaultSettingsUseCase.setLastEnqueuedPlaylistName(playlist.name)
                playerController.enqueue(
                    songs = playlist.songs,
                    startIndex = index,
                    playWhenReady = true,
                )
            }
        }
    }

    private suspend fun playAutomatically() {
        val lastPlayedMediaId = defaultSettingsUseCase.lastPlayedMediaId()
        val playlistResult = playlistUseCase.getPlayingQueue()
            .takeIf { it is Result.Success && it.data.songs.isNotEmpty() }
            ?: playlistUseCase.getPlaylist(Playlist.RadioPlaylistId.first)

        when(playlistResult) {
            is Result.Success -> {
                val playlist = playlistResult.data
                val mediaItems = playlist.songs.map { it.toMediaItem() }
                val index = mediaItems
                    .indexOfFirst { it.mediaId == lastPlayedMediaId }
                    .takeIf { it != C.INDEX_UNSET }
                    .defaultZero()

                playerController.enqueue(
                    songs = playlist.songs,
                    startPositionMs = 0L,
                    startIndex = index,
                    playWhenReady = true,
                )
            }
            is Result.Error -> {

            }
        }
    }
}