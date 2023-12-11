package com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model

import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.musicservice.data.MusicState

data class MusicPlayerState(
    val musicState: MusicState,
    val playingQueue: List<Song>,
    val repeatMode: RepeatMode,
    val shuffleMode: ShuffleMode,
) {
    companion object {
        val default = MusicPlayerState(
            musicState = MusicState(),
            playingQueue = emptyList(),
            repeatMode = RepeatMode.REPEAT_OFF,
            shuffleMode = ShuffleMode.NONE
        )
    }
}