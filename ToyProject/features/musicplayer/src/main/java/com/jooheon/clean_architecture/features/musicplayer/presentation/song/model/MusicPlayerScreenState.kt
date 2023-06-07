package com.jooheon.clean_architecture.features.musicplayer.presentation.song.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.musicservice.data.MusicState

data class MusicPlayerScreenState(
    val musicState: MusicState,
    val playlists: List<Playlist>,
    val currentDuration: Long,
    val progressBarVisibility: Boolean,
) {
    companion object {
        val default = MusicPlayerScreenState(
            musicState = MusicState(
                playlist = Song.defaultList,
                currentPlayingMusic = Song.default.copy(albumId = "1234")
            ),
            playlists = listOf(Playlist.default),
            currentDuration = 0L,
            progressBarVisibility = true,
        )
    }
}