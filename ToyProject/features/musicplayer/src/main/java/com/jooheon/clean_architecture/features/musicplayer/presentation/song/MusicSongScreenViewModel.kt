package com.jooheon.clean_architecture.features.musicplayer.presentation.song

import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.music.PlaylistType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.MusicMediaItemEventUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSongScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
): BaseViewModel() {
    override val TAG: String = MusicSongScreenViewModel::class.java.simpleName

    private val _musicPlayerScreenState = MutableStateFlow(MusicPlayerScreenState.default)
    val musicPlayerScreenState = _musicPlayerScreenState.asStateFlow()

    init {
        collectMusicState()
        collectExoPlayerState()
        collectDuration()
        collectPlaylist()
        loadData(musicPlayerScreenState.value.musicState.playlistType)
    }

    fun dispatch(event: MusicPlayerScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlayerScreenEvent.OnPlayPauseClick -> onPlayPauseButtonClicked(event.song)
            is MusicPlayerScreenEvent.OnPlayClick -> onPlay(event.song)
            is MusicPlayerScreenEvent.OnSnapTo -> snapTo(event.duration)
            is MusicPlayerScreenEvent.OnNextClick -> onNextClicked()
            is MusicPlayerScreenEvent.OnPreviousClick -> onPreviousClicked()
            is MusicPlayerScreenEvent.OnPause -> { /** Nothing **/}
            is MusicPlayerScreenEvent.OnRepeatClick -> onRepeatClicked()
            is MusicPlayerScreenEvent.OnShuffleClick -> onShuffleClicked()
            is MusicPlayerScreenEvent.OnPlaylistTypeChanged -> onPlaylistTypeChanged(event.playlistType)
        }
    }

    fun onMusicMediaItemEvent(event: MusicMediaItemEvent) {
        musicMediaItemEventUseCase.dispatch(event)
    }

    fun loadData(playlistType: PlaylistType) {
        musicControllerUsecase.loadPlaylist(playlistType)
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

    private fun onPlaylistTypeChanged(playlistType: PlaylistType) {
        if(musicPlayerScreenState.value.musicState.playlistType == playlistType) {
            return
        }

        loadData(playlistType)
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

    private fun collectPlaylist() = viewModelScope.launch {
        musicMediaItemEventUseCase.playlistState.collectLatest { playlists ->
            _musicPlayerScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }
}