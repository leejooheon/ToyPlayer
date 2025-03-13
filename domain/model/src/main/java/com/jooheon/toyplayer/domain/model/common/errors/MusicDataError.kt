package com.jooheon.toyplayer.domain.model.common.errors

import kotlinx.serialization.Serializable

@Serializable
sealed interface MusicDataError: RootError {
    @Serializable data object Empty: MusicDataError
    @Serializable data class Remote(val cause: String): MusicDataError
    // add something
}
