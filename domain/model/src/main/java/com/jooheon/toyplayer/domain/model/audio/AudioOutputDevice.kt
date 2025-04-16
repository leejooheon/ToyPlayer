package com.jooheon.toyplayer.domain.model.audio

data class AudioOutputDevice(
    val type: Type,
    val name: String,
) {
    enum class Type {
        Speaker, WiredHeadset, Bluetooth, Usb, Hdmi, Unknown
    }
}
