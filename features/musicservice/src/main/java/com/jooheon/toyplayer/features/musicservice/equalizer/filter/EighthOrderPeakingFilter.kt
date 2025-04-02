package com.jooheon.toyplayer.features.musicservice.equalizer.filter

import kotlin.math.*

// https://ieeexplore.ieee.org/document/7797451
// https://mdpi-res.com/d_attachment/applsci/applsci-06-00129/article_deploy/applsci-06-00129.pdf
class EighthOrderPeakingFilter(
    sampleRate: Float,
    frequency: Float,
    gainDB: Float,
    q: Float,
    stages: Int = 2 // 필요에 따라 필터 계수를 올리면 된다.
) {
    private val filters: List<BiquadPeakingFilter>

    init {
        val perStageGain = gainDB / stages.toFloat()
        filters = List(stages) {
            BiquadPeakingFilter(sampleRate, frequency, perStageGain, computeEnhancedQ(q, gainDB))
        }
    }

    fun processSample(input: Float): Float {
        return filters.fold(input) { acc, filter -> filter.processSample(acc) }
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