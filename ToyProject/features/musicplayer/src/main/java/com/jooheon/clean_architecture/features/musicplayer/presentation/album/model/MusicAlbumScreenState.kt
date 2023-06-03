package com.jooheon.clean_architecture.features.musicplayer.presentation.album.model

import com.jooheon.clean_architecture.domain.entity.music.Album

data class MusicAlbumScreenState(
    val albums: List<Album>,
) {
    companion object {
        val default = MusicAlbumScreenState(
            albums = listOf(Album.default.copy(id = "selected"), Album.default),
        )
    }
}