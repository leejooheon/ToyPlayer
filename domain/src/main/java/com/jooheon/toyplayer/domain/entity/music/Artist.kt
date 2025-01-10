package com.jooheon.toyplayer.domain.entity.music

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val albums: List<Album>
) {
    companion object {
        val default = Artist(
            id = "-1",
            name = "<unknown>",
            albums = emptyList(),
        )
    }
}