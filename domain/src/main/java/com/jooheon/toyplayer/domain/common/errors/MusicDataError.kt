package com.jooheon.toyplayer.domain.common.errors

import kotlinx.serialization.Serializable

@Serializable
sealed interface MusicDataError: RootError {
    @Serializable data object Empty: MusicDataError
    // add something
}
