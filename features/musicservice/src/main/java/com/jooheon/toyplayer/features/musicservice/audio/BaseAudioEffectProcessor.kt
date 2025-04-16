package com.jooheon.toyplayer.features.musicservice.audio

import androidx.annotation.OptIn
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer


@OptIn(UnstableApi::class)
abstract class BaseAudioEffectProcessor: BaseAudioProcessor() {
    protected fun bypassOutput(inputBuffer: ByteBuffer) {
        val inputBufferLimit = inputBuffer.limit()
        val buffer: ByteBuffer = replaceOutputBuffer(inputBuffer.remaining())
        if(buffer.hasRemaining()) {
            buffer.put(inputBuffer)
        }
        buffer.flip()
        inputBuffer.limit(inputBufferLimit)
    }

    abstract fun shouldBypass(): Boolean
}