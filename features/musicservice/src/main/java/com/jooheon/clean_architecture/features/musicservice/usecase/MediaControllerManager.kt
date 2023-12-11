package com.jooheon.clean_architecture.features.musicservice.usecase

import android.content.ComponentName
import android.content.Context
import android.media.session.PlaybackState
import androidx.core.content.ContextCompat
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.clean_architecture.domain.common.extension.defaultFalse
import com.jooheon.clean_architecture.domain.common.extension.defaultZero
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.ext.androidPlaybackState
import com.jooheon.clean_architecture.features.musicservice.ext.currentWindow
import com.jooheon.clean_architecture.features.musicservice.ext.mediaItemTransitionReason
import com.jooheon.clean_architecture.features.musicservice.ext.mediaItems
import com.jooheon.clean_architecture.features.musicservice.ext.playWhenReadyChangeReason
import com.jooheon.clean_architecture.features.musicservice.ext.mediaItemsIndices
import com.jooheon.clean_architecture.features.musicservice.ext.playbackState
import com.jooheon.clean_architecture.features.musicservice.ext.playerState
import com.jooheon.clean_architecture.features.musicservice.ext.timelineChangeReason
import kotlinx.coroutines.*
import timber.log.Timber

class MediaControllerManager(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val musicStateHolder: MusicStateHolder
) : Player.Listener {
    private val TAG = MusicService::class.java.simpleName + "@" + MediaControllerManager::class.java.simpleName

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private var durationJob: Job? = null

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        Timber.tag(TAG).d("onMediaItemTransition: ${reason.mediaItemTransitionReason()}")
        musicStateHolder.onCurrentWindowChanged(controller?.currentWindow)
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        super.onTimelineChanged(timeline, reason)
        Timber.tag(TAG).d("onTimelineChanged: ${reason.timelineChangeReason()}")

        if(reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
            musicStateHolder.onMediaItemsChanged(controller?.mediaItems ?: emptyList())
            Timber.tag("shuffle_test").d("onTimelineChanged: ${controller?.shuffleModeEnabled}, ${controller?.mediaItemsIndices}")
        }
        musicStateHolder.onCurrentWindowChanged(controller?.currentWindow)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Timber.tag(TAG).d( "onIsPlayingChanged - $isPlaying")
        musicStateHolder.onIsPlayingChanged(isPlaying)
        if(isPlaying) {
            Timber.tag("shuffle_test").d("onIsPlayingChanged: ${controller?.mediaItemsIndices}")
            collectDuration()
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        val androidPlaybackState = controller?.androidPlaybackState ?: PlaybackState.STATE_NONE
        Timber.tag(TAG).d("onPlaybackStateChanged: playbackState: ${playbackState.playerState()}, androidPlaybackState: ${androidPlaybackState.playbackState()}")

        musicStateHolder.onPlaybackStateChanged(androidPlaybackState)
        musicStateHolder.onPlayerStateChanged(playbackState)
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        Timber.tag(TAG).d("onMediaMetadataChanged: ${mediaMetadata.title}")
    }

    override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onPlaylistMetadataChanged(mediaMetadata)
        Timber.tag(TAG).d("onPlaylistMetadataChanged: mediaMetadata: ${mediaMetadata.title}")
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        Timber.tag(TAG).d("onPlayWhenReadyChanged: playWhenReady: ${playWhenReady}, reason: ${reason.playWhenReadyChangeReason()}")
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)
        Timber.tag(TAG).d("onRepeatModeChanged: $repeatMode")
        musicStateHolder.onRepeatModeChanged(repeatMode)
    }

    @UnstableApi
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        Timber.tag(TAG).d("onShuffleModeEnabledChanged: $shuffleModeEnabled")
        musicStateHolder.onShuffleModeChanged(shuffleModeEnabled)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Timber.tag(TAG).d("onPlayerError: ${error.message}")
        musicStateHolder.onPlaybackErrorChannel(error)
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        var msg = ""
        repeat(events.size()) {
            msg += "${it}th: event: ${events.get(it)}\n"
        }
//        Timber.tag(TAG_PLAYER).d("====== onEvents ======\n${msg}")
    }

    @Synchronized
    private fun collectDuration() {
        durationJob?.cancel()
        durationJob = applicationScope.launch(Dispatchers.Main) {
            while (controller?.isPlaying.defaultFalse() && isActive) {
                val duration = if (controller?.duration != C.TIME_UNSET) {
                    controller?.currentPosition.defaultZero()
                } else {
                    0L
                }
//                Timber.tag(TAG).d("snapTo: ${duration}, job: $this")
                musicStateHolder.onCurrentDurationChanged(duration)

                withContext(Dispatchers.IO) {
                    delay(500)
                }
            }
        }
    }

    fun init() {
        controllerFuture = MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, MusicService::class.java))
        ).buildAsync()

        controllerFuture.addListener({
            controller?.addListener(this)
        }, ContextCompat.getMainExecutor(context))
    }

    fun release() {
        MediaController.releaseFuture(controllerFuture)
    }
}