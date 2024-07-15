package com.jooheon.toyplayer.domain.entity

import kotlinx.serialization.Serializable

@Serializable
sealed class Entity {
    enum class SupportLaunguages(val code: String) {
        AUTO("Auto"),
        ENGLISH("en"),
        KOREAN("ko");
    }

    enum class SupportThemes(val code: String) {
        AUTO("auto"),
        DARK("dark"),
        LIGHT("light"),
        DYNAMIC_DARK("dynamic_dark"),
        DYNAMIC_LIGHT("dynamic_light");
    }
}