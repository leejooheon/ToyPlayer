package com.jooheon.toyplayer.features.musicservice.ext

import android.media.session.PlaybackState
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline

val Int.isPlaying: Boolean get() = this == PlaybackState.STATE_PLAYING
val Int.isBuffering: Boolean get() = this == PlaybackState.STATE_BUFFERING
val Timeline.mediaItems: List<MediaItem>
    get() = List(windowCount) { getWindow(it, Timeline.Window()).mediaItem }

val Player.mediaItems: List<MediaItem>
    get() = List(mediaItemCount) { getMediaItemAt(it) }

val Player.currentWindow: Timeline.Window?
    get() = if (mediaItemCount == 0) null else currentTimeline.getWindow(currentMediaItemIndex, Timeline.Window())

val Player.mediaItemsIndices: List<Int>
    get() {
        val indices = mutableListOf<Int>()
        var index = currentTimeline.getFirstWindowIndex(shuffleModeEnabled)
        if (index == -1) {
            return emptyList()
        }

        repeat(currentTimeline.windowCount) {
            indices += index
            index = currentTimeline.getNextWindowIndex(index, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
        }

        return indices
    }

fun Player.forceSeekToPrevious() {
    if (hasPreviousMediaItem() || currentPosition > maxSeekToPreviousPosition) {
        seekToPrevious()
    } else if (mediaItemCount > 0) {
        seekTo(mediaItemCount - 1, C.TIME_UNSET)
    }
}

fun Player.forceSeekToNext() {
    if(hasNextMediaItem()) {
        seekToNextMediaItem()
    } else {
        seekTo(0, C.TIME_UNSET)
    }
}

fun Player.playAtIndex(index: Int, duration: Long) {
    seekTo(index, duration)
    playWhenReady = true
    prepare()
}

fun Player.enqueue(
    mediaItem: MediaItem,
    playWhenReady: Boolean
) {
    val index = currentMediaItemIndex + 1
    addMediaItem(index, mediaItem)

    if(playWhenReady) playAtIndex(index, C.TIME_UNSET)
}

fun Player.forceEnqueue(
    mediaItems: List<MediaItem>,
    startIndex: Int,
    startPositionMs: Long,
    playWhenReady: Boolean
) {
    setMediaItems(mediaItems, startIndex, startPositionMs)
    if(playWhenReady) playAtIndex(startIndex, startPositionMs)
}

fun Player.shuffledItems(): List<MediaItem> {
    val mediaItems = when {
        mediaItemCount <= 1 -> mediaItems
        else -> {
            mediaItemsIndices.toMutableList().also {
                if(isPlaying) {
                    it.remove(currentMediaItemIndex)
                    it.shuffle()
                    it.add(0, currentMediaItemIndex)
                } else {
                    it.shuffle()
                }
            }.map {
                getMediaItemAt(it)
            }
        }
    }

    return mediaItems
}
val Player.lastIndex: Int get() = mediaItemCount - 1

val Player.androidPlaybackState: Int
    get() = when (playbackState) {
        Player.STATE_BUFFERING -> if (playWhenReady) PlaybackState.STATE_BUFFERING else PlaybackState.STATE_PAUSED
        Player.STATE_READY -> if (playWhenReady) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED
        Player.STATE_ENDED -> PlaybackState.STATE_STOPPED
        Player.STATE_IDLE -> PlaybackState.STATE_NONE
        else -> PlaybackState.STATE_NONE
    }