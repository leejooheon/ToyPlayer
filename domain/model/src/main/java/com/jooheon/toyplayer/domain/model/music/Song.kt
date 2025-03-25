package com.jooheon.toyplayer.domain.model.music

import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.radio.RadioData.Companion.toRadioDataOrNull
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val audioId: Long,
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
    fun radioDataOrNull() = data.defaultEmpty().toRadioDataOrNull()
    fun key(): String = radioDataOrNull()?.serialize() ?: "${audioId}_${trackNumber}"

    companion object {
        val default = Song(
            audioId = -1L,
            useCache = false,
            displayName = "",
            title = "",
            artist = "<unknown>",
            artistId = "",
            album = "",
            albumId = "",
            duration = 1L,
            path = "",
            trackNumber = 1,
            imageUrl = "",
            isFavorite = false,
            data = null,
        )
        val preview = Song(
            audioId = -1L,
            useCache = false,
            displayName = "preview-song-displayName",
            title = "preview-song-title",
            artist = "preview-song-artist",
            artistId = "",
            album = "-",
            albumId = "-",
            duration = 1L,
            path = "-",
            trackNumber = 1,
            imageUrl = "",
            isFavorite = true,
            data = null,
        )

        const val BUNDLE_AUDIO_ID = "bundle_audio_id"
        const val BUNDLE_USE_CACHE = "bundle_use_cache"
        const val BUNDLE_ARTIST_ID = "bundle_artist_id"
        const val BUNDLE_ALBUM_ID = "bundle_album_id"
        const val BUNDLE_DURATION = "bundle_duration"
        const val BUNDLE_IS_FAVORITE = "bundle_is_favorite"
        const val BUNDLE_DATA = "bundle_data"
        const val BUNDLE_PATH = "bundle_path"
    }
}