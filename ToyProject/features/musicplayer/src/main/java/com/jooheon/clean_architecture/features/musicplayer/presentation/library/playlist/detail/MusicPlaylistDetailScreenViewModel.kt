package com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlaylistDetailScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase
): AbsMusicPlayerViewModel(musicControllerUsecase) {
    override val TAG = MusicPlaylistDetailScreenViewModel::class.java.simpleName

    private val _musicPlaylistDetailScreenState = MutableStateFlow(MusicPlaylistDetailScreenState.default)
    val musicPlaylistDetailScreenState = _musicPlaylistDetailScreenState.asStateFlow()

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    fun init(playlist: Playlist) = viewModelScope.launch {
        _musicPlaylistDetailScreenState.update {
            it.copy(
                playlist = playlist
            )
        }
    }

    fun dispatch(event: MusicPlaylistDetailScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlaylistDetailScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back.route)
            is MusicPlaylistDetailScreenEvent.OnSongClick ->  {
                musicControllerUsecase.onOpenQueue(
                    songs = listOf(event.song),
                    addToPlayingQueue = true,
                    autoPlay = true,
                )
            }
        }
    }
}