package com.jooheon.toyplayer.domain.model.radio

import kotlinx.serialization.Serializable

@Serializable
sealed class RadioType {
    @Serializable data object KBS: RadioType()
    @Serializable data object SBS: RadioType()
    @Serializable data object MBC: RadioType()
    @Serializable data class ETC(val name: String): RadioType()

    fun name(): String = when(this) {
        is KBS -> "KBS"
        is SBS -> "SBS"
        is MBC -> "MBC"
        is ETC -> this.name
    }
}