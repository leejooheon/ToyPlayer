package com.jooheon.toyplayer.features.musicplayer.presentation.common.music

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.common.base.BaseViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class AbsMusicPlayerViewModel (
    private val musicStateHolder: MusicStateHolder,
    private val playbackEventUseCase: PlaybackEventUseCase
): BaseViewModel() {
    override val TAG = AbsMusicPlayerViewModel::class.java.simpleName

    private val _musicPlayerState = MutableStateFlow(MusicPlayerState.default)
    val musicPlayerState = _musicPlayerState.asStateFlow()

    protected val _navigateToPlayingQueueScreen = Channel<Playlist>()
    val navigateToPlayingQueueScreen = _navigateToPlayingQueueScreen.receiveAsFlow()

    init {
        collectMusicState()
    }

    fun dispatch(event: MusicPlayerEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlayerEvent.OnPlayingQueueClick -> onPlayingQueueClick()
            else -> playbackEventUseCase.dispatch(event)
        }
    }

    private suspend fun onPlayingQueueClick() {
        _navigateToPlayingQueueScreen.send(
            Playlist.playingQueuePlaylist.copy(
                songs = musicPlayerState.value.playingQueue
            )
        )
    }

    private fun collectMusicState() = viewModelScope.launch {
        musicStateHolder.musicState.collectLatest { musicState ->
            _musicPlayerState.update {
                it.copy(
                    musicState = musicState
                )
            }
        }
    }
}