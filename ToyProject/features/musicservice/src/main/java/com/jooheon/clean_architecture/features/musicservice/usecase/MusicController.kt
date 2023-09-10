package com.jooheon.clean_architecture.features.musicservice.usecase

import android.content.Context
import android.util.Log
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCaseImpl
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.data.exoPlayerStateAsString
import com.jooheon.clean_architecture.features.musicservice.data.uri
import com.jooheon.clean_architecture.toyproject.features.common.extension.showToast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

/**
 * IMPORTANT!!
 * NEVER APPROACH DIRECTLY.
 * Use MusicControllerUsecase.
**/
class MusicController @Inject constructor( // di 옮기고, internal class로 바꿔야함
    @ApplicationContext private val context: Context,
    private val applicationScope: CoroutineScope,
    private val exoPlayer: ExoPlayer,
) : IMusicController {
    private val TAG = MusicService::class.java.simpleName + "@" +  MusicController::class.java.simpleName

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _playingQueue = MutableStateFlow(emptyList<Song>())
    val playingQueue: StateFlow<List<Song>> = _playingQueue

    private val _currentPlayingMusic = MutableStateFlow(Song.default)
    val currentPlayingMusic: StateFlow<Song> = _currentPlayingMusic

    private val _repeatMode = MutableStateFlow(RepeatMode.REPEAT_ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode

    private val _shuffleMode = MutableStateFlow(ShuffleMode.SHUFFLE)
    val shuffleMode: StateFlow<ShuffleMode> = _shuffleMode

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration: StateFlow<Long> = _currentDuration

    private val _exoPlayerState = MutableStateFlow<Int?>(null)
    val exoPlayerState: StateFlow<Int?> = _exoPlayerState

    private var durationFromPlayerJob: Job? = null

    init {
        initExoPlayer()
    }

    private suspend fun playWithMediaItem(mediaItem: MediaItem) = withContext(Dispatchers.Main) {
        exoPlayer.run {
            playWhenReady = true
            setMediaItem(mediaItem)
            prepare()
//            seekTo(55000)
            play()
        }
    }

    override suspend fun updatePlayingQueue(songs: List<Song>) {
        _playingQueue.tryEmit(songs)
    }

    override suspend fun play(song: Song) {
        val musicStreamUri = song.uri
        Timber.tag(TAG).d("play musicStreamUri - ${musicStreamUri}, seekTo: ${song.duration}")

        _currentPlayingMusic.tryEmit(song)
        playWithMediaItem(MediaItem.fromUri(musicStreamUri))
    }

    override suspend fun resume() = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d("resume: ${currentPlayingMusic.value}, Metadata: ${exoPlayer.mediaMetadata.title}")
        exoPlayer.play()
        debugMessage("resume")
    }

    override suspend fun pause() = withContext(Dispatchers.Main) {
        exoPlayer.run {
            playWhenReady = true
            pause()
        }
        debugMessage("pause")
    }

    override suspend fun stop() = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d( "stop")
        _currentPlayingMusic.tryEmit(Song.default)
        exoPlayer.stop()

        debugMessage("stop")
    }

    override suspend fun previous() {
        Timber.tag(TAG).d( "previous")
        val playlist = playingQueue.value
        val currentIndex = playlist.indexOfFirst {
            it.hashCode() == currentPlayingMusic.value.hashCode()
        }

        val previousSong = when {
            currentIndex == 0 -> playlist.last()
            currentIndex >= 1 -> playlist.get(currentIndex - 1)
            else -> playlist.firstOrNull()
        } ?: Song.default

        play(previousSong)

        debugMessage("previous")
    }

    override suspend fun next() {
        val playlist = playingQueue.value
        val currentIndex = playlist.indexOfFirst {
            it.hashCode() == currentPlayingMusic.value.hashCode()
        }

        val nextSong = when {
            currentIndex >= playlist.lastIndex -> playlist.firstOrNull()
            currentIndex != -1 -> playlist.get(currentIndex + 1)
            else -> playlist.firstOrNull()
        } ?: Song.default
        Timber.tag(TAG).d( "nextSong: ${nextSong.title}")

        play(nextSong)

        debugMessage("next")
    }

    override suspend fun snapTo(duration: Long, fromUser: Boolean) = withContext(Dispatchers.Main) {
        _currentDuration.tryEmit(duration)
        if(fromUser) exoPlayer.seekTo(duration)
    }

    override suspend fun changeRepeatMode() = withContext(Dispatchers.Main) {
        val repeatMode = when(repeatMode.value) {
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.REPEAT_OFF
            RepeatMode.REPEAT_OFF -> RepeatMode.REPEAT_ALL
        }
        _repeatMode.tryEmit(repeatMode)
        debugMessage("repeatMode")
    }

    override suspend fun changeShuffleMode() = withContext(Dispatchers.Main) {
        val shuffleMode = when(shuffleMode.value) {
            ShuffleMode.SHUFFLE -> ShuffleMode.NONE
            ShuffleMode.NONE -> ShuffleMode.SHUFFLE
        }

        _shuffleMode.tryEmit(shuffleMode)
        debugMessage("shuffleMode")
    }

    override suspend fun refresh() {
        val currentPlayingMusic = currentPlayingMusic.value
        stop()
        delay(300)
        play(currentPlayingMusic)

        debugMessage("refresh")
    }

    override suspend fun changeSkipDuration() {
//        val skipDuration = settingUseCase.getSkipForwardBackward()
//        runOnUiThread {
//            _skipState.tryEmit(skipDuration)
//        }
    }

    fun collectDurationFromPlayer() {
        durationFromPlayerJob?.cancel()
        durationFromPlayerJob = applicationScope.launch(Dispatchers.IO) {
            while (isPlaying.value && isActive) {
                val duration = withContext(Dispatchers.Main) {
                    if (exoPlayer.duration != -1L) {
                        exoPlayer.currentPosition
                    } else {
                        0L
                    }
                }
                Timber.tag(TAG).d( "snapTo: ${duration}, job: ${this}")

                snapTo(
                    duration = duration,
                    fromUser = false
                )
                delay(500)
            }
        }
    }
    private fun initExoPlayer() {
        exoPlayer.apply {
            addListener(exoPlayerListener())
        }.run {
            emitExoPlayerState(playbackState)
        }
    }

    private fun exoPlayerListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            Timber.tag(TAG).d(playbackState.exoPlayerStateAsString())

            if (playbackState == ExoPlayer.STATE_ENDED) {
                applicationScope.launch {

                    when (this@MusicController.repeatMode.value) {
                        RepeatMode.REPEAT_ALL -> this@MusicController.next()
                        RepeatMode.REPEAT_OFF -> this@MusicController.stop()
                        RepeatMode.REPEAT_ONE -> this@MusicController.play(currentPlayingMusic.value)
                    }
                }
            }

            emitExoPlayerState(playbackState)
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            var msg = ""
            repeat(events.size()) {
                msg += "${it}th: event: ${events.get(it)}\n"
            }

            Timber.tag(TAG).d("====== onEvents ======\n${msg}")
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            Timber.d("onPlayWhenReadyChanged: ${playWhenReady}, reason: ${reason}")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            Timber.tag(TAG).d( "onIsPlayingChanged - ${isPlaying}")
            _isPlaying.tryEmit(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Timber.tag(TAG).d("Jooheon onPlayerError: ${error.message}")
            _currentPlayingMusic.tryEmit(Song.default)
        }
    }

    private fun emitExoPlayerState(playbackState: Int) = applicationScope.launch {
        _exoPlayerState.tryEmit(playbackState)
    }

    private suspend fun debugMessage(message: String) = withContext(Dispatchers.Main) {
        context.showToast(message)
    }
}