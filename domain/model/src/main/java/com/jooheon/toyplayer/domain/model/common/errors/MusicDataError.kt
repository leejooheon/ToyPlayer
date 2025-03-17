package com.jooheon.toyplayer.domain.model.common.errors

import kotlinx.serialization.Serializable

@Serializable
sealed interface MusicDataError: RootError {
    @Serializable data object Empty: MusicDataError
    @Serializable data class Remote(val cause: String): MusicDataError
    @Serializable data class InvalidData(val message: String): MusicDataError
    // add something

    companion object {
        val key = MusicDataError::class.java.simpleName
        const val KEY_CODE = "code"
        const val KEY_MESSAGE = "message"
    }
}
