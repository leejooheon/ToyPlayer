package com.jooheon.clean_architecture.domain.entity.music

data class MediaFolder(
    val title: String,
    val mediaId: MediaId,
    val mediaType: Int,
)