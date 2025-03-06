package com.jooheon.toyplayer.domain.model.music

enum class SkipForwardBackward {
    FIVE_SECOND,
    TEN_SECOND,
    FIFTEEN_SECOND;

    fun toInteger() = when (this) {
        FIVE_SECOND -> "5"
        TEN_SECOND -> "10"
        FIFTEEN_SECOND -> "15"
    }
}