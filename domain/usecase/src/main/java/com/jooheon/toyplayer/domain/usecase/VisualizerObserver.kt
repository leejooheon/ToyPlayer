package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.audio.VisualizerData
import kotlinx.coroutines.flow.Flow

interface VisualizerObserver {
    fun observe(): Flow<VisualizerData>
}