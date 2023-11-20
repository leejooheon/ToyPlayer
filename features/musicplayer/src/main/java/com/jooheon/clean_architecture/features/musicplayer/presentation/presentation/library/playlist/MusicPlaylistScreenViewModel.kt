package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.library.playlist

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.extension.defaultZero
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicPlaylistItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicPlaylistScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase,
    private val playlistUseCase: PlaylistUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
): AbsMusicPlayerViewModel(musicControllerUsecase) {
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
        musicControllerUsecase.musicState.collectLatest {
            val oldPlayingQueue = musicPlaylistScreenState.value.playlists.firstOrNull {
                it.id == Playlist.PlayingQueuePlaylistId
            } ?: return@collectLatest

            if(it.playingQueue == oldPlayingQueue.songs) {
                return@collectLatest
            }

            playlistUseCase.update()
        }
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