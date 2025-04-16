package com.jooheon.toyplayer.domain.model.common.errors

import kotlinx.serialization.Serializable

@Serializable
sealed interface PlaybackDataError: RootError {
    @Serializable data object Empty: PlaybackDataError
    @Serializable data class Remote(val cause: String): PlaybackDataError
    @Serializable data class InvalidData(val message: String): PlaybackDataError

    @Serializable
    data class PlaylistNotFound(val id: Int) : PlaybackDataError
    @Serializable
    data object PlaylistDuplicatedName : PlaybackDataError

    companion object {
        val key = PlaybackDataError::class.java.simpleName
        const val KEY_MESSAGE = "message"
    }
}
