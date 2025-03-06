package com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.model

import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Playlist

data class MusicAlbumDetailScreenState(
    val album: Album,
    val playlists: List<Playlist>,
) {
    companion object {
        val default = MusicAlbumDetailScreenState(
            album = Album.default.copy(
                name = "name"
            ),
            playlists = listOf(Playlist.default),
        )
    }
}