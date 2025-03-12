package com.jooheon.toyplayer.domain.model

data class RadioRawData(
    val channelName: String,
    val channelCode: String,
    val channelSubCode: String? = null,
)