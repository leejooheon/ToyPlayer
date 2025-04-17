package com.jooheon.toyplayer.domain.model.audio

data class VisualizerData(
    val fft: List<Float>,
    val sampleRateHz: Int,
) {
    companion object {
        val default = VisualizerData(
            fft = FloatArray(4096 + 2).toList(),
            sampleRateHz = 44100,
        )
    }
}