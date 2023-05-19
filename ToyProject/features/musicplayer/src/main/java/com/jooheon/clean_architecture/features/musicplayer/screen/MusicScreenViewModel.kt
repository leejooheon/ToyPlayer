package com.jooheon.clean_architecture.features.musicplayer.screen

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase
): BaseViewModel() {
    override val TAG: String = MusicScreenViewModel::class.java.simpleName

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _progressBarVisibilityState = MutableStateFlow(false)
    val progressBarVisibilityState = _progressBarVisibilityState.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    init {
        collectMusicState()
        collectExoPlayerState()
        collectDuration()
        loadData()
    }

    fun loadData() {
        musicControllerUsecase.loadPlaylist()
    }

    fun onPlay(song: Song) = viewModelScope.launch {
        musicControllerUsecase.onPlay(song)
    }
    fun onNextClicked() = viewModelScope.launch {
        musicControllerUsecase.onNext()
    }
    fun onPreviousClicked() = viewModelScope.launch {
        musicControllerUsecase.onPrevious()
    }
    fun onPlayPauseButtonClicked(song: Song) = viewModelScope.launch {
        if(musicState.value.isPlaying) {
            musicControllerUsecase.onPause()
        } else {
            musicControllerUsecase.onPlay(song)
        }
    }
    fun onShuffleClicked() = viewModelScope.launch {
        musicControllerUsecase.onShuffleButtonPressed()
    }

    fun onRepeatClicked() = viewModelScope.launch {
        musicControllerUsecase.onRepeatButtonPressed()
    }

    fun snapTo(duration: Long) {
        musicControllerUsecase.snapTo(duration)
    }
    private fun collectMusicState() = viewModelScope.launch {
        musicControllerUsecase.musicState.collectLatest {
            _musicState.tryEmit(it.copy())
        }
    }

    private fun collectExoPlayerState() = viewModelScope.launch {
        musicControllerUsecase.exoPlayerState.collectLatest {
            it ?: return@collectLatest
            _progressBarVisibilityState.tryEmit(it == ExoPlayer.STATE_BUFFERING)
        }
    }

    private fun collectDuration() = viewModelScope.launch {
        musicControllerUsecase.timePassed.collectLatest {
            _duration.tryEmit(it)
        }
    }
}