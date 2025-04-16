package com.jooheon.toyplayer.features.musicservice.audio.equalizer

import kotlin.math.*

// https://secure.aes.org/forum/pubs/conventions/?elib=17963
internal class BiquadPeakingFilter(
    private val sampleRate: Float,
    private val frequency: Float,
    private val gainDB: Float,
    private val q: Float
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
        val A = 10.0.pow(gainDB / 40.0)
        val omega = 2.0 * Math.PI * frequency / sampleRate
        val sinW = sin(omega)
        val cosW = cos(omega)

        val alphaBoost = if (gainDB >= 0f) {
            sinW / (2.0 * q)
        } else {
            sinW / (2.0 * q * A)
        }

        val a0 = 1.0 + alphaBoost / A
        val safeA0 = if (abs(a0) < 1e-8) 1e-8 else a0

        b0 = (1.0 + alphaBoost * A) / safeA0
        b1 = (-2.0 * cosW) / safeA0
        b2 = (1.0 - alphaBoost * A) / safeA0
        a1 = (-2.0 * cosW) / safeA0
        a2 = (1.0 - alphaBoost / A) / safeA0
    }

    internal fun processSample(input: Float): Float {
        val x0 = input.toDouble()
        val y0 = b0 * x0 + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2

        x2 = x1
        x1 = x0
        y2 = y1
        y1 = y0

        return y0.coerceIn(-1e4, 1e4).toFloat()
    }
}