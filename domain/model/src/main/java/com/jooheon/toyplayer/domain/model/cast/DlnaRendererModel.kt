package com.jooheon.toyplayer.domain.model.cast

import kotlinx.serialization.Serializable

@Serializable
data class DlnaRendererModel(
    val name: String,
    val udn: String, // Unique Device Name
) {
    companion object {
        val default = DlnaRendererModel(
            name = "",
            udn = "",
        )
    }
}