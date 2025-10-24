package com.jooheon.toyplayer.domain.model.cast

data class DlnaStateModel(
    val state: String, // TRANSITIONING, PAUSED_PLAYBACK, PLAYING, STOPPED
    val duration: Long, // 0:04:27
    val position: Long,
) {
    val isPlaying: Boolean
        get() = state == "PLAYING"
    val isBuffering: Boolean
        get() = state == "TRANSITIONING"

    companion object Companion {
        val default = DlnaStateModel(
            state = "STOPPED",
            duration = 0L,
            position = 0L,
        )
    }

    override fun toString(): String {
        return "$state\nDuration: $duration\nPosition: $position"
    }

    fun toPlaybackState(): Int {
        return when (state) {
            "PLAYING" -> 3 // STATE_PLAYING
            "PAUSED_PLAYBACK" -> 2 // STATE_PAUSED
            "TRANSITIONING" -> 6 // STATE_BUFFERING
            "STOPPED" -> 1 // STATE_STOPPED
            else -> 0 // STATE_NONE
        }
    }
    fun toPlayerState(ready: Boolean): Int {
        return if(!ready) 4 // Player.STATE_ENDED
        else when (state) {
            "PLAYING" -> 3 //Player.STATE_READY
            "PAUSED_PLAYBACK" -> 3 //Player.STATE_READY
            "TRANSITIONING" -> 2 // Player.STATE_BUFFERING
            "STOPPED" -> 1 // Player.STATE_IDLE
            else -> 4 // Player.STATE_ENDED
        }
    }
}