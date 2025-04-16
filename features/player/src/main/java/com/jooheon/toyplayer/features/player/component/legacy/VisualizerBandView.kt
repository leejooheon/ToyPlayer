package com.jooheon.toyplayer.features.player.component.legacy

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import com.jooheon.toyplayer.features.common.extension.deviceHeight
import com.jooheon.toyplayer.features.common.extension.deviceWidth
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Based on FFTBandView by Pär Amsen:
 * https://github.com/paramsen/noise/blob/master/sample/src/main/java/com/paramsen/noise/sample/view/FFTBandView.kt
 */
class VisualizerBandView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val SAMPLE_SIZE = 4096
        const val PEAK_RANGE = 11
        const val PEAK_COUNT = 3

        const val START_FREQUENCY = 20
        const val END_FREQUENCY = 200
        const val FREQUENCY_BANDS = 49
    }

    private var sampleRateHz: Int = 44100
    private val bandWidth = ((END_FREQUENCY - START_FREQUENCY) / FREQUENCY_BANDS.toDouble()).roundToInt()
    private val maxConst = 25_000 // Reference max value for accum magnitude

    private val paintBandsFill = Paint()

    private var currentBarHeightArray = FloatArray(FREQUENCY_BANDS)
    private var barOffsetArray = FloatArray(FREQUENCY_BANDS)

    private var barAnimatorArray: Array<ValueAnimator?> = arrayOfNulls(FREQUENCY_BANDS)

    private var space: Float = (5.36f).dp()
    private var defaultSize: Float = (8.04f).dp()

    init {
        paintBandsFill.color = Color.WHITE
        paintBandsFill.style = Paint.Style.FILL
    }

    private fun setSize(hasLongWidth: Boolean) {
        if (hasLongWidth) {
            val cell = width.toFloat() / (FREQUENCY_BANDS * 3 + (FREQUENCY_BANDS - 1) * 2)
            space = cell * 2f
            defaultSize = cell * 3f
        } else {
            val cell = width.toFloat() / (FREQUENCY_BANDS * 3.5f + (FREQUENCY_BANDS - 1) * 2)
            space = cell * 2f
            defaultSize = cell * 3.5f
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val hasLongWidth = context.deviceWidth() >= context.deviceHeight()
        setSize(hasLongWidth)
    }

    override fun onDetachedFromWindow() {
        for ((idx, _) in barAnimatorArray.withIndex()) {
            barAnimatorArray[idx]?.cancel()
        }
        super.onDetachedFromWindow()
    }

    // The result array has the pairs of real+imaginary floats in a one dimensional array; even indices
    // are real, odd indices are imaginary. DC(Direct Current) bin is located at index 0, 1, nyquist at index n-2, n-1
    fun onFFT(fft: FloatArray, sampleRate: Int) {
        this.sampleRateHz = sampleRate

        val barHeightArray = calculateHeights(fft)

        for ((idx, barHeight) in barHeightArray.withIndex()) {
            barAnimatorArray[idx]?.cancel()
            //  기본값은 10ms마다 새로고치도록 설정되지만, 애플리케이션에서 프레임을 새로고칠 수 있는 속도는
            //  궁극적으로 시스템의 전반적인 사용량과 시스템에서 기본 타이머를 제공하는 속도에 따라 달라짐
            barAnimatorArray[idx] = ValueAnimator.ofFloat(currentBarHeightArray[idx], barHeight).apply {
                addUpdateListener {
                    barOffsetArray[idx] = it.animatedValue as Float
                }

                duration = 100L
                interpolator = LinearInterpolator()
                start()
            }
        }

        for ((idx, value) in barHeightArray.withIndex()) {
            currentBarHeightArray[idx] = value
        }
    }

    private fun calculateHeights(fft: FloatArray): FloatArray {
        var currentFftPosition = 0
        var currentFrequencyBandLimitIndex = 0
        var lastindex = -1

        var barHeightArray = FloatArray(FREQUENCY_BANDS)
        val tempPair = ArrayList<Pair<Int, Float>>()

        // 주파수 = SR / sample_size * index
        val endPosition = END_FREQUENCY * SAMPLE_SIZE / sampleRateHz

        while (currentFftPosition < endPosition) {
            val nextFrequency = START_FREQUENCY + bandWidth * currentFrequencyBandLimitIndex
            val nextLimitAtPosition = nextFrequency * SAMPLE_SIZE / sampleRateHz

            if (nextLimitAtPosition == currentFftPosition) {
                currentFrequencyBandLimitIndex++
                continue
            }
//            Log.e("KIMEY", "nextLimitAtPosition : $nextLimitAtPosition, " +
//                    "currentFrequencyBandLimitIndex : $currentFrequencyBandLimitIndex, " +
//                    "(START_FREQUENCY + BAND_WIDTH * currentFrequencyBandLimitIndex) : " +
//                    "${(START_FREQUENCY + bandWidth * currentFrequencyBandLimitIndex)} ")

            // Convert real and imaginary part to get energy
            val raw1 = fft[currentFftPosition * 2 ].toDouble().pow(2.0)
            val raw2 = fft[currentFftPosition * 2 + 1].toDouble().pow(2.0)
            val raw = (raw1 + raw2).toFloat()

            currentFftPosition = nextLimitAtPosition

            val barHeight =
                (height * (raw / maxConst.toDouble()).coerceAtMost(1.0).toFloat())

            tempPair.add(Pair(currentFrequencyBandLimitIndex, barHeight))

            currentFrequencyBandLimitIndex++
        }

        // 높이값 기준 내림차순으로 정렬
        tempPair.sortByDescending {
            it.second
        }

        val peakCount = PEAK_COUNT//(1..PEAK_COUNT).random()

        val tempSize = tempPair.size
        tempPair.subList(peakCount, tempSize).clear()

        // 남아있는 PEAK_COUNT 만큼의 큰 값들을 주파수 오름차순으로 정렬
        tempPair.sortBy {
            it.first
        }

        for (i in 0 until peakCount) {
            val barHeight = tempPair[i].second
            val index = tempPair[i].first

            barHeightArray = getPeakRange(barHeightArray, barHeight, index, lastindex)

            lastindex = index + (PEAK_RANGE - 1)/2
        }

        return barHeightArray
    }

    private fun getPeakRange(barHeightArray: FloatArray, barHeight: Float, index: Int, lastIndex: Int): FloatArray {
        var halfPosition = (PEAK_RANGE - 1) / 2

        while (halfPosition > 0) {

            val resultValue =
                barHeight.softTransition((PEAK_RANGE - 1) / 2, halfPosition)

            if (index - halfPosition >= 0) {
                if (index - halfPosition <= lastIndex &&
                    barHeightArray[index - halfPosition] < resultValue ||
                    index - halfPosition > lastIndex
                ) {
                    barHeightArray[index - halfPosition] = resultValue
                }
            }

            if (index + halfPosition < FREQUENCY_BANDS) {
                if (index + halfPosition <= lastIndex &&
                    barHeightArray[index + halfPosition] < resultValue ||
                    index + halfPosition > lastIndex) {
                    barHeightArray[index + halfPosition] = resultValue
                }
            }

            halfPosition--
        }

        if (index <= lastIndex && barHeightArray[index] < barHeight || index > lastIndex) {
            barHeightArray[index] = barHeight
        }

        return barHeightArray
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawAudio(canvas)

        invalidate()
    }

    private fun drawAudio(canvas: Canvas) {
        for (i in 0 until FREQUENCY_BANDS) {
            val leftX = (width + space) * (i / FREQUENCY_BANDS.toFloat())
            val rightX = leftX + defaultSize

            val top = height - barOffsetArray[i]
            val bottom = height + barOffsetArray[i]

            if (barOffsetArray[i] <= defaultSize) { // 바 높이가 기본 값보다 작은 경우
                canvas.drawRoundRect(
                    leftX,
                    (height - defaultSize) / 2,
                    rightX,
                    (height + defaultSize) / 2,
                    30f,
                    30f,
                    paintBandsFill
                )
            } else {
                canvas.drawRoundRect(
                    leftX,
                    top / 2,
                    rightX,
                    bottom / 2,
                    30f,
                    30f,
                    paintBandsFill
                )
            }
        }
    }

    private fun Float.dp(): Float {
        return this * Resources.getSystem().displayMetrics.density
    }

    private fun Float.softTransition(halfPeakSize: Int, position: Int): Float {
        var result = this
        result -= result / halfPeakSize * position - 2f.dp() // 부드러운 곡선 형태로 모양을 주기 위해 2dp

        return result
    }
}
