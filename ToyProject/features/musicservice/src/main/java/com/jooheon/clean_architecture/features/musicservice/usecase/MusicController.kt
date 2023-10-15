package com.jooheon.clean_architecture.features.musicservice.usecase

import androidx.annotation.FloatRange
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.ext.currentWindow
import com.jooheon.clean_architecture.features.musicservice.ext.mediaItemTransitionReason
import com.jooheon.clean_architecture.features.musicservice.ext.playWhenReadyChangeReason
import com.jooheon.clean_architecture.features.musicservice.ext.playerState
import com.jooheon.clean_architecture.features.musicservice.ext.timelineChangeReason
import com.jooheon.clean_architecture.features.musicservice.ext.windows
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber

@UnstableApi
class MusicController(
    private val applicationScope: CoroutineScope,
    private val exoPlayer: ExoPlayer,
) : IMusicController {
    private val TAG = MusicService::class.java.simpleName + "@" +  MusicController::class.java.simpleName + "@Main"
    private val TAG_PLAYER = MusicService::class.java.simpleName + "@" + "PlayerListener"

    private val _timelineWindows = MutableStateFlow(exoPlayer.currentTimeline.windows)
    val timelineWindows = _timelineWindows.asStateFlow()

    private val _nullableWindow = MutableStateFlow(exoPlayer.currentWindow)
    val nullableWindow = _nullableWindow.asStateFlow()

    private val _mediaItemIndex = MutableStateFlow(if (exoPlayer.mediaItemCount == 0) C.INDEX_UNSET else exoPlayer.currentMediaItemIndex)
    val mediaItemIndex = _mediaItemIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _repeatMode = MutableStateFlow(ExoPlayer.REPEAT_MODE_ALL)
    val repeatMode: StateFlow<Int> = _repeatMode

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode: StateFlow<Boolean> = _shuffleMode

    private val _currentDuration = MutableStateFlow(C.TIME_UNSET)
    val currentDuration: StateFlow<Long> = _currentDuration

    private val _playbackSpeed = MutableStateFlow<@receiver:FloatRange(from = 0.1, to = 1.0) Float>( 1f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed

    private val _exoPlayerState = MutableStateFlow<@Player.State Int>(ExoPlayer.STATE_IDLE)
    val exoPlayerState: StateFlow<Int> = _exoPlayerState

    private val _forceUpdate = MutableStateFlow(Player.STATE_READY)
    val forceUpdate: StateFlow<Int> = _forceUpdate

    private val _playbackExceptionChannel = Channel<PlaybackException>()
    val playbackExceptionChannel = _playbackExceptionChannel.receiveAsFlow()

    private var durationFromPlayerJob: Job? = null

    init {
        initExoPlayer()
    }
    override suspend fun play(
        index: Int,
        seekTo: Long,
        playWhenReady: Boolean,
    ) = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d("play: index: $index, playWhenReady: $playWhenReady")

        exoPlayer.run {
            this@run.playWhenReady = playWhenReady
            if(exoPlayer.currentMediaItemIndex != index) seekTo(index, seekTo)
            if(exoPlayerState.value == Player.STATE_IDLE) prepare()
        }
        debugMessage("play $playWhenReady")
    }

    override suspend fun previous() = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d( "previous")

        with(exoPlayer) {
            if(hasPreviousMediaItem()) {
                seekToPreviousMediaItem()
            } else {
                snapTo(C.TIME_UNSET, true)
            }

            debugMessage("previous")
        }
    }
    override suspend fun next() = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d( "next")
        with(exoPlayer) {
            if(hasNextMediaItem()) {
                seekToNextMediaItem()
            } else {
                snapTo(C.TIME_UNSET, true)
            }
            debugMessage("next")
        }
    }

    override suspend fun pause() = withContext(Dispatchers.Main) {
        if(isPlaying.value) {
            exoPlayer.run {
                pause()
            }
        }
        debugMessage("pause")
    }

    override suspend fun stop() = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d( "stop")
        exoPlayer.stop()
        debugMessage("stop")
    }

    override suspend fun snapTo(duration: Long, fromUser: Boolean) = withContext(Dispatchers.Main) {
        _currentDuration.tryEmit(duration)
        if(fromUser) exoPlayer.seekTo(duration)
    }

    override suspend fun changeRepeatMode(@Player.RepeatMode repeatMode: Int) = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d("changeRepeatMode: $repeatMode")
        exoPlayer.repeatMode = repeatMode
        debugMessage("repeatMode")
    }

    override suspend fun changeShuffleMode(shuffleModeEnabled: Boolean) = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d("changeShuffleMode: $shuffleModeEnabled")
        exoPlayer.shuffleModeEnabled = shuffleModeEnabled
        debugMessage("shuffleMode")
    }
    override suspend fun changePlaybackSpeed(
        @FloatRange(from = 0.1, to = 1.0) playbackSpeed: Float
    ) = withContext(Dispatchers.Main) {
        _playbackSpeed.tryEmit(playbackSpeed)
        exoPlayer.setPlaybackSpeed(playbackSpeed)
        debugMessage("changePlaybackSpeed: $playbackSpeed")
    }

    override suspend fun setMediaItems(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        playWhenReady: Boolean
    ) = withContext(Dispatchers.Main) {
        with(exoPlayer) {
            setMediaItems(mediaItems, startIndex, C.TIME_UNSET)

            this@with.playWhenReady = playWhenReady
            prepare()
        }
    }
    override suspend fun addMediaItems(
        mediaItems: List<MediaItem>,
        addNext: Boolean,
        playWhenReady: Boolean,
    ) = withContext(Dispatchers.Main) {
        with(exoPlayer) {
            val index = if(addNext) currentMediaItemIndex + 1 else mediaItemCount
            addMediaItems(index, mediaItems)

            if(playWhenReady) {
                prepare()
                this@MusicController.play(
                    index = index,
                    seekTo = C.TIME_UNSET,
                    playWhenReady = true
                )
            }
        }
    }
    override suspend fun removeMeidaItems(
        mediaItemIndexes: List<Int>
    ) = withContext(Dispatchers.Main) {
        mediaItemIndexes.forEach { exoPlayer.removeMediaItem(it) }
    }

    @Synchronized
    private fun collectDuration() {
        durationFromPlayerJob?.cancel()
        durationFromPlayerJob = applicationScope.launch(Dispatchers.Main) {
            while (exoPlayer.isPlaying && isActive) {
                val duration = if (exoPlayer.duration != C.TIME_UNSET) {
                    exoPlayer.currentPosition
                } else {
                    0L
                }
                Timber.tag(TAG).d( "snapTo: ${duration}, job: $this")

                snapTo(
                    duration = duration,
                    fromUser = false
                )

                withContext(Dispatchers.IO) {
                    delay(500)
                }
            }
        }
    }

    private fun initExoPlayer() {
        Timber.tag("Jooheon").e("initExoPlayer: $this")
        exoPlayer.apply {
            addListener(exoPlayerListener())
        }.run {
            emitExoPlayerState(playbackState)
        }
    }

    private fun exoPlayerListener() = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            Timber.tag(TAG_PLAYER).d("onMediaItemTransition: ${reason.mediaItemTransitionReason()}")
            val mediaItemIndex = if (exoPlayer.mediaItemCount == 0) C.INDEX_UNSET
                                 else exoPlayer.currentMediaItemIndex

            _nullableWindow.tryEmit(exoPlayer.currentWindow)
            _mediaItemIndex.tryEmit(mediaItemIndex)
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            Timber.tag(TAG_PLAYER).d("onTimelineChanged: ${reason.timelineChangeReason()}")

            _timelineWindows.tryEmit(timeline.windows)

            val mediaItemIndex = if (exoPlayer.mediaItemCount == 0) C.INDEX_UNSET
            else exoPlayer.currentMediaItemIndex
            _mediaItemIndex.tryEmit(mediaItemIndex)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            Timber.tag(TAG_PLAYER).d( "onIsPlayingChanged - ${isPlaying}")
            _isPlaying.tryEmit(isPlaying)

            if(isPlaying) {
                collectDuration()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            Timber.tag(TAG_PLAYER).d("onPlaybackStateChanged: ${playbackState.playerState()}")
            emitExoPlayerState(playbackState)
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            Timber.tag(TAG_PLAYER).d("onMediaMetadataChanged: ${mediaMetadata.title}")
        }

        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onPlaylistMetadataChanged(mediaMetadata)
            Timber.tag(TAG_PLAYER).d("onPlaylistMetadataChanged: mediaMetadata: ${mediaMetadata.title}")
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            Timber.tag(TAG_PLAYER).d("onPlayWhenReadyChanged: playWhenReady: ${playWhenReady}, reason: ${reason.playWhenReadyChangeReason()}")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            _repeatMode.tryEmit(repeatMode)
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            _shuffleMode.tryEmit(shuffleModeEnabled)
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Timber.tag(TAG_PLAYER).d("onPlayerError: ${error.message}")
            _playbackExceptionChannel.trySend(error)
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            var msg = ""
            repeat(events.size()) {
                msg += "${it}th: event: ${events.get(it)}\n"
            }
//            Timber.tag(TAG_PLAYER).d("====== onEvents ======\n${msg}")
        }
    }

    private fun emitExoPlayerState(playbackState: Int) = applicationScope.launch {
        if(playbackState == Player.STATE_READY) {
            _exoPlayerState.tryEmit(playbackState)
            withContext(Dispatchers.IO) {
                repeat(3) {
                    delay(300)
                    _forceUpdate.tryEmit(Player.STATE_READY + it)
                }
            }
        } else {
            _exoPlayerState.tryEmit(playbackState)
        }
    }

    private suspend fun debugMessage(message: String) = withContext(Dispatchers.Main) {
        if(BuildConfig.DEBUG) {
//            context.showToast(message)
        }
    }
}