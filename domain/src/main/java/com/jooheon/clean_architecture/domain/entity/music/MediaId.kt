package com.jooheon.clean_architecture.domain.entity.music

import kotlinx.serialization.SerialName
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
    @SerialName("playlist")
    data object Playlist : MediaId
}