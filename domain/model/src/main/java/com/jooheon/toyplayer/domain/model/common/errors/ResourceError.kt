package com.jooheon.toyplayer.domain.model.common.errors

import kotlinx.serialization.Serializable

@Serializable
sealed interface ResourceError: RootError {
    @Serializable data class Glide(val cause: String): ResourceError
    @Serializable data class Unknown(val cause: String): ResourceError
}