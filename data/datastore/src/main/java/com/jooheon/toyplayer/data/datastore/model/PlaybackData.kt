package com.jooheon.toyplayer.data.datastore.model

import com.jooheon.toyplayer.domain.entity.music.SkipForwardBackward

data class PlaybackData(
    val lastPlayedPosition: Long,
    val skipDuration: Long,
    val repeatMode: Int,
    val shuffleMode: Boolean,
)