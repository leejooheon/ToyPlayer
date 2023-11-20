package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.artist.detail.model

import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song

data class MusicArtistDetailScreenState(
    val artist: Artist,
    val playlists: List<Playlist>,
) {
    companion object {
        val default = MusicArtistDetailScreenState(
            artist = Artist.default.copy(
                name = "Artist name",
            ),
            playlists = listOf(Playlist.default)
        )
    }
}