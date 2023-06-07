package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultZero
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.playlist.PlaylistUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicPlaylistItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
): BaseViewModel() {
    override val TAG = MusicPlaylistScreenViewModel::class.java.simpleName

    private val _musicPlaylistScreenState = MutableStateFlow(MusicPlaylistScreenState.default)
    val musicPlaylistScreenState = _musicPlaylistScreenState.asStateFlow()

    private val _navigateToDetailScreen = Channel<Playlist>()
    val navigateToDetailScreen = _navigateToDetailScreen.receiveAsFlow()

    init {
        collectPlaylist()
    }
    fun dispatch(event: MusicPlaylistScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlaylistScreenEvent.Refresh -> {} //playlistUseCase.update()
            is MusicPlaylistScreenEvent.OnPlaylistClick -> _navigateToDetailScreen.send(event.playlist)
            is MusicPlaylistScreenEvent.OnAddPlaylist -> insertPlaylist(event)
        }
    }

    fun onMusicMediaItemEvent(event: MusicPlaylistItemEvent) = viewModelScope.launch {
        musicMediaItemEventUseCase.dispatch(event)
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
    }

    private fun collectPlaylist() = viewModelScope.launch {
        playlistUseCase.playlistState.collectLatest { playlists ->
            _musicPlaylistScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }
}