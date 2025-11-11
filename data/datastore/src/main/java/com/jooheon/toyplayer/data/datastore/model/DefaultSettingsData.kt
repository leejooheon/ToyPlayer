package com.jooheon.toyplayer.data.datastore.model


data class DefaultSettingsData(
    val lastEnqueuedPlaylistName: String,
    val lastPlayedMediaId: String,
    val isDarkTheme: Boolean,
    val audioUsage: Int,
)