package com.jooheon.toyplayer.features.musicservice.equalizer.filter

import kotlin.math.cos
import kotlin.math.sin

internal class BiquadHighPassFilter(
    private val sampleRate: Float,
    private val cutoffFreq: Float = 20f,
    private val q: Float = 0.707f
) {
    private var b0 = 0.0
    private var b1 = 0.0
    private var b2 = 0.0
    private var a1 = 0.0
    private var a2 = 0.0
    private var x1 = 0.0
    private var x2 = 0.0
    private var y1 = 0.0
    private var y2 = 0.0

    init {
        updateCoefficients()
    }

    private fun updateCoefficients() {
        val omega = 2.0 * Math.PI * cutoffFreq / sampleRate
        val sinW = sin(omega)
        val cosW = cos(omega)
        val alpha = sinW / (2.0 * q)

        val a0 = 1.0 + alpha
        b0 = (1.0 + cosW) / 2.0 / a0
        b1 = -(1.0 + cosW) / a0
        b2 = (1.0 + cosW) / 2.0 / a0
        a1 = -2.0 * cosW / a0
        a2 = (1.0 - alpha) / a0
    }

    internal fun processSample(input: Float): Float {
        val x0 = input.toDouble()
        val y0 = b0 * x0 + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2

        x2 = x1
        x1 = x0
        y2 = y1
        y1 = y0

        return y0.toFloat()
    }
}