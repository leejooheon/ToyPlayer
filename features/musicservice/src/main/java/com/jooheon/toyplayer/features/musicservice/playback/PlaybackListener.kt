package com.jooheon.toyplayer.features.musicservice.playback

import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.features.musicservice.MusicService
import com.jooheon.toyplayer.features.musicservice.ext.mediaItemTransitionReason
import com.jooheon.toyplayer.features.musicservice.ext.mediaItems
import com.jooheon.toyplayer.features.musicservice.ext.playWhenReadyChangeReason
import com.jooheon.toyplayer.features.musicservice.ext.playerState
import com.jooheon.toyplayer.features.musicservice.ext.timelineChangeReason
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlaybackListener(
    private val musicStateHolder: MusicStateHolder
) : Player.Listener {
    private val TAG = MusicService::class.java.simpleName + "@" + PlaybackListener::class.java.simpleName

    internal fun observeDuration(scope: CoroutineScope, player: Player) = scope.launch {
        combine(
            musicStateHolder.mediaItem,
            musicStateHolder.isPlaying
        ) { _, isPlaying ->
            isPlaying
        }.collectLatest { isPlaying ->
            if(isPlaying) {
                withContext(Dispatchers.Main) {
                    pollCurrentDuration(player).collect {
                        value -> musicStateHolder.onCurrentDurationChanged(value)
                    }
                }
            }
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        Timber.tag(TAG).d("onMediaItemTransition: ${reason.mediaItemTransitionReason()}")
        musicStateHolder.onMediaItemChanged(mediaItem ?: MediaItem.EMPTY)
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        super.onTimelineChanged(timeline, reason)
        Timber.tag(TAG).d("onTimelineChanged: ${reason.timelineChangeReason()}")

        if(reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
            musicStateHolder.onMediaItemsChanged(timeline.mediaItems)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Timber.tag(TAG).d( "onIsPlayingChanged - $isPlaying")
        musicStateHolder.onIsPlayingChanged(isPlaying)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        Timber.tag(TAG).d("onPlaybackStateChanged: playbackState: ${playbackState.playerState()}")
        musicStateHolder.onPlaybackStateChanged(playbackState)
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
        musicStateHolder.onPlaybackException(error)
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        musicStateHolder.onPositionDiscontinuity(oldPosition.positionMs, newPosition.positionMs)
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        var msg = ""
        repeat(events.size()) {
            msg += "${it}th: event: ${events.get(it)}\n"
        }
//        Timber.tag(TAG_PLAYER).d("====== onEvents ======\n${msg}")
    }

    private fun pollCurrentDuration(player: Player) = flow {
        while (player.isPlaying && (player.currentPosition + POLL_INTERVAL_MSEC <= player.duration)) {
            emit(player.currentPosition)
            delay(POLL_INTERVAL_MSEC)
        }
    }.conflate()

    companion object {
        private const val POLL_INTERVAL_MSEC = 500L
    }
}