package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
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
    musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {
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
        }
    }
}