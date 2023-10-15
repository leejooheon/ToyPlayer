package com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.model.MusicArtistDetailScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.model.MusicArtistDetailScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicArtistDetailScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase,
    private val playlistUseCase: PlaylistUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
): AbsMusicPlayerViewModel(musicControllerUsecase) {
    override val TAG = MusicArtistDetailScreenViewModel::class.java.simpleName

    private val _musicArtistDetailScreenState = MutableStateFlow(MusicArtistDetailScreenState.default)
    val musicArtistDetailScreenState = _musicArtistDetailScreenState.asStateFlow()

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    init {
        collectPlaylistState()
    }

    fun initialize(artist: Artist) = viewModelScope.launch {
        _musicArtistDetailScreenState.update {
            it.copy(artist = artist)
        }
    }
    fun dispatch(event: MusicArtistDetailScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicArtistDetailScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back.route)
            is MusicArtistDetailScreenEvent.OnSongClick -> {
                musicControllerUsecase.onPlayAtPlayingQueue(
                    songs = listOf(event.song),
                    addToPlayingQueue = true,
                    playWhenReady = true,
                )
            }
            is MusicArtistDetailScreenEvent.OnAlbumClick -> {
                val route = ScreenNavigation.Music.AlbumDetail.createRoute(event.album)
                _navigateTo.send(route)
            }
        }
    }
    fun onMusicMediaItemEvent(event: MusicMediaItemEvent) = viewModelScope.launch {
        musicMediaItemEventUseCase.dispatch(event)
    }

    private fun collectPlaylistState() = viewModelScope.launch{
        playlistUseCase.allPlaylist().collectLatest { playlists ->
            _musicArtistDetailScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }
}
