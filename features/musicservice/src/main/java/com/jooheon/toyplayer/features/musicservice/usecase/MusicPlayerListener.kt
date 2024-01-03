package com.jooheon.toyplayer.features.musicservice.usecase

import android.media.session.PlaybackState
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.common.extension.defaultZero
import com.jooheon.toyplayer.features.musicservice.MusicService
import com.jooheon.toyplayer.features.musicservice.ext.androidPlaybackState
import com.jooheon.toyplayer.features.musicservice.ext.currentWindow
import com.jooheon.toyplayer.features.musicservice.ext.mediaItemTransitionReason
import com.jooheon.toyplayer.features.musicservice.ext.mediaItems
import com.jooheon.toyplayer.features.musicservice.ext.playWhenReadyChangeReason
import com.jooheon.toyplayer.features.musicservice.ext.mediaItemsIndices
import com.jooheon.toyplayer.features.musicservice.ext.playbackState
import com.jooheon.toyplayer.features.musicservice.ext.playerState
import com.jooheon.toyplayer.features.musicservice.ext.timelineChangeReason
import kotlinx.coroutines.*
import timber.log.Timber

class MusicPlayerListener(
    private val applicationScope: CoroutineScope,
    private val musicStateHolder: MusicStateHolder
) : Player.Listener {
    private val TAG = MusicService::class.java.simpleName + "@" + MusicPlayerListener::class.java.simpleName
    private var player: Player? = null
    private var durationJob: Job? = null

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        Timber.tag(TAG).d("onMediaItemTransition: ${reason.mediaItemTransitionReason()}")
        musicStateHolder.onCurrentWindowChanged(player?.currentWindow)
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        super.onTimelineChanged(timeline, reason)
        Timber.tag(TAG).d("onTimelineChanged: ${reason.timelineChangeReason()}")

        if(reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
            musicStateHolder.onMediaItemsChanged(player?.mediaItems ?: emptyList())
            Timber.tag("shuffle_test").d("onTimelineChanged: ${player?.shuffleModeEnabled}, ${player?.mediaItemsIndices}")
        }
        musicStateHolder.onCurrentWindowChanged(player?.currentWindow)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Timber.tag(TAG).d( "onIsPlayingChanged - $isPlaying")
        musicStateHolder.onIsPlayingChanged(isPlaying)
        if(isPlaying) {
            Timber.tag("shuffle_test").d("onIsPlayingChanged: ${player?.mediaItemsIndices}")
            collectDuration()
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        val androidPlaybackState = player?.androidPlaybackState ?: PlaybackState.STATE_NONE
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
        musicStateHolder.onPlayWhenReadyChanged(playWhenReady)
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
            while (player?.isPlaying.defaultFalse() && isActive) {
                val duration = if (player?.duration != C.TIME_UNSET) {
                    player?.currentPosition.defaultZero()
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

    fun setPlayer(player: Player) {
        this.player = player.apply {
            removeListener(this@MusicPlayerListener)
            addListener(this@MusicPlayerListener)
        }
    }

    fun release() {
        player?.removeListener(this)
        player = null
    }
}