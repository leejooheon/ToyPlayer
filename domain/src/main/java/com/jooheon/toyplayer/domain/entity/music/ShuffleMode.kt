package com.jooheon.toyplayer.domain.entity.music

enum class ShuffleMode {
    SHUFFLE,
    NONE,;

    companion object {
        fun getByValue(flag: Boolean) = if(flag) SHUFFLE else NONE
    }
}
