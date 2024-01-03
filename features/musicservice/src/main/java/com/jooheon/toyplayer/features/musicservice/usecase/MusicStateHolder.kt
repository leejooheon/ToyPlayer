package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.toyplayer.domain.common.FailureStatus
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.musicservice.ext.isBuffering
import com.jooheon.toyplayer.features.musicservice.ext.isPlaying
import com.jooheon.toyplayer.features.musicservice.ext.playbackErrorReason
import com.jooheon.toyplayer.features.musicservice.ext.playbackState
import com.jooheon.toyplayer.features.musicservice.ext.toPlaybackState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class MusicStateHolder(
    private val applicationScope: CoroutineScope,
    private val playingQueueUseCase: PlayingQueueUseCase
) {
    private val TAG = MusicStateHolder::class.java.simpleName

    private val _songLibrary = MutableStateFlow<List<Song>>(emptyList())
    val songLibrary = _songLibrary.asStateFlow()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _mediaItems = MutableStateFlow<List<MediaItem>?>(null)
    val mediaItems = _mediaItems.asStateFlow()

//    private val _playingQueue = MutableStateFlow<List<Song>>(emptyList())
//    val playingQueue = _playingQueue.asStateFlow()

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

    private val _playWhenReady = MutableStateFlow(false)
    val playWhenReady = _playWhenReady.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState = _playbackState.asStateFlow()

    private val _playerState = MutableStateFlow<@Player.State Int>(Player.STATE_IDLE)
    val playerState = _playerState.asStateFlow()

    private val _playbackError = Channel<PlaybackException>()
    val playbackError = _playbackError.receiveAsFlow()

    private val _browserConnected = MutableStateFlow(false)
    val browserConnected = _browserConnected.asStateFlow()

    init {
        collectDuration()
        collectCurrentWindow()
        collectMediaItemsState()
        collectPlaybackState()
        collectPlayerState()
        collectPlaybackError()
    }
    private fun collectMediaItemsState() = applicationScope.launch {
        mediaItems.collectLatest { mediaItems ->
            mediaItems ?: return@collectLatest

            val newPlayingQueue = mediaItems.mapNotNull { mediaItem ->
                songLibrary.value.firstOrNull { it.key() == mediaItem.mediaId }
            }

            playingQueueUseCase.updatePlayingQueue(newPlayingQueue)
        }
    }
    private fun collectDuration() = applicationScope.launch {
        currentDuration.collectLatest { duration ->
            _musicState.update {
                it.copy(
                    timePassed = duration
                )
            }
        }
    }
    private fun collectCurrentWindow() = applicationScope.launch {
        currentWindow.collectLatest { currentWindow ->
            currentWindow ?: return@collectLatest

            val song = playingQueueUseCase.getPlayingQueue().firstOrNull {
                it.key() == currentWindow.mediaItem.mediaId
            } ?: Song.default

            Timber.tag(TAG).d( "collectCurrentWindow: ${song.title.defaultEmpty()}")
            if(song == Song.default) return@collectLatest

            _musicState.update {
                it.copy(
                    currentPlayingMusic = song,
                    timePassed = 0L
                )
            }
        }
    }

    private fun collectPlaybackState() = applicationScope.launch {
        playbackState.collectLatest { playbackState ->
            Timber.tag(TAG).d( "collectPlaybackState: ${playbackState.playbackState()}, ${playbackState.isPlaying}, ${playbackState.isBuffering}")
            _musicState.update {
                it.copy(
                    playbackState = playbackState
                )
            }
        }
    }

    private fun collectPlayerState() = applicationScope.launch {
        combine(
            playWhenReady,
            playerState,
        ) { playWhenReady, playerState ->
            Pair(playWhenReady, playerState)
        }.collectLatest { (playWhenReady, playerState) ->
            _musicState.update {
                it.copy(
                    playbackState = playerState.toPlaybackState(playWhenReady)
                )
            }
        }
    }

    private fun collectPlaybackError() = applicationScope.launch {
        playbackError.collectLatest { error ->
            Timber.tag(TAG).e("collectPlaybackException: ${error.errorCode.playbackErrorReason()}, ${error.message}")

            val failureStatus = when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> FailureStatus.NO_INTERNET

                else -> FailureStatus.OTHER
            }

//            _musicStreamErrorChannel.send(
//                Resource.Failure(
//                    failureStatus = failureStatus,
//                    code = error.errorCode,
//                    message = error.message
//                )
//            )
        }
    }

    fun enqueueSongLibrary(songs: List<Song>) {
        val songLibrary = songLibrary.value

        songs.filter { song ->
            songLibrary.none { it.key() == song.key() } // remove duplicate
        }.also {
            _songLibrary.tryEmit(songLibrary + it)
        }
    }

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
    fun onPlayWhenReadyChanged(playWhenReady: Boolean) {
        _playWhenReady.tryEmit(playWhenReady)
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
    fun onBrowserConnectionChanged(connection: Boolean) {
        _browserConnected.tryEmit(connection)
    }
}