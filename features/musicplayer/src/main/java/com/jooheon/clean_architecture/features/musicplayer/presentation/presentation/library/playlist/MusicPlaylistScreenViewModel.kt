package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.library.playlist

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultZero
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicPlaylistItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
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
    musicControllerUsecase: MusicControllerUseCase,
    musicStateHolder: MusicStateHolder,
    private val playlistUseCase: PlaylistUseCase,
    private val playingQueueUseCase: PlayingQueueUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
): AbsMusicPlayerViewModel(musicControllerUsecase, musicStateHolder) {
    override val TAG = MusicPlaylistScreenViewModel::class.java.simpleName

    private val _musicPlaylistScreenState = MutableStateFlow(MusicPlaylistScreenState.default)
    val musicPlaylistScreenState = _musicPlaylistScreenState.asStateFlow()

    private val _navigateToDetailScreen = Channel<Playlist>()
    val navigateToDetailScreen = _navigateToDetailScreen.receiveAsFlow()

    init {
        collectPlaylist()
        collectPlayingQueue()
    }

    fun dispatch(event: MusicPlaylistScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlaylistScreenEvent.Refresh -> {} //playlistUseCase.update()
            is MusicPlaylistScreenEvent.OnPlaylistClick -> onPlaylistClick(event.playlist)
            is MusicPlaylistScreenEvent.OnAddPlaylist -> insertPlaylist(event)
        }
    }

    fun onMusicMediaItemEvent(event: MusicPlaylistItemEvent) = viewModelScope.launch {
        musicMediaItemEventUseCase.dispatch(event)
    }

    private suspend fun onPlaylistClick(playlist: Playlist) {
        if(playlist.id == Playlist.PlayingQueuePlaylistId) {
            _navigateToPlayingQueueScreen.send(playlist)
        } else {
            _navigateToDetailScreen.send(playlist)
        }
    }

    private suspend fun insertPlaylist(event: MusicPlaylistScreenEvent.OnAddPlaylist) {
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

    private fun collectPlayingQueue() = viewModelScope.launch {
        playingQueueUseCase.playingQueue().onEach {
            if(it !is Resource.Success) return@onEach

            val playingQueue = it.value
            val oldPlayingQueue = musicPlaylistScreenState.value.playlists.firstOrNull {
                it.id == Playlist.PlayingQueuePlaylistId
            } ?: return@onEach

            if(playingQueue == oldPlayingQueue.songs) {
                return@onEach
            }

            playlistUseCase.update()
        }.launchIn(this)
    }

    private fun collectPlaylist() = viewModelScope.launch {
        playlistUseCase.allPlaylist().collectLatest { playlists ->
            _musicPlaylistScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }
}