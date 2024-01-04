package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenState
import com.jooheon.toyplayer.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.MusicStateHolder
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
    private val musicControllerUsecase: MusicControllerUseCase,
    musicStateHolder: MusicStateHolder,
): AbsMusicPlayerViewModel(musicControllerUsecase, musicStateHolder) {
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
            is MusicPlaylistDetailScreenEvent.OnPlayAllClick -> {
                val playlist = musicPlaylistDetailScreenState.value.playlist
                val songs = if(event.shuffle) playlist.songs.shuffled()
                            else playlist.songs

                musicControllerUsecase.enqueue(
                    songs = songs,
                    addNext = false,
                    playWhenReady = true,
                )
            }
            is MusicPlaylistDetailScreenEvent.OnSongClick ->  {
                musicControllerUsecase.enqueue(
                    song = event.song,
                    playWhenReady = true
                )
            }
        }
    }
}