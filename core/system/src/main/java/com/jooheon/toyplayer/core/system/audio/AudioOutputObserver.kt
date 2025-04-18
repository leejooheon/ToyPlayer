package com.jooheon.toyplayer.core.system.audio

import com.jooheon.toyplayer.domain.model.audio.AudioOutputDevice
import kotlinx.coroutines.flow.Flow

interface AudioOutputObserver {
    fun observeCurrentOutput(): Flow<AudioOutputDevice>
    fun observeAvailableOutputs(): Flow<List<AudioOutputDevice>>
    fun observeSystemVolume(): Flow<Pair<Int, Int>> // current, max

    fun setVolume(volume: Int)
}