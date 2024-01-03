package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model

import com.jooheon.toyplayer.domain.entity.music.RepeatMode
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.musicservice.data.MusicState

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