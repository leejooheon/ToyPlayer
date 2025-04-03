package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.domain.repository.api.PlayerSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
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
    suspend fun setEqualizerPreset(preset: Preset) {
        val raw = Json.encodeToString(Preset.serializer(), preset)
        playerSettingsRepository.setEqualizerPreset(raw)
    }
    suspend fun setChannelBalance(channelBalance: Float) {
        playerSettingsRepository.setChannelBalance(channelBalance)
    }
    suspend fun setBassBoost(bassBoost: Int) {
        playerSettingsRepository.setBassBoost(bassBoost)
    }

    fun flowRepeatMode(): Flow<Int> = playerSettingsRepository.flowRepeatMode()
    fun flowShuffleMode(): Flow<Boolean> = playerSettingsRepository.flowShuffleMode()
    fun flowVolume(): Flow<Float> = playerSettingsRepository.flowVolume()
    fun flowEqualizerPreset(): Flow<Preset> {
        return playerSettingsRepository.flowEqualizerPreset()
            .map { json ->
                runCatching { Json.decodeFromString(Preset.serializer(), json) }
                    .getOrElse { Preset.default }
            }
    }
    fun flowChannelBalance(): Flow<Float> = playerSettingsRepository.flowChannelBalance()
    fun flowBassBoost(): Flow<Int> = playerSettingsRepository.flowBassBoost()
}