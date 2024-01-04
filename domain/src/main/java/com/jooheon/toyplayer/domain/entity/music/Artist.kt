package com.jooheon.toyplayer.domain.entity.music

import com.jooheon.toyplayer.domain.common.Resource
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
            name = "<unknown>",
            albums = emptyList(),
        )

        val defaultList = listOf(
            default.copy(name = Resource.longStringPlaceholder),
            default.copy(name = Resource.mediumStringPlaceholder),
            default.copy(name = Resource.shortStringPlaceholder),
        )
    }
}