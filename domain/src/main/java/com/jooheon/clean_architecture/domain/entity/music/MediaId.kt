package com.jooheon.clean_architecture.domain.entity.music

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface MediaId {
    @Serializable
    @SerialName("root")
    data object Root : MediaId

    @Serializable
    @SerialName("automotive_root")
    data object AutomotiveRoot : MediaId

    @Serializable
    @SerialName("all_songs")
    data object AllSongs : MediaId

    @Serializable
    @SerialName("album")
    data object Album : MediaId

    @Serializable
    @SerialName("playlist")
    data object Playlist : MediaId
}