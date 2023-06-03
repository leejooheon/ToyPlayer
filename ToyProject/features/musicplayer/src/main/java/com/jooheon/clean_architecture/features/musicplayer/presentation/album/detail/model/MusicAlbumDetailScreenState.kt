package com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.model

import com.jooheon.clean_architecture.domain.entity.music.Album

data class MusicAlbumDetailScreenState(
    val album: Album
) {
    companion object {
        val default = MusicAlbumDetailScreenState(
            album = Album.default
        )
    }
}