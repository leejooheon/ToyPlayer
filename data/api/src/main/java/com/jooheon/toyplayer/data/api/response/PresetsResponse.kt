package com.jooheon.toyplayer.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class PresetsResponse(
    val id: Int,
    val name : String,
    val gains : List<Float>,
)