package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.repository.api.PlayerSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayerSettingsUseCase @Inject constructor(
    private val playerSettingsRepository: PlayerSettingsRepository,
) {
    suspend fun setVolume(volume: Float) {
        playerSettingsRepository.setVolume(volume)
    }
    suspend fun setRepeatMode(repeatMode: Int) {
        playerSettingsRepository.setRepeatMode(repeatMode)
    }
    suspend fun setShuffleMode(shuffleEnabled: Boolean) {
        playerSettingsRepository.setShuffleMode(shuffleEnabled)
    }

    suspend fun flowRepeatMode(): Flow<Int> = playerSettingsRepository.flowRepeatMode()
    suspend fun flowShuffleMode(): Flow<Boolean> = playerSettingsRepository.flowShuffleMode()
    suspend fun flowVolume(): Flow<Float> = playerSettingsRepository.flowVolume()
}