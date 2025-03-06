package com.jooheon.toyplayer.data.datastore.model

data class PlaybackData(
    val lastPlayedPosition: Long,
    val skipDuration: Long,
    val repeatMode: Int,
    val shuffleMode: Boolean,
)