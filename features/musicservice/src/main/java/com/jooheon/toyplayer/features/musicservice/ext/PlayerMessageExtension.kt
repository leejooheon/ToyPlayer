package com.jooheon.toyplayer.features.musicservice.ext

import android.media.session.PlaybackState
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player

fun Int.playbackState() = when(this) {
    PlaybackState.STATE_STOPPED -> "STATE_STOPPED"
    PlaybackState.STATE_PAUSED -> "STATE_PAUSED"
    PlaybackState.STATE_PLAYING -> "STATE_PLAYING"
    PlaybackState.STATE_FAST_FORWARDING -> "STATE_FAST_FORWARDING"
    PlaybackState.STATE_REWINDING -> "STATE_REWINDING"
    PlaybackState.STATE_BUFFERING -> "STATE_BUFFERING"
    PlaybackState.STATE_ERROR -> "STATE_ERROR"
    PlaybackState.STATE_CONNECTING -> "STATE_CONNECTING"
    PlaybackState.STATE_SKIPPING_TO_PREVIOUS -> "STATE_SKIPPING_TO_PREVIOUS"
    PlaybackState.STATE_SKIPPING_TO_NEXT -> "STATE_SKIPPING_TO_NEXT"
    PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM -> "STATE_SKIPPING_TO_QUEUE_ITEM"
    else -> "STATE_NONE"
}
fun Int.playerState() = when(this) {
    Player.STATE_IDLE -> "STATE_IDLE"
    Player.STATE_BUFFERING -> "STATE_BUFFERING"
    Player.STATE_READY -> "STATE_READY"
    Player.STATE_ENDED -> "STATE_ENDED"
    else -> "STATE_NONE"
}

fun @Player.State Int.toPlaybackState(playWhenReady: Boolean): Int {
    return when(this) {
        Player.STATE_BUFFERING -> if (playWhenReady) PlaybackState.STATE_BUFFERING else PlaybackState.STATE_PAUSED
        Player.STATE_READY -> if (playWhenReady) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED
        Player.STATE_ENDED -> PlaybackState.STATE_STOPPED
        Player.STATE_IDLE -> PlaybackState.STATE_NONE
        else -> PlaybackState.STATE_NONE
    }
}

fun Int.mediaItemTransitionReason() = when(this) {
    Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> "MEDIA_ITEM_TRANSITION_REASON_REPEAT"
    Player.MEDIA_ITEM_TRANSITION_REASON_AUTO, -> "MEDIA_ITEM_TRANSITION_REASON_AUTO"
    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> "MEDIA_ITEM_TRANSITION_REASON_SEEK"
    Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> "MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED"
    else -> throw IllegalStateException("Unknown MediaItemTransitionReason")
}
fun Int.playWhenReadyChangeReason() = when(this) {
    Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST -> "PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST"
    Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS, -> "PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS"
    Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY -> "PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY"
    Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE -> "PLAY_WHEN_READY_CHANGE_REASON_REMOTE"
    Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM -> "PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM"
    Player.PLAY_WHEN_READY_CHANGE_REASON_SUPPRESSED_TOO_LONG -> "PLAY_WHEN_READY_CHANGE_REASON_SUPPRESSED_TOO_LONG"
    else -> throw IllegalStateException("Unknown MediaItemTransitionReason")
}
fun Int.timelineChangeReason() = when(this) {
    Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED -> "TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED"
    Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE, -> "PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS"
    else -> throw IllegalStateException("Unknown MediaItemTransitionReason")
}