package com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model

import com.jooheon.clean_architecture.features.musicservice.data.MusicState

data class MusicPlayerState(
    val musicState: MusicState,
    val duration: Long,
) {
    companion object {
        val default = MusicPlayerState(
            musicState = MusicState(),
            duration = 0L,
        )
    }
}