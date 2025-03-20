package com.jooheon.toyplayer.data.datastore.model

data class PlaybackData(
    val lastEnqueuedPlaylistName: String,
    val lastPlayedMediaId: String,
    val repeatMode: Int,
    val shuffleMode: Boolean,
)