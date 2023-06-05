package com.jooheon.clean_architecture.features.musicplayer.presentation.player.model

import com.jooheon.clean_architecture.domain.entity.music.PlaylistType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.musicservice.data.MusicState

data class MusicPlayerScreenState(
    val musicState: MusicState,
    val currentDuration: Long,
    val progressBarVisibility: Boolean,
) {
    companion object {
        val default = MusicPlayerScreenState(
            musicState = MusicState(
                playlist = listOf(Song.default, Song.default,),
                currentPlayingMusic = Song.default.copy(albumId = "1234")
            ),
            currentDuration = 0L,
            progressBarVisibility = true,
        )
    }
}