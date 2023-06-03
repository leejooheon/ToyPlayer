package com.jooheon.clean_architecture.domain.entity.music

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    val name: String,
    val artist: String,
    val artistId: String,
    val songs: List<Song>
): java.io.Serializable {
    companion object {
        val default = Album(
            id = "album-id",
            name = "album-name",
            artist = "artist-name",
            artistId = "artist-id",
            songs = listOf(Song.default, Song.default)
        )
    }
}