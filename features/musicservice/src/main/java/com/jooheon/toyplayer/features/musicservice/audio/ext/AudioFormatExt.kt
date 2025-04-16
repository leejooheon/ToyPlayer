package com.jooheon.toyplayer.features.musicservice.audio.ext

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi


internal val AudioProcessor.AudioFormat.frameSize: Int
    @OptIn(UnstableApi::class)
    get() {
        val bytesPerSample = when (encoding) {
            C.ENCODING_PCM_16BIT -> 2
            C.ENCODING_PCM_8BIT -> 1
            C.ENCODING_PCM_FLOAT -> 4
            else -> error("Unsupported encoding: $encoding")
        }

        return bytesPerSample * channelCount
    }