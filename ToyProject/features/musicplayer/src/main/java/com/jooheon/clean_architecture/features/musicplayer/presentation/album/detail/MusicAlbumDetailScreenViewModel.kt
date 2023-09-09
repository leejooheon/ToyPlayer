package com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.model.MusicAlbumDetailScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.model.MusicAlbumDetailScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
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
    private val musicControllerUsecase: MusicControllerUsecase,
    private val playlistUseCase: PlaylistUseCase,
    private val playingQueueUseCase: PlayingQueueUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
): AbsMusicPlayerViewModel(musicControllerUsecase) {
    override val TAG = MusicAlbumDetailScreenViewModel::class.java.simpleName

    private val _musicAlbumDetailScreenState = MutableStateFlow(MusicAlbumDetailScreenState.default)
    val musicAlbumDetailScreenState = _musicAlbumDetailScreenState.asStateFlow()

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    private var requirePlay = false

    init {
        collectPlaylistState()
        collectPlayingQueue()
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
                musicControllerUsecase.onPlay(song = event.song)
            }
            is MusicAlbumDetailScreenEvent.OnActionPlayAll -> {
                Timber.d("OnActionPlayAll: ${event.album.name}, ${event.shuffle}")
                requirePlay = true
                musicControllerUsecase.onOpenQueue(
                    songs = event.album.songs,
                    addToPlayingQueue = false
                )
            }
        }
    }

    fun onMusicMediaItemEvent(event: MusicMediaItemEvent) {
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

    private fun collectPlayingQueue() = viewModelScope.launch {
        playingQueueUseCase.playingQueue().collectLatest {
            if(!requirePlay) return@collectLatest
            if(it.isEmpty()) return@collectLatest

            musicControllerUsecase.onPlay(it.first())
        }
    }
}