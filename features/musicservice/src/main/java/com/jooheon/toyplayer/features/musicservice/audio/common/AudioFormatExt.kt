package com.jooheon.toyplayer.features.musicservice.audio.common

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi


@OptIn(UnstableApi::class)
internal fun frameSize(audioFormat: AudioProcessor.AudioFormat): Int {
    val bytesPerSample = when (audioFormat.encoding) {
        C.ENCODING_PCM_16BIT -> 2
        C.ENCODING_PCM_8BIT -> 1
        C.ENCODING_PCM_FLOAT -> 4
        else -> error("Unsupported encoding: ${audioFormat.encoding}")
    }

    return bytesPerSample * audioFormat.channelCount
}