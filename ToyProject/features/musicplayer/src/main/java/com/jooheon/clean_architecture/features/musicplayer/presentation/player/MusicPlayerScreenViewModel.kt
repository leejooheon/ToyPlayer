package com.jooheon.clean_architecture.features.musicplayer.presentation.player

import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicplayer.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase
): BaseViewModel() {
    override val TAG: String = MusicPlayerScreenViewModel::class.java.simpleName

    private val _musicPlayerScreenState = MutableStateFlow(MusicPlayerScreenState.default)
    val musicPlayerScreenState = _musicPlayerScreenState.asStateFlow()

    init {
        collectMusicState()
        collectExoPlayerState()
        collectDuration()
        loadData()
    }

//    MusicPlayerScreenState, MusicPlayerScreenEvent
    fun dispatch(event: MusicPlayerScreenEvent, state: MusicPlayerScreenState) {
        when(event) {
            MusicPlayerScreenEvent.OnPlayPause -> onPlayPauseButtonClicked(state.musicState.currentPlayingMusic)
            MusicPlayerScreenEvent.OnPlay -> onPlay(state.musicState.currentPlayingMusic)
            MusicPlayerScreenEvent.OnPause -> { /** Nothing **/}
            MusicPlayerScreenEvent.OnNext -> onNextClicked()
            MusicPlayerScreenEvent.OnPrevious -> onPreviousClicked()
            MusicPlayerScreenEvent.OnShuffle -> onShuffleClicked()
            MusicPlayerScreenEvent.OnRepeat -> onRepeatClicked()
            MusicPlayerScreenEvent.SnapTo -> snapTo(state.currentDuration)
        }
    }

    fun loadData() {
        musicControllerUsecase.loadPlaylist()
    }

    private fun onPlay(song: Song) = viewModelScope.launch {
        musicControllerUsecase.onPlay(song)
    }
    private fun onNextClicked() = viewModelScope.launch {
        musicControllerUsecase.onNext()
    }
    private fun onPreviousClicked() = viewModelScope.launch {
        musicControllerUsecase.onPrevious()
    }
    private fun onPlayPauseButtonClicked(song: Song) = viewModelScope.launch {
        if(musicPlayerScreenState.value.musicState.isPlaying) {
            musicControllerUsecase.onPause()
        } else {
            musicControllerUsecase.onPlay(song)
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
    private fun collectMusicState() = viewModelScope.launch {
        musicControllerUsecase.musicState.collectLatest { musicState ->
            _musicPlayerScreenState.update {
                it.copy(
                    musicState = musicState
                )
            }
        }
    }

    private fun collectExoPlayerState() = viewModelScope.launch {
        musicControllerUsecase.exoPlayerState.collectLatest { progress ->
            _musicPlayerScreenState.update {
                it.copy(
                    progressBarVisibility = progress == ExoPlayer.STATE_BUFFERING
                )
            }
        }
    }

    private fun collectDuration() = viewModelScope.launch {
        musicControllerUsecase.timePassed.collectLatest { duration ->
            _musicPlayerScreenState.update {
                it.copy(
                    currentDuration = duration
                )
            }
        }
    }
}