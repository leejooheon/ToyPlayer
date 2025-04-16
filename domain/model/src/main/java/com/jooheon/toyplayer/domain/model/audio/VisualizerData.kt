package com.jooheon.toyplayer.domain.model.audio

data class VisualizerData(
    val fft: List<Float>,
    val sampleRateHz: Int,
    val channel: Int
) {
    companion object {
        val default = VisualizerData(
            fft = emptyList(),
            sampleRateHz = -1,
            channel = -1
        )
    }
}