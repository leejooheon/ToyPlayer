package com.jooheon.toyplayer.features.musicservice.equalizer.filter

import kotlin.math.*

// https://secure.aes.org/forum/pubs/conventions/?elib=17963
class BiquadPeakingFilter(
    private val sampleRate: Float,
    private val frequency: Float,
    private val gainDB: Float,
    private val q: Float
) {
    private var b0 = 0.0f
    private var b1 = 0.0f
    private var b2 = 0.0f
    private var a1 = 0.0f
    private var a2 = 0.0f
    private var x1 = 0.0f
    private var x2 = 0.0f
    private var y1 = 0.0f
    private var y2 = 0.0f

    init {
        updateCoefficients()
    }

    private fun updateCoefficients() {
        val A = 10f.pow(gainDB / 40f)
        val omega = 2.0 * Math.PI * frequency / sampleRate
        val sinW = sin(omega).toFloat()
        val cosW = cos(omega).toFloat()

        // 논문 기반 계수 보정: boost/cut에 따라 alpha 조정
        val alphaBoost = if (gainDB >= 0f) {
            sinW / (2f * q)
        } else {
            // 감쇠 시 bandwidth 보정을 더 강하게 적용
            sinW / (2f * q * A)
        }

        val a0 = 1f + alphaBoost / A
        val safeA0 = if (abs(a0) < 1e-8f) 1e-8f else a0

        b0 = (1f + alphaBoost * A) / safeA0
        b1 = (-2f * cosW) / safeA0
        b2 = (1f - alphaBoost * A) / safeA0
        a1 = (-2f * cosW) / safeA0
        a2 = (1f - alphaBoost / A) / safeA0
    }

    fun processSample(input: Float): Float {
        val x0 = input
        val y0 = b0 * x0 + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2

        x2 = x1
        x1 = x0
        y2 = y1
        y1 = y0

        val result = y0.coerceIn((-1e4).toFloat(), 1e4.toFloat())
        return result
    }
}