package com.jooheon.clean_architecture.domain.entity.music

import com.jooheon.clean_architecture.domain.common.Resource
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    val name: String,
    val artist: String,
    val artistId: String,
    val imageUrl: String,
    val songs: List<Song>
): java.io.Serializable {
    companion object {
        val default = Album(
            id = "-1",
            name = "-",
            artist = "<unknown>",
            artistId = "-1",
            imageUrl = "",
            songs = emptyList()
        )

        val defaultList = listOf(
            default.copy(name = Resource.longStringPlaceholder),
            default.copy(name = Resource.mediumStringPlaceholder),
            default.copy(name = Resource.shortStringPlaceholder),
        )
    }
}