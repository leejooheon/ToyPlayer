package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.common.extension.defaultZero
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaylistEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.PlaylistEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
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
    private val playlistUseCase: PlaylistUseCase,
    private val playlistEventUseCase: PlaylistEventUseCase,
    private val musicStateHolder: MusicStateHolder,
    playbackEventUseCase: PlaybackEventUseCase,
): AbsMusicPlayerViewModel(musicStateHolder, playbackEventUseCase) {
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

    fun onPlaylistEvent(event: PlaylistEvent) = viewModelScope.launch {
        playlistEventUseCase.dispatch(event)
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
        musicStateHolder.playingQueue.collectLatest {  playingQueue ->
            val oldPlayingQueue = musicPlaylistScreenState.value.playlists.firstOrNull {
                it.id == Playlist.PlayingQueuePlaylistId
            } ?: return@collectLatest

            if(playingQueue == oldPlayingQueue.songs) {
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