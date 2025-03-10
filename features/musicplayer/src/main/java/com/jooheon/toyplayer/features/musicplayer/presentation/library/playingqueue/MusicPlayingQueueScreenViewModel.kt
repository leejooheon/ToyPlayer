package com.jooheon.toyplayer.features.musicplayer.presentation.library.playingqueue

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.SongItemEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.library.playingqueue.model.MusicPlayingQueueScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.library.playingqueue.model.MusicPlayingQueueScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayingQueueScreenViewModel @Inject constructor(
    private val songItemEventUseCase: SongItemEventUseCase,
    private val musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase,
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {
    override val TAG = MusicPlayingQueueScreenViewModel::class.java.simpleName

    private val _musicPlayingQueueScreenState = MutableStateFlow(MusicPlayingQueueScreenState.default)
    val musicPlayingQueueScreenState = _musicPlayingQueueScreenState.asStateFlow()

    fun dispatch(event: MusicPlayingQueueScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlayingQueueScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back)
        }
    }

    fun onSongItemEvent(event: SongItemEvent) = viewModelScope.launch {
        songItemEventUseCase.dispatch(event)
    }
}