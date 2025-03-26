package com.jooheon.toyplayer.domain.model.common.errors

import kotlinx.serialization.Serializable

@Serializable
sealed interface NetworkError: RootError {
    @Serializable
    data object NoInternet : RootError

}
