package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.Artist
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.model.MusicArtistDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.model.MusicArtistDetailScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
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
    private val playlistUseCase: PlaylistUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
    playerController: PlayerController,
    musicStateHolder: MusicStateHolder,
): AbsMusicPlayerViewModel(playerController, musicStateHolder) {
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
