package com.jooheon.toyplayer.domain.entity.music

import com.jooheon.toyplayer.domain.common.Resource
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    private val audioId: Long,
    val useCache: Boolean,
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
) {
    fun key() = audioId.toString()
    companion object {
        val default = Song(
            audioId = -1L,
            useCache = false,
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
        const val BUNDLE_USE_CACHE = "bundle_use_cache"
        const val BUNDLE_ARTIST_ID = "bundle_artist_id"
        const val BUNDLE_ALBUM_ID = "bundle_album_id"
        const val BUNDLE_DURATION = "bundle_duration"
        const val BUNDLE_IS_FAVORITE = "bundle_is_favorite"
        const val BUNDLE_DATA = "bundle_data"
        const val BUNDLE_PATH = "bundle_path"
    }
}