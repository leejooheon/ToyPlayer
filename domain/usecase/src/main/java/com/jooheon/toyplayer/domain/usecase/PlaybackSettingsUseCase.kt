package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.music.RepeatMode
import com.jooheon.toyplayer.domain.model.music.ShuffleMode
import com.jooheon.toyplayer.domain.repository.api.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
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

    fun repeatMode(): Flow<RepeatMode> = playbackSettingsRepository.flowRepeatMode()

    fun shuffleMode(): Flow<ShuffleMode> = playbackSettingsRepository.flowShuffleMode()

    suspend fun getRecentMediaItemKey(): Long {
        return playbackSettingsRepository.getPlayingQueueKey()
    }

    suspend fun setRecentMediaItemKey(key: Long) {
        playbackSettingsRepository.setPlayingQueueKey(key)
    }
}