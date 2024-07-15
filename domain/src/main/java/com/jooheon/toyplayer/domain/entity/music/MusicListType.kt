package com.jooheon.toyplayer.domain.entity.music

import kotlinx.serialization.Serializable

@Serializable
enum class MusicListType {
    Local, Streaming, Asset, All,;
}