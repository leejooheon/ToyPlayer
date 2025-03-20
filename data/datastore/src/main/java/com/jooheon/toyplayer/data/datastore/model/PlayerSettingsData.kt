package com.jooheon.toyplayer.data.datastore.model

data class PlayerSettingsData(
    val repeatMode: Int,
    val shuffleMode: Boolean,
    val volume: Float,
)