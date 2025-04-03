package com.jooheon.toyplayer.data.datastore.model

data class PlayerSettingsData(
    val repeatMode: Int,
    val shuffleMode: Boolean,
    val preset: String,
    val volume: Float,
    val channelBalance: Float,
    val bassBoost: Int,
)