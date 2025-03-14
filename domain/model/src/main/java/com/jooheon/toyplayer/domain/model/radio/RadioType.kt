package com.jooheon.toyplayer.domain.model.radio

import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import kotlinx.serialization.Serializable

@Serializable
sealed class RadioType {
    @Serializable data object Kbs: RadioType()
    @Serializable data object Sbs: RadioType()
    @Serializable data object Mbc: RadioType()
    @Serializable data class Etc(val name: String): RadioType()

    fun name() = if(this is Etc) name else this::class.simpleName.defaultEmpty()
}