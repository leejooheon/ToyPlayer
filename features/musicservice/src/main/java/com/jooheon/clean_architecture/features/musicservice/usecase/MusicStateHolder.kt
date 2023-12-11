package com.jooheon.clean_architecture.features.musicservice.usecase

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class MusicStateHolder {
    private val TAG = MusicStateHolder::class.java.simpleName

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems = _mediaItems.asStateFlow()

    private val _currentWindow = MutableStateFlow<Timeline.Window?>(null)
    val currentWindow = _currentWindow.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _repeatMode = MutableStateFlow(ExoPlayer.REPEAT_MODE_ALL)
    val repeatMode = _repeatMode.asStateFlow()

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode = _shuffleMode.asStateFlow()

    private val _currentDuration = MutableStateFlow(C.TIME_UNSET)
    val currentDuration = _currentDuration.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState = _playbackState.asStateFlow()

    private val _playerState = MutableStateFlow<@Player.State Int>(Player.STATE_IDLE)
    val playerState = _playerState.asStateFlow()

    private val _playbackError = Channel<PlaybackException>()
    val playbackError = _playbackError.receiveAsFlow()

    fun onMediaItemsChanged(mediaItems: List<MediaItem>) {
        _mediaItems.tryEmit(mediaItems)
    }
    fun onCurrentWindowChanged(currentWindow: Timeline.Window?) {
        _currentWindow.tryEmit(currentWindow)
    }
    fun onIsPlayingChanged(isPlaying: Boolean) {
        _isPlaying.tryEmit(isPlaying)
    }
    fun onRepeatModeChanged(repeatMode: Int) {
        _repeatMode.tryEmit(repeatMode)
    }
    fun onShuffleModeChanged(shuffleMode: Boolean) {
        _shuffleMode.tryEmit(shuffleMode)
    }
    fun onCurrentDurationChanged(duration: Long) {
        _currentDuration.tryEmit(duration)
    }
    fun onPlaybackStateChanged(playbackState: Int) {
        _playbackState.tryEmit(playbackState)
    }
    fun onPlayerStateChanged(playerState: Int) {
        _playerState.tryEmit(playerState)
    }
    fun onPlaybackErrorChannel(exception: PlaybackException) {
        _playbackError.trySend(exception)
    }
}