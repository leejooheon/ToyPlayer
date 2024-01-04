package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.common.PlayerController
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail.model.MusicAlbumDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail.model.MusicAlbumDetailScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.MusicStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MusicAlbumDetailScreenViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
    playerController: PlayerController,
    musicControllerUseCase: MusicControllerUseCase,
    musicStateHolder: MusicStateHolder,
): AbsMusicPlayerViewModel(playerController, musicControllerUseCase, musicStateHolder) {
    override val TAG = MusicAlbumDetailScreenViewModel::class.java.simpleName

    private val _musicAlbumDetailScreenState = MutableStateFlow(MusicAlbumDetailScreenState.default)
    val musicAlbumDetailScreenState = _musicAlbumDetailScreenState.asStateFlow()

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    init {
        collectPlaylistState()
    }
    fun initialize(album: Album) = viewModelScope.launch {
        _musicAlbumDetailScreenState.update {
            it.copy(album = album)
        }
    }

    fun dispatch(event: MusicAlbumDetailScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicAlbumDetailScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back.route)
        }
    }

    fun onMusicMediaItemEvent(event: MusicMediaItemEvent) = viewModelScope.launch {
        musicMediaItemEventUseCase.dispatch(event)
    }

    private fun collectPlaylistState() = viewModelScope.launch {
        playlistUseCase.allPlaylist().collectLatest { playlists ->
            _musicAlbumDetailScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }
}