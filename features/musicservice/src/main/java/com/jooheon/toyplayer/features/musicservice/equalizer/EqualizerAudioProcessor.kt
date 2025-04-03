package com.jooheon.toyplayer.features.musicservice.equalizer

import androidx.annotation.OptIn
import androidx.lifecycle.AtomicReference
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.features.musicservice.equalizer.filter.BiquadHighPassFilter
import com.jooheon.toyplayer.features.musicservice.equalizer.filter.MultiOrderPeakingFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.tanh

@OptIn(UnstableApi::class)
class EqualizerAudioProcessor(
    scope: CoroutineScope,
    playerSettingsUseCase: PlayerSettingsUseCase,
) : BaseAudioProcessor() {
    private var currentPreset = AtomicReference(Preset.default)

    private lateinit var lowCutFilters: List<BiquadHighPassFilter>
    private lateinit var peakingFiltersPerChannel: List<List<MultiOrderPeakingFilter>>

    private var equalizerType = EqualizerType.default
    private var channelCount = 0
    private var smoothedGain = 0f

    init {
        playerSettingsUseCase
            .flowEqualizerPreset()
            .onEach {
                currentPreset.set(it)
                if(isActive) updateFilter(inputAudioFormat)
            }
            .launchIn(scope)
    }

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        channelCount = inputAudioFormat.channelCount

        updateFilter(inputAudioFormat)
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

                peakingFiltersPerChannel[channel].forEach { filter ->
                    sample = filter.processSample(sample)
                    if(equalizerType != EqualizerType.BAND_31) {
                        sample = lowCutFilters[channel].processSample(sample)
                    }
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

    private fun updateFilter(inputAudioFormat: AudioProcessor.AudioFormat) {
        val preset = currentPreset.get()

        lowCutFilters = List(channelCount) {
            BiquadHighPassFilter(
                sampleRate = inputAudioFormat.sampleRate.toFloat(),
                cutoffFreq = when(preset.type) {
                    EqualizerType.BAND_31 -> 0.1f
                    EqualizerType.BAND_15 -> 7f
                    else -> 10f
                },
                q = when (preset.type) {
                    EqualizerType.BAND_31 -> 1f
                    EqualizerType.BAND_15 -> 0.85f
                    else -> 0.707f
                }
            )
        }

        peakingFiltersPerChannel = List(channelCount) {
            preset.type.frequencies().mapIndexed { index, frequency ->
                MultiOrderPeakingFilter(
                    sampleRate = inputAudioFormat.sampleRate.toFloat(),
                    frequency = frequency,
                    gainDB = preset.gains.getOrNull(index).defaultZero(),
                    q = 1f
                )
            }
        }

        equalizerType = preset.type
        smoothedGain = 1f
    }

    companion object {
        private const val MAX_AMPLITUDE = 0.95f
        private const val ATTACK = 0.15f
        private const val RELEASE = 0.5f
    }
}