package com.jooheon.toyplayer.domain.model.music

data class MediaFolder(
    val title: String,
    val mediaId: MediaId,
    val mediaType: Int,
)