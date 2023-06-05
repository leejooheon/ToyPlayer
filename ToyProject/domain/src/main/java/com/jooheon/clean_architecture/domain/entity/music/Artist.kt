package com.jooheon.clean_architecture.domain.entity.music

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val albums: List<Album>
): java.io.Serializable {
    companion object {
        val default = Artist(
            id = "-1",
            name = "-",
            albums = listOf(
                Album.default,
            )
        )
    }
}