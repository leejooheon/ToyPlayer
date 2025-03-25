package com.jooheon.toyplayer.domain.model.radio

import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import kotlinx.serialization.Serializable

@Serializable
sealed class RadioType {
    @Serializable data object KBS: RadioType()
    @Serializable data object SBS: RadioType()
    @Serializable data object MBC: RadioType()
    @Serializable data class ETC(val name: String): RadioType()

    fun name() = if(this is ETC) name else this::class.simpleName.defaultEmpty()
}