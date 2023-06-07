package com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.model

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Playlist

data class MusicAlbumDetailScreenState(
    val album: Album,
    val playlists: List<Playlist>,
) {
    companion object {
        val default = MusicAlbumDetailScreenState(
            album = Album.default.copy(
                name = Resource.shortStringPlaceholder
            ),
            playlists = listOf(Playlist.default),
        )
    }
}