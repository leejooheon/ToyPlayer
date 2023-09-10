package com.jooheon.clean_architecture.domain.entity.music

import com.jooheon.clean_architecture.domain.common.Resource
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Song(
    val audioId: Long,
    val displayName: String,
    val title: String,
    val artist: String,
    val artistId: String,
    val album: String,
    val albumId: String,
    val duration: Long,
    val path: String,
    val trackNumber: Int,
    val imageUrl: String,
    var isFavorite: Boolean = false,
    var data: String? = null,
    private val uniqueId: String = UUID.randomUUID().toString(),
): java.io.Serializable {

    companion object {
        val default = Song(
            audioId = -1L,
            displayName = "-",
            title = "-",
            artist = "<unknown>",
            artistId = "",
            album = "-",
            albumId = "-",
            duration = 1L,
            path = "-",
            trackNumber = 1,
            imageUrl = "",
            isFavorite = false,
            data = null,
        )

        val defaultList = listOf(
            default.copy(title = Resource.longStringPlaceholder),
            default.copy(title = Resource.mediumStringPlaceholder),
            default.copy(title = Resource.shortStringPlaceholder),
        )
    }
}