package com.jooheon.clean_architecture.features.musicservice.usecase

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.ext.playbackErrorReason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class MusicStateHolder(
    private val applicationScope: CoroutineScope
) {
    private val TAG = MusicStateHolder::class.java.simpleName

    private val _songLibrary = mutableListOf<Song>()
    val songLibrary: List<Song> get() = _songLibrary.toList()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems = _mediaItems.asStateFlow()

    private val _playingQueue = MutableStateFlow<List<Song>>(emptyList())
    val playingQueue = _playingQueue.asStateFlow()

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

    init {
        collectDuration()
        collectPlaybackState()
        collectCurrentWindow()
        collectPlaybackError()
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
    private fun collectPlaybackState() = applicationScope.launch {
        playbackState.collectLatest { playbackState ->
            _musicState.update {
                it.copy(
                    playbackState = playbackState
                )
            }
        }
    }
    private fun collectCurrentWindow() = applicationScope.launch {
        combine(
            playingQueue,
            currentWindow
        ) { playingQueue, currentWindow ->
            Pair(playingQueue, currentWindow)
        }.collectLatest { (playingQueue, currentWindow) ->
            currentWindow ?: return@collectLatest
            val song = playingQueue.firstOrNull { it.id() == currentWindow.mediaItem.mediaId } ?: Song.default
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
        songs.filter { song ->
            _songLibrary.none { it.id() == song.id() } // remove duplicate
        }.also {
            _songLibrary.addAll(it)
        }
    }

    fun onMediaItemsChanged(mediaItems: List<MediaItem>) {
        val newPlayingQueue = mediaItems.mapNotNull { mediaItem ->
            songLibrary.firstOrNull { it.id() == mediaItem.mediaId }
        }

        _mediaItems.tryEmit(mediaItems)
        _playingQueue.tryEmit(newPlayingQueue)
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