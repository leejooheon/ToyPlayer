package com.jooheon.toyplayer.features.musicservice.audio

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.google.common.util.concurrent.AtomicDouble
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.features.musicservice.audio.ext.frameSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs

@UnstableApi
class BalanceAudioProcessor(
    scope: CoroutineScope,
    playerSettingsUseCase: PlayerSettingsUseCase,
): BaseAudioEffectProcessor() {
    private var channelCount = 0
    private var balance = AtomicDouble(0.0)  // -1.0 (left) ~ 1.0 (right)

    init {
        playerSettingsUseCase
            .flowChannelBalance()
            .onEach { balance.set(it.toDouble()) }
            .launchIn(scope)
    }

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        channelCount = inputAudioFormat.channelCount
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if(shouldBypass()) {
            bypassOutput(inputBuffer)
            return
        }

        val outputBuffer = replaceOutputBuffer(inputBuffer.remaining())
        inputBuffer.order(ByteOrder.LITTLE_ENDIAN)
        outputBuffer.order(ByteOrder.LITTLE_ENDIAN)

        val balance = balance.get().toFloat()
        while (inputBuffer.remaining() >= inputAudioFormat.frameSize) {
            val left = inputBuffer.short.toFloat() / 32768f
            val right = inputBuffer.short.toFloat() / 32768f

            val leftGain = (1f - balance.coerceIn(0f, 1f))
            val rightGain = (1f + balance.coerceIn(-1f, 0f))

            val outLeft = (left * leftGain).coerceIn(-1f, 1f)
            val outRight = (right * rightGain).coerceIn(-1f, 1f)

            outputBuffer.putShort((outLeft * 32768f).toInt().toShort())
            outputBuffer.putShort((outRight * 32768f).toInt().toShort())
        }

        outputBuffer.flip()
    }

    override fun shouldBypass(): Boolean {
        return abs(balance.get()) < 0.0001 // Float-safe 비교
    }
}
