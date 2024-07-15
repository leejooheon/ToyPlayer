package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.common.extension.defaultZero
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaylistEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.PlaylistEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
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
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase,
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {
    override val TAG = MusicPlaylistScreenViewModel::class.java.simpleName

    private val _musicPlaylistScreenState = MutableStateFlow(MusicPlaylistScreenState.default)
    val musicPlaylistScreenState = _musicPlaylistScreenState.asStateFlow()

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
            _navigateTo.send(ScreenNavigation.Music.PlayingQueue)
        } else {
            val screen = ScreenNavigation.Music.PlaylistDetail(playlist.id)
            _navigateTo.send(screen)
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
        musicStateHolder.mediaItems.collectLatest {  mediaItems ->
            val oldPlayingQueue = musicPlaylistScreenState.value.playlists.firstOrNull {
                it.id == Playlist.PlayingQueuePlaylistId
            } ?: return@collectLatest

            val songs = mediaItems.map { it.toSong() }
            if(songs == oldPlayingQueue.songs) {
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