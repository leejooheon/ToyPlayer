package com.jooheon.toyplayer.domain.model.music

import kotlinx.serialization.Serializable

@Serializable
enum class MusicListType {
    Local, Streaming, Asset, All,;
}