package com.jooheon.toyplayer.domain.model.music

import kotlinx.serialization.SerialName
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
sealed interface MediaId {

    @Serializable
    @SerialName("root")
    data object Root : MediaId

    @Serializable
    @SerialName("all_songs")
    data object AllSongs : MediaId

    @Serializable
    @SerialName("album_root")
    data object AlbumRoot : MediaId
    @Serializable
    @SerialName("album")
    data class Album(val id: String) : MediaId

    @Serializable
    @SerialName("artist_root")
    data object ArtistRoot : MediaId
    @Serializable
    @SerialName("artist")
    data class Artist(val id: String) : MediaId

    @Serializable
    @SerialName("playlist_root")
    data object PlaylistRoot : MediaId
    @Serializable
    @SerialName("playlist")
    data class Playlist(val id: Int) : MediaId

    @Serializable
    @SerialName("playback")
    data class Playback(
        val parentId: String,
        val id: String,
    ) : MediaId

    @Serializable
    sealed interface InternalMediaId: MediaId {
        @Serializable
        @SerialName("local_songs")
        data object LocalSongs : InternalMediaId
        @Serializable
        @SerialName("stream_songs")
        data object StreamSongs : InternalMediaId
        @Serializable
        @SerialName("asset_songs")
        data object AssetSongs : InternalMediaId
        @Serializable
        @SerialName("radio_songs")
        data object RadioSongs : InternalMediaId
    }

    companion object {
        fun String.toMediaIdOrNull(): MediaId? = try {
            Json.decodeFromString(MediaId.serializer(), this)
        } catch (e: SerializationException) {
            null
        }
    }
    fun serialize() = Json.encodeToString(serializer(), this)
}