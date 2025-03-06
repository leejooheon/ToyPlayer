package com.jooheon.toyplayer.domain.model.music

enum class ShuffleMode {
    SHUFFLE,
    NONE,;

    companion object {
        fun getByValue(flag: Boolean) = if(flag) SHUFFLE else NONE
    }
}
