package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultZero
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.music.MusicPlayListUsecase
import com.jooheon.clean_architecture.domain.usecase.playlist.PlaylistUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events.PlaylistDropDownMenuEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicPlaylistScreenViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
): BaseViewModel() {
    override val TAG = MusicPlaylistScreenViewModel::class.java.simpleName

    private val _musicPlaylistScreenState = MutableStateFlow(MusicPlaylistScreenState.default)
    val musicPlaylistScreenState = _musicPlaylistScreenState.asStateFlow()

    private val _navigateToDetailScreen = Channel<Playlist>()
    val navigateToDetailScreen = _navigateToDetailScreen.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        playlistUseCase.getAllPlaylist()
            .onEach { resource ->
                handleResponse(resource)

                if(resource is Resource.Success) {
                    _musicPlaylistScreenState.update {
                        it.copy(playlists = resource.value)
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun dispatch(event: MusicPlaylistScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlaylistScreenEvent.onPlaylistClick -> _navigateToDetailScreen.send(event.playlist)
            is MusicPlaylistScreenEvent.onAddPlaylist -> insertPlaylist(event)
        }
    }

    fun dispatch(event: PlaylistDropDownMenuEvent) = viewModelScope.launch {
        when(event) {
            is PlaylistDropDownMenuEvent.OnPlaylistDelete -> deletePlaylist(event)
            is PlaylistDropDownMenuEvent.OnPlaylistNameChange -> updatePlaylist(event)
            is PlaylistDropDownMenuEvent.OnPlaylistSaveAsFile -> playlistSaveAsFile(event)
            else -> { /** Nothing **/ }
        }
    }

    private fun insertPlaylist(event: MusicPlaylistScreenEvent.onAddPlaylist) = viewModelScope.launch {
        val title = event.title
        val nextId = musicPlaylistScreenState.value.playlists.maxByOrNull { it.id }?.id.defaultZero() + 1

        val playlist = Playlist(
            id = nextId,
            name = title,
            thumbnailUrl = "",
            songs = emptyList()
        )

        withContext(Dispatchers.IO) {
            playlistUseCase.insertPlaylists(playlist)
        }
        loadData()
    }

    private fun deletePlaylist(event: PlaylistDropDownMenuEvent.OnPlaylistDelete) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            playlistUseCase.deletePlaylists(event.playlist)
        }
        loadData()
    }

    private fun updatePlaylist(event: PlaylistDropDownMenuEvent.OnPlaylistNameChange) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            playlistUseCase.updatePlaylists(event.playlist)
        }
        loadData()
    }

    private fun playlistSaveAsFile(event: PlaylistDropDownMenuEvent.OnPlaylistSaveAsFile) = viewModelScope.launch {
        /** TODO **/
        withContext(Dispatchers.IO) {
            delay(300)
        }
    }
}