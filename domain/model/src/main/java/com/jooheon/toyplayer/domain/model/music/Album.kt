package com.jooheon.toyplayer.domain.model.music

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    val name: String,
    val artist: String,
    val artistId: String,
    val imageUrl: String,
    val songs: List<Song>
) {
    companion object {
        val default = Album(
            id = "-1",
            name = "-",
            artist = "<unknown>",
            artistId = "-1",
            imageUrl = "",
            songs = emptyList()
        )
    }
}