package com.jooheon.clean_architecture.domain.entity.music

import com.jooheon.clean_architecture.domain.common.Resource
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Song(
//    private val uniqueId: String = UUID.randomUUID().toString(),
    private val audioId: Long,
    val audioType: AudioType,
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
): java.io.Serializable {
    fun key() = audioId.toString()
    companion object {
        val default = Song(
            audioId = -1L,
            audioType = AudioType.SONG,
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