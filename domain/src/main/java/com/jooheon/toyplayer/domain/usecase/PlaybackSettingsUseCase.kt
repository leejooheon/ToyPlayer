package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.repository.PlaybackSettingsRepository
import javax.inject.Inject

class PlaybackSettingsUseCase @Inject constructor(
    private val playbackSettingsRepository: PlaybackSettingsRepository,
) {
    suspend fun repeatModeChanged(repeatMode: Int) {
        playbackSettingsRepository.setRepeatMode(repeatMode)
    }
    suspend fun shuffleModeChanged(shuffleEnabled: Boolean) {
        playbackSettingsRepository.setShuffleMode(shuffleEnabled)
    }

    fun repeatMode() = playbackSettingsRepository.flowRepeatMode()

    fun shuffleMode() = playbackSettingsRepository.flowShuffleMode()

    suspend fun getRecentMediaItemKey(): Long {
        return playbackSettingsRepository.getPlayingQueueKey()
    }

    suspend fun setRecentMediaItemKey(key: Long) {
        playbackSettingsRepository.setPlayingQueueKey(key)
    }
}