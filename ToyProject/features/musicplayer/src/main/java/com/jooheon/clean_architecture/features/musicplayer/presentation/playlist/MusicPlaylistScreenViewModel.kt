package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultZero
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.playlist.PlaylistUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicPlaylistItemEvent
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
            is MusicPlaylistScreenEvent.Refresh -> loadData()
            is MusicPlaylistScreenEvent.OnPlaylistClick -> _navigateToDetailScreen.send(event.playlist)
            is MusicPlaylistScreenEvent.OnAddPlaylist -> insertPlaylist(event)
        }
    }

    fun dispatch(event: MusicPlaylistItemEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlaylistItemEvent.OnDelete -> deletePlaylist(event.playlist)
            is MusicPlaylistItemEvent.OnChangeName -> updatePlaylist(event.playlist)
            is MusicPlaylistItemEvent.OnSaveAsFile -> playlistSaveAsFile(event.playlist)
            else -> { /** Nothing **/ }
        }
    }

    private fun insertPlaylist(event: MusicPlaylistScreenEvent.OnAddPlaylist) = viewModelScope.launch {
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

    private fun deletePlaylist(playlist: Playlist) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            playlistUseCase.deletePlaylists(playlist)
        }
        loadData()
    }

    private fun updatePlaylist(playlist: Playlist) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            playlistUseCase.updatePlaylists(playlist)
        }
        loadData()
    }

    private fun playlistSaveAsFile(playlist: Playlist) = viewModelScope.launch {
        /** TODO **/
        withContext(Dispatchers.IO) {
            delay(300)
        }
    }
}