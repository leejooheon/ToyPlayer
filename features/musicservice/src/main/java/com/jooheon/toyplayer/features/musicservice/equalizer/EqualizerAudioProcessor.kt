package com.jooheon.toyplayer.features.musicservice.equalizer

import androidx.annotation.OptIn
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.features.musicservice.equalizer.filter.BiquadHighPassFilter
import com.jooheon.toyplayer.features.musicservice.equalizer.filter.EighthOrderPeakingFilter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.tanh

@OptIn(UnstableApi::class)
class EqualizerAudioProcessor : BaseAudioProcessor() {
    private val centerFrequencies = listOf(31.5f, 63f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)
//    private val gains = listOf(10f, 10f, 8.0f, 4.3f, 0.5f, -6.0f, -5.4f, -4.4f, -4.0f, -3.0f)
//    private val gains = listOf(-3f, -3f, -1.2f, 2.2f, 5.1f, 9.8f, 6.8f, 1.8f, -0.1f, -3.0f)
//    private val gains = listOf(15f, 15f, 15f, 15f, 15f, 15f, 15f, 15f, 15f, 15f)
    private val gains = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    private lateinit var lowCutFilters: List<BiquadHighPassFilter>
    private lateinit var filtersPerChannel: List<List<EighthOrderPeakingFilter>>
    private var channelCount = 0
    private var smoothedGain = 0f

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        channelCount = inputAudioFormat.channelCount
        smoothedGain = 0f

        lowCutFilters = List(channelCount) {
            BiquadHighPassFilter(
                sampleRate = inputAudioFormat.sampleRate.toFloat()
            )
        }

        filtersPerChannel = List(channelCount) {
            centerFrequencies.mapIndexed { index, freq ->
                EighthOrderPeakingFilter(
                    sampleRate = inputAudioFormat.sampleRate.toFloat(),
                    frequency = freq,
                    gainDB = gains[index],
                    q = 1f
                )
            }
        }

        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val outputBuffer = replaceOutputBuffer(inputBuffer.remaining())
        inputBuffer.order(ByteOrder.LITTLE_ENDIAN)
        outputBuffer.order(ByteOrder.LITTLE_ENDIAN)

        val samplesPerChannel = List(channelCount) { mutableListOf<Float>() }
        while (inputBuffer.remaining() >= 2 * channelCount) {
            for (channel in 0 until channelCount) {
                val raw = inputBuffer.short
                var sample = raw.toFloat() / 32768f
                filtersPerChannel[channel].forEach { filter ->
                    val processed = filter.processSample(sample)
                    sample = lowCutFilters[channel].processSample(processed)
                }
                samplesPerChannel[channel].add(sample)
            }
        }

        computeSmoothedGain(samplesPerChannel)

        for (i in 0 until samplesPerChannel[0].size) {
            for (channel in 0 until channelCount) {
                val sample = samplesPerChannel[channel][i] * smoothedGain
                val cliped = softClip(sample)
                val processed = (cliped * 32768f)
                    .toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()

                outputBuffer.putShort(processed)
            }
        }
        outputBuffer.flip()
    }

    private fun computeSmoothedGain(samplesPerChannel: List<MutableList<Float>>) {
        val samples = samplesPerChannel.flatten()

        val peak = samples
            .maxOfOrNull { abs(it) }
            ?.takeIf { it.isFinite() }
            .defaultZero()

        val gain = if (peak > MAX_AMPLITUDE) MAX_AMPLITUDE / peak else 1f
        val smoothing = if (gain < smoothedGain) RELEASE else ATTACK
        smoothedGain += (gain - smoothedGain) * smoothing
    }

    private fun softClip(sample: Float, threshold: Float = 0.98f): Float {
        val abs = abs(sample)
        if (abs <= threshold) return sample

        val sign = if (sample >= 0) 1f else -1f
        val knee = 1f - threshold
        val over = (abs - threshold) / knee

        val clipped = threshold + knee * tanh(over)
        return sign * clipped
    }

    companion object {
        private const val MAX_AMPLITUDE = 0.95f
        private const val ATTACK = 0.15f
        private const val RELEASE = 0.5f
    }
}