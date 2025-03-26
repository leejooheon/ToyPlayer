package com.jooheon.toyplayer.domain.model.common.errors

import kotlinx.serialization.Serializable

@Serializable
sealed interface PlaybackError: RootError {
    @Serializable data class UnKnown(val code: Int): PlaybackError
    @Serializable data object Behind: PlaybackError
}