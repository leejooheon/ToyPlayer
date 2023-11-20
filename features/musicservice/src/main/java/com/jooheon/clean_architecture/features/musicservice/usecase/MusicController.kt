package com.jooheon.clean_architecture.features.musicservice.usecase

import android.content.ComponentName
import android.content.Context
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.ext.currentWindow
import com.jooheon.clean_architecture.features.musicservice.ext.mediaItemTransitionReason
import com.jooheon.clean_architecture.features.musicservice.ext.mediaItemsIndices
import com.jooheon.clean_architecture.features.musicservice.ext.playWhenReadyChangeReason
import com.jooheon.clean_architecture.features.musicservice.ext.playerState
import com.jooheon.clean_architecture.features.musicservice.ext.timelineChangeReason
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber

class MusicController(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val exoPlayer: ExoPlayer,
) : IMusicController {
    private val TAG = MusicService::class.java.simpleName + "@" +  MusicController::class.java.simpleName + "@Main"
    private val TAG_PLAYER = MusicService::class.java.simpleName + "@" + "PlayerListener"

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>

    private val _playerInitialized = MutableStateFlow(false)
    val playerInitialized: StateFlow<Boolean> = _playerInitialized

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems = _mediaItems.asStateFlow()

    private val _currentWindow = MutableStateFlow(exoPlayer.currentWindow)
    val currentWindow = _currentWindow.asStateFlow()

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

    private val _playbackExceptionChannel = Channel<PlaybackException>()
    val playbackExceptionChannel = _playbackExceptionChannel.receiveAsFlow()

    private var durationFromPlayerJob: Job? = null

    init {
        initMediaControllerFuture()
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
                seekToDefaultPosition(mediaItemCount - 1)
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
                seekToDefaultPosition(0)
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
                this@MusicController.play(
                    index = index,
                    seekTo = C.TIME_UNSET,
                    playWhenReady = playWhenReady
                )
            }
        }
    }
    override suspend fun removeMeidaItems(
        mediaItemsIndices: List<Int>
    ) = withContext(Dispatchers.Main) {
        mediaItemsIndices.forEach { exoPlayer.removeMediaItem(it) }
    }

    override fun releaseMediaBrowser() {
        MediaBrowser.releaseFuture(browserFuture)
    }

    @UnstableApi
    private suspend fun maybeShufflePlaylist() = withContext(Dispatchers.Main) {
        val indices = exoPlayer.mediaItemsIndices.toMutableList()
        if(indices.isEmpty()) return@withContext

        if(exoPlayer.shuffleModeEnabled) {
            indices.remove(exoPlayer.currentMediaItemIndex)
            indices.shuffle()
            indices.add(0, exoPlayer.currentMediaItemIndex)
        }
        exoPlayer.setShuffleOrder(
            ShuffleOrder.DefaultShuffleOrder(indices.toIntArray(), System.currentTimeMillis())
        )
    }

    private suspend fun currentTimelineMediaItems(): List<MediaItem> = withContext(Dispatchers.Main) {
        val indices = exoPlayer.mediaItemsIndices.toMutableList()
        Timber.tag(TAG).d("currentTimelineMediaItems: ${shuffleMode.value}, shuffled: $indices")
        return@withContext indices.map { exoPlayer.getMediaItemAt(it) }
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

    private fun initMediaControllerFuture() {
        browserFuture = MediaBrowser.Builder(
            context, SessionToken(context, ComponentName(context, MusicService::class.java))
        ).buildAsync()

        browserFuture.addListener({
            initExoPlayer()
            _playerInitialized.tryEmit(true)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun initExoPlayer() {
        exoPlayer.apply {
            addListener(exoPlayerListener)
        }.run {
            emitExoPlayerState(playbackState)
        }
    }

    private val exoPlayerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            Timber.tag(TAG_PLAYER).d("onMediaItemTransition: ${reason.mediaItemTransitionReason()}")

            applicationScope.launch(Dispatchers.Main) {
                changePlaybackSpeed(1f)
                _currentWindow.tryEmit(exoPlayer.currentWindow)
            }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            Timber.tag(TAG_PLAYER).d("onTimelineChanged: ${reason.timelineChangeReason()}")

            applicationScope.launch(Dispatchers.Main) {
                if(reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                    _mediaItems.tryEmit(currentTimelineMediaItems())
                }

                _currentWindow.tryEmit(exoPlayer.currentWindow)
            }
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
            Timber.tag(TAG_PLAYER).d("onRepeatModeChanged: $repeatMode")
        }

        @UnstableApi
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            applicationScope.launch {
                maybeShufflePlaylist()
                _shuffleMode.tryEmit(shuffleModeEnabled)
            }
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
        _exoPlayerState.tryEmit(playbackState)
    }

    private suspend fun debugMessage(message: String) = withContext(Dispatchers.Main) {
        if(BuildConfig.DEBUG) {
//            context.showToast(message)
        }

        Timber.tag(TAG).d(message)
    }
}