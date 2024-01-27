package com.jooheon.toyplayer.features.musicservice

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.musicservice.data.RingBuffer
import com.jooheon.toyplayer.features.musicservice.ext.toPlaybackState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

class MusicStateHolder(
    private val applicationScope: CoroutineScope,
) {
    private val mutex = Mutex()
    private val streamBuffer = RingBuffer<Pair<String, Uri>?>(URI_BUFFER_SIZE) { null }

    private val _mediaItems = MutableSharedFlow<List<MediaItem>?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val mediaItems = _mediaItems

    private val _mediaItem = MutableSharedFlow<MediaItem?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val mediaItem = _mediaItem

    private val _playbackError = MutableSharedFlow<PlaybackException>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val playbackError = _playbackError

    private val _playingQueue = MutableStateFlow<List<Song>>(emptyList())
    val playingQueue = _playingQueue.asStateFlow()

    private val _songLibrary = MutableStateFlow<List<Song>>(emptyList())
    val songLibrary = _songLibrary.asStateFlow()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

//    private val _playingQueue = MutableStateFlow<List<Song>>(emptyList())
//    val playingQueue = _playingQueue.asStateFlow()

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

    private val _disContinuation = MutableStateFlow(Pair(0L, 0L))
    val disContinuation = _disContinuation.asStateFlow()

    init {
        collectDuration()
        collectMediaItem()
        collectMediaItemsState()
        collectPlaybackState()
        collectPlayerState()
    }

    private fun collectMediaItemsState() = applicationScope.launch {
        mediaItems.collectLatest { mediaItems ->
            mediaItems ?: return@collectLatest

            val newPlayingQueue = mediaItems.mapNotNull { mediaItem ->
                songLibrary.value.firstOrNull { it.key() == mediaItem.mediaId }
            }
            _playingQueue.emit(newPlayingQueue)
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
    private fun collectMediaItem() = applicationScope.launch {
        mediaItem.collectLatest { mediaItem ->
            val song = songLibrary.value.firstOrNull { it.key() == mediaItem?.mediaId } ?: Song.default

            Timber.d( "collectMediaItem: ${song.title.defaultEmpty()}")
            _musicState.update {
                it.copy(
                    currentPlayingMusic = song,
                    timePassed = 0L
                )
            }
        }
    }

    private fun collectPlaybackState() = applicationScope.launch {
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

    internal fun enqueueSongLibrary(songs: List<Song>) {
        val songLibrary = songLibrary.value

        songs.filter { song ->
            songLibrary.none { it.key() == song.key() } // remove duplicate
        }.also {
            _songLibrary.tryEmit(songLibrary + it)
        }
    }

    internal fun onMediaItemsChanged(mediaItems: List<MediaItem>) {
        _mediaItems.tryEmit(mediaItems)
    }
    internal fun onMediaItemChanged(mediaItem: MediaItem?) {
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
    internal fun onPlayerStateChanged(playerState: Int) {
        _playerState.tryEmit(playerState)
    }
    internal fun onPlaybackException(exception: PlaybackException) {
        Timber.e("onPlaybackException: ${PlaybackException.getErrorCodeName(exception.errorCode)}, ${exception.message}")
        _playbackError.tryEmit(exception)
    }
    internal fun onPositionDiscontinuity(oldPosition: Long, newPosition: Long) {
        _disContinuation.tryEmit(Pair(oldPosition, newPosition))
    }

    internal suspend fun appendToRingBuffer(data: Pair<String, Uri>) {
        mutex.withLock {
            streamBuffer.append(data)
        }
    }
    internal suspend fun getStreamResultOrNull(id: String): Uri? {
        mutex.withLock {
            repeat(URI_BUFFER_SIZE) { index ->
                val (key, uri) = streamBuffer.getOrNull(index) ?: return@repeat
                if (key == id) {
                    return uri
                }
            }
            return null
        }
    }
    companion object {
        internal const val URI_BUFFER_SIZE = 3
    }
}