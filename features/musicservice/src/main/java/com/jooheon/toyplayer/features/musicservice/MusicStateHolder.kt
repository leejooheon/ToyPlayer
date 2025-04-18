package com.jooheon.toyplayer.features.musicservice

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.musicservice.ext.toPlaybackState
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicStateHolder @Inject constructor() {
    private val _mediaItems = MutableSharedFlow<List<MediaItem>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val mediaItems = _mediaItems.asSharedFlow()

    private val _mediaItem = MutableSharedFlow<MediaItem>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val mediaItem = _mediaItem.asSharedFlow()

    private val _playbackError = MutableSharedFlow<PlaybackException>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val playbackError = _playbackError

    private val _musicState = MutableStateFlow(MusicState.default)
    val musicState = _musicState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _repeatMode = MutableStateFlow(ExoPlayer.REPEAT_MODE_ALL)
    val repeatMode = _repeatMode.asStateFlow()

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode = _shuffleMode.asStateFlow()

    private val _currentDuration = MutableStateFlow(C.TIME_UNSET)
    val currentDuration = _currentDuration.asStateFlow()

    private val _playWhenReady = MutableStateFlow(false)
    val playWhenReady = _playWhenReady.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState = _playbackState.asStateFlow()

    private val _disContinuation = MutableStateFlow(Pair(0L, 0L))
    val disContinuation = _disContinuation.asStateFlow()

    internal fun observeStates(scope: CoroutineScope) = scope.launch {
        launch {
            mediaItem.collectLatest { mediaItem ->
                val song = mediaItem
                    .takeIf { it != MediaItem.EMPTY }
                    ?.toSong()
                    ?: Song.default

                Timber.d( "collectMediaItem: ${song.title.defaultEmpty()}")
                _musicState.update {
                    it.copy(
                        currentPlayingMusic = song,
                    )
                }
            }
        }
        launch {
            combine(
                playWhenReady,
                playbackState,
            ) { playWhenReady, playbackState ->
                Pair(playWhenReady, playbackState)
            }.collectLatest { (playWhenReady, playbackState) ->
                _musicState.update {
                    it.copy(
                        playbackState = playbackState.toPlaybackState(playWhenReady)
                    )
                }
            }
        }
    }

    internal fun onMediaItemsChanged(mediaItems: List<MediaItem>) {
        _mediaItems.tryEmit(mediaItems)
    }
    internal fun onMediaItemChanged(mediaItem: MediaItem) {
        _mediaItem.tryEmit(mediaItem)
    }
    internal fun onIsPlayingChanged(isPlaying: Boolean) {
        _isPlaying.tryEmit(isPlaying)
    }
    internal fun onRepeatModeChanged(repeatMode: Int) {
        _repeatMode.tryEmit(repeatMode)
    }
    internal fun onShuffleModeChanged(shuffleMode: Boolean) {
        _shuffleMode.tryEmit(shuffleMode)
    }
    internal fun onCurrentDurationChanged(duration: Long) {
        _currentDuration.tryEmit(duration)
    }
    internal fun onPlayWhenReadyChanged(playWhenReady: Boolean) {
        _playWhenReady.tryEmit(playWhenReady)
    }
    internal fun onPlaybackStateChanged(playbackState: Int) {
        _playbackState.tryEmit(playbackState)
    }
    internal fun onPlaybackException(exception: PlaybackException) {
        Timber.e("onPlaybackException: ${PlaybackException.getErrorCodeName(exception.errorCode)}, ${exception.message}")
        _playbackError.tryEmit(exception)
    }
    internal fun onPositionDiscontinuity(oldPosition: Long, newPosition: Long) {
        _disContinuation.tryEmit(Pair(oldPosition, newPosition))
    }
}