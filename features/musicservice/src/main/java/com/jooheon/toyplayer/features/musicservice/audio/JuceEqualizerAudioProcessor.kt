package com.jooheon.toyplayer.features.musicservice.audio

import androidx.annotation.OptIn
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.features.musicservice.audio.ext.frameSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs
import kotlin.math.tanh

@OptIn(UnstableApi::class)
class JuceEqualizerAudioProcessor(
    scope: CoroutineScope,
    playerSettingsUseCase: PlayerSettingsUseCase,
) : BaseAudioEffectProcessor() {
    private var currentPreset = AtomicReference(Preset.default)

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
        if (shouldBypass()) {
            bypassOutput(inputBuffer)
            return
        }

        val outputBuffer = replaceOutputBuffer(inputBuffer.remaining())
        inputBuffer.order(ByteOrder.LITTLE_ENDIAN)
        outputBuffer.order(ByteOrder.LITTLE_ENDIAN)

        val numFrames = inputBuffer.remaining() / inputAudioFormat.frameSize
        val totalSamples = numFrames * channelCount

        val input = FloatArray(totalSamples)
        val output = FloatArray(totalSamples)

        for (i in 0 until totalSamples) {
            input[i] = inputBuffer.short.toFloat() / 32768f
        }

        nativeProcessSamples(input, output, channelCount, numFrames)
//        computeSmoothedGain(output)

        for (sample in output) {
//            val s = (softClip(sample * smoothedGain) * 32768f)
//                .toInt()
//                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
//                .toShort()
            val s = (sample * 32768f)
                .toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
            outputBuffer.putShort(s)
        }

        outputBuffer.flip()
    }

    private fun updateFilter(inputAudioFormat: AudioProcessor.AudioFormat) {
        val preset = currentPreset.get()
        channelCount = inputAudioFormat.channelCount
        nativePrepare(
            sampleRate = inputAudioFormat.sampleRate.toFloat(),
            channelCount = inputAudioFormat.channelCount,
            frequencies = preset.type.frequencies().toFloatArray(),
            gains = preset.gains.toFloatArray()
        )
    }

    private fun computeSmoothedGain(output: FloatArray) {
        val peak = output.maxOfOrNull { abs(it) } ?: 0f
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

    external fun nativePrepare(
        sampleRate: Float,
        channelCount: Int,
        frequencies: FloatArray,
        gains: FloatArray,
    )
    external fun nativeProcessSamples(
        input: FloatArray,
        output: FloatArray,
        channelCount: Int,
        numFrames: Int,
    )

    override fun shouldBypass(): Boolean = currentPreset.get().isFlat()

    companion object {
        private const val MAX_AMPLITUDE = 0.95f
        private const val ATTACK = 0.15f
        private const val RELEASE = 0.5f
    }
}