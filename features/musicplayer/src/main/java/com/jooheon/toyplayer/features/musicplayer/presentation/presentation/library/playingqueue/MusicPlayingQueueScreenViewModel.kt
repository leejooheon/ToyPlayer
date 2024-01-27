package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.model.MusicPlayingQueueScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.model.MusicPlayingQueueScreenState
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
class MusicPlayingQueueScreenViewModel @Inject constructor(
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
    playerController: PlayerController,
    private val musicStateHolder: MusicStateHolder,
): AbsMusicPlayerViewModel(playerController, musicStateHolder) {
    override val TAG = MusicPlayingQueueScreenViewModel::class.java.simpleName

    private val _musicPlayingQueueScreenState = MutableStateFlow(MusicPlayingQueueScreenState.default)
    val musicPlayingQueueScreenState = _musicPlayingQueueScreenState.asStateFlow()

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    init {
        collectPlayingQueue()
    }

    fun dispatch(event: MusicPlayingQueueScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlayingQueueScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back.route)
        }
    }

    fun onMusicMediaItemEvent(event: MusicMediaItemEvent) = viewModelScope.launch {
        musicMediaItemEventUseCase.dispatch(event)
    }
    private fun collectPlayingQueue() = viewModelScope.launch {
        musicStateHolder.playingQueue.collectLatest { playingQueue ->
            _musicPlayingQueueScreenState.update {
                it.copy(
                    playlist = Playlist.playingQueuePlaylist.copy(
                        songs = playingQueue
                    )
                )
            }
        }
    }
}