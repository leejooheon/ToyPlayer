package com.jooheon.clean_architecture.features.musicplayer.presentation.common.music

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import com.jooheon.clean_architecture.toyproject.features.common.base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class AbsMusicPlayerViewModel (
    private val musicControllerUsecase: MusicControllerUsecase,
): BaseViewModel() {
    override val TAG = AbsMusicPlayerViewModel::class.java.simpleName

    private val _musicPlayerState = MutableStateFlow(MusicPlayerState.default)
    val musicPlayerState = _musicPlayerState.asStateFlow()

    protected val _navigateToPlayingQueueScreen = Channel<Playlist>()
    val navigateToPlayingQueueScreen = _navigateToPlayingQueueScreen.receiveAsFlow()

    init {
        collectMusicState()
        collectDuration()
    }

    fun dispatch(event: MusicPlayerEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlayerEvent.OnPlayingQueueClick -> onPlayingQueueClick()
            is MusicPlayerEvent.OnPlayPauseClick -> onPlayPauseButtonClicked(event.song)
            is MusicPlayerEvent.OnPlayClick -> onPlay(event.song)
            is MusicPlayerEvent.OnSnapTo -> snapTo(event.duration)
            is MusicPlayerEvent.OnNextClick -> onNextClicked()
            is MusicPlayerEvent.OnPreviousClick -> onPreviousClicked()
            is MusicPlayerEvent.OnPause -> { /** Nothing **/}
            is MusicPlayerEvent.OnRepeatClick -> onRepeatClicked()
            is MusicPlayerEvent.OnShuffleClick -> onShuffleClicked()
        }
    }

    private fun onPlay(song: Song) {
        musicControllerUsecase.addToPlayingQueue(song)
    }

    private fun onNextClicked() {
        musicControllerUsecase.onNext()
    }

    private fun onPreviousClicked() {
        musicControllerUsecase.onPrevious()
    }

    private suspend fun onPlayingQueueClick() {
        _navigateToPlayingQueueScreen.send(
            Playlist.playingQueuePlaylist.copy(
                songs = musicPlayerState.value.musicState.playingQueue
            )
        )
    }

    private fun onPlayPauseButtonClicked(song: Song) {
        if(musicPlayerState.value.musicState.isPlaying) {
            musicControllerUsecase.onPause()
        } else {
            musicControllerUsecase.onPlay(song = song)
        }
    }

    private fun onShuffleClicked() {
        val state = musicPlayerState.value.musicState
        val shuffleModeEnabled = when(state.shuffleMode) {
            ShuffleMode.SHUFFLE -> false
            ShuffleMode.NONE -> true
        }
        musicControllerUsecase.onShuffleButtonPressed(shuffleModeEnabled)
    }

    private fun onRepeatClicked() {
        val state = musicPlayerState.value.musicState
        val repeatMode = when(state.repeatMode) {
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.REPEAT_OFF
            RepeatMode.REPEAT_OFF -> RepeatMode.REPEAT_ALL
        }
        musicControllerUsecase.onRepeatButtonPressed(repeatMode)
    }

    private fun snapTo(duration: Long) {
        musicControllerUsecase.snapTo(duration)
    }

    private fun collectDuration() = viewModelScope.launch {
        musicControllerUsecase.timePassed.collectLatest { duration ->
            _musicPlayerState.update {
                it.copy(
                    duration = duration
                )
            }
        }
    }
    private fun collectMusicState() = viewModelScope.launch {
        musicControllerUsecase.musicState.collectLatest { musicState ->
            _musicPlayerState.update {
                it.copy(
                    musicState = musicState
                )
            }
        }
    }
}