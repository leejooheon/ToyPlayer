package com.jooheon.toyplayer.features.musicplayer.presentation.artist.detail.model

import com.jooheon.toyplayer.domain.entity.music.Artist
import com.jooheon.toyplayer.domain.entity.music.Playlist

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