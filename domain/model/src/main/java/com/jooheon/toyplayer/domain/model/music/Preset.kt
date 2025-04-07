package com.jooheon.toyplayer.domain.model.music

import kotlinx.serialization.Serializable

@Serializable
data class Preset(
    val id: Int,
    val name : String,
    val gains : List<Float>,
    val type: EqualizerType,
    val isCustom: Boolean,
) {
    fun isSavedPreset(): Boolean = isCustom && name != CUSTOM_PRESET_NAME
    fun isCustomPreset(): Boolean = isCustom && name == CUSTOM_PRESET_NAME
    fun isFlat(): Boolean = gains.all { it == 0f }

    companion object {
        const val CUSTOM_PRESET_NAME = " "

        val default = Preset(
            id = -1,
            name = "",
            gains = List(EqualizerType.default.frequencies().size) { 0f },
            type = EqualizerType.default,
            isCustom = false,
        )

        val preview = Preset(
            id = -1,
            name = "preset",
            gains = listOf(1f, 2f, 3f, 4f, 5f),
            type = EqualizerType.default,
            isCustom = true,
        )
    }
}