package com.jooheon.toyplayer.data.datastore.model

data class PlaybackData(
    val playlistId: Int,
    val lastPlayedMediaId: String,
    val repeatMode: Int,
    val shuffleMode: Boolean,
)