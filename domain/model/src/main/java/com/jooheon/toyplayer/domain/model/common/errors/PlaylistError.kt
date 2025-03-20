package com.jooheon.toyplayer.domain.model.common.errors

import kotlinx.serialization.Serializable

@Serializable
sealed interface PlaylistError: RootError {
    @Serializable
    data object UnKnown : PlaylistError
    @Serializable
    data class NotFound(val id: Int) : PlaylistError
    @Serializable
    data object DuplicatedName : PlaylistError
}
