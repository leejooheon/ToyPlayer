package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.music.RepeatMode
import com.jooheon.toyplayer.domain.model.music.ShuffleMode
import com.jooheon.toyplayer.domain.repository.api.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaybackSettingsUseCase @Inject constructor(
    private val playbackSettingsRepository: PlaybackSettingsRepository,
) {
    suspend fun setRecentPlaylistId(id: Int) {
        playbackSettingsRepository.setRecentPlaylistId(id)
    }
    suspend fun setLastPlayedMediaId(mediaId: String) {
        playbackSettingsRepository.setLastPlayedMediaId(mediaId)
    }
    suspend fun repeatModeChanged(repeatMode: Int) {
        playbackSettingsRepository.setRepeatMode(repeatMode)
    }
    suspend fun shuffleModeChanged(shuffleEnabled: Boolean) {
        playbackSettingsRepository.setShuffleMode(shuffleEnabled)
    }

    fun flowPlaylistId(): Flow<Int> = playbackSettingsRepository.flowPlaylistId()
    suspend fun playlistId() = playbackSettingsRepository.playlistId()
    suspend fun repeatMode(): RepeatMode = playbackSettingsRepository.repeatMode()
    suspend fun shuffleMode(): ShuffleMode = playbackSettingsRepository.shuffleMode()
    suspend fun lastPlayedMediaId() = playbackSettingsRepository.lastPlayedMediaId()
}