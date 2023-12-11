package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.album.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.album.detail.model.MusicAlbumDetailScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.album.detail.model.MusicAlbumDetailScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicStateHolder
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
    private val musicControllerUsecase: MusicControllerUseCase,
    private val playlistUseCase: PlaylistUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
    musicStateHolder: MusicStateHolder,
): AbsMusicPlayerViewModel(musicControllerUsecase, musicStateHolder) {
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
            is MusicAlbumDetailScreenEvent.OnSongClick -> {
                musicControllerUsecase.enqueue(
                    song = event.song,
                    playWhenReady = true
                )
            }
            is MusicAlbumDetailScreenEvent.OnActionPlayAll -> {
                Timber.d("OnActionPlayAll: ${event.album.name}, ${event.shuffle}")

                val songs = if(event.shuffle) event.album.songs.shuffled()
                            else event.album.songs

                musicControllerUsecase.enqueue(
                    songs = songs,
                    addNext = false,
                    playWhenReady = true
                )
            }
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