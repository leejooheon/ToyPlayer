package com.jooheon.toyplayer.features.settings.presentation.equalizer.ext

import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Preset

fun Preset.label(): UiText {
    val uiText = if(isCustom) {
        if(isCustomPreset()) UiText.StringResource(Strings.equalizer_user_preset)
        else UiText.DynamicString(this.name)
    } else {
        when (this.name) {
            "Flat" -> UiText.DynamicString("Flat")
            "Classic" -> UiText.DynamicString("Classic")
            "Dance" -> UiText.DynamicString("Dance")
            "Live" -> UiText.DynamicString("Live")
            "Pop" -> UiText.DynamicString("Pop")
            "Rock" -> UiText.DynamicString("Rock")
            "Techno" -> UiText.DynamicString("Techno")
            else -> UiText.DynamicString(this.name)
        }
    }

    return uiText
}