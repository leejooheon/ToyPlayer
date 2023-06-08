package com.jooheon.clean_architecture.features.musicplayer.presentation.common.music

import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class AbsMusicPlayerViewModel (
    private val musicControllerUsecase: MusicControllerUsecase,
): BaseViewModel() {
    override val TAG = AbsMusicPlayerViewModel::class.java.simpleName

    private val _musicPlayerState = MutableStateFlow(MusicPlayerState.default)
    val musicPlayerState = _musicPlayerState.asStateFlow()

    init {
        collectMusicState()
        collectDuration()
        collectExoPlayerState()
    }

    fun dispatch(event: MusicPlayerEvent) = viewModelScope.launch {
        when(event) {
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

    private fun onPlay(song: Song) = viewModelScope.launch {
        musicControllerUsecase.onPlay(
            song = song,
            addToPlayingQueue = true
        )
    }

    private fun onNextClicked() = viewModelScope.launch {
        musicControllerUsecase.onNext()
    }

    private fun onPreviousClicked() = viewModelScope.launch {
        musicControllerUsecase.onPrevious()
    }

    private fun onPlayPauseButtonClicked(song: Song) = viewModelScope.launch {
        if(musicPlayerState.value.musicState.isPlaying) {
            musicControllerUsecase.onPause()
        } else {
            musicControllerUsecase.onPlay(
                song = song,
                addToPlayingQueue = false,
            )
        }
    }

    private fun onShuffleClicked() = viewModelScope.launch {
        musicControllerUsecase.onShuffleButtonPressed()
    }

    private fun onRepeatClicked() = viewModelScope.launch {
        musicControllerUsecase.onRepeatButtonPressed()
    }

    private fun snapTo(duration: Long) {
        musicControllerUsecase.snapTo(duration)
    }

    private fun collectExoPlayerState() = viewModelScope.launch {
        musicControllerUsecase.exoPlayerState.collectLatest { progress ->
            _musicPlayerState.update {
                it.copy(
                    progressBarVisibility = progress == ExoPlayer.STATE_BUFFERING
                )
            }
        }
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