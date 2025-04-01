package com.jooheon.toyplayer.features.musicservice.equalizer.filter

import kotlin.math.*

// https://ieeexplore.ieee.org/document/7797451
// https://mdpi-res.com/d_attachment/applsci/applsci-06-00129/article_deploy/applsci-06-00129.pdf
class EighthOrderPeakingFilter(
    sampleRate: Float,
    val frequency: Float,
    val gainDB: Float,
    q: Float
) {
    private val stage1 = BiquadPeakingFilter(sampleRate, frequency, gainDB / 4f, computeEnhancedQ(q, gainDB))
    private val stage2 = BiquadPeakingFilter(sampleRate, frequency, gainDB / 4f, computeEnhancedQ(q, gainDB))
    private val stage3 = BiquadPeakingFilter(sampleRate, frequency, gainDB / 4f, computeEnhancedQ(q, gainDB))
    private val stage4 = BiquadPeakingFilter(sampleRate, frequency, gainDB / 4f, computeEnhancedQ(q, gainDB))

    fun processSample(input: Float): Float {
        val y1 = stage1.processSample(input)
        val y2 = stage2.processSample(y1)
        val y3 = stage3.processSample(y2)
        val y4 = stage4.processSample(y3)
        return y4
    }

    // 논문 기반 Q 값 조정 로직 (예시): gain에 따라 Q를 보정함으로써 더 정확한 응답곡선 생성
    private fun computeEnhancedQ(baseQ: Float, gainDB: Float): Float {
        val absGain = abs(gainDB)
        return when {
            absGain < 3f -> baseQ
            absGain < 6f -> baseQ * 1.1f
            absGain < 10f -> baseQ * 1.25f
            else -> baseQ * 1.4f
        }
    }
}