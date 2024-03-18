package com.jooheon.toyplayer.domain.entity.music

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
sealed interface MediaId {
    @Serializable
    @SerialName("root")
    data object Root : MediaId

    @Serializable
    @SerialName("all_songs")
    data object AllSongs : MediaId

    @Serializable
    @SerialName("local_songs")
    data object LocalSongs : MediaId
    @Serializable
    @SerialName("stream_songs")
    data object StreamSongs : MediaId
    @Serializable
    @SerialName("asset_songs")
    data object AssetSongs : MediaId

    @Serializable
    @SerialName("album_root")
    data object AlbumRoot : MediaId

    @Serializable
    @SerialName("album")
    data class Album(val id: String) : MediaId

    @Serializable
    @SerialName("playlist_root")
    data object PlaylistRoot : MediaId

    @Serializable
    @SerialName("playlist")
    data class Playlist(val id: String) : MediaId

    @Serializable
    @SerialName("content")
    data class Content(val parent: MediaId, val key: String) : MediaId

    companion object {
        fun String.toMediaIdOrNull(): MediaId? = try {
            Json.decodeFromString(MediaId.serializer(), this)
        } catch (e: SerializationException) {
            null
        }
    }
    fun serialize() = Json.encodeToString(serializer(), this)
}