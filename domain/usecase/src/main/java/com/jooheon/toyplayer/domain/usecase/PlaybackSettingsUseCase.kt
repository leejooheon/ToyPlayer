package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.music.RepeatMode
import com.jooheon.toyplayer.domain.model.music.ShuffleMode
import com.jooheon.toyplayer.domain.repository.api.PlaybackSettingsRepository
import javax.inject.Inject

class PlaybackSettingsUseCase @Inject constructor(
    private val playbackSettingsRepository: PlaybackSettingsRepository,
) {
    suspend fun setLastEnqueuedPlaylistName(name: String) {
        playbackSettingsRepository.setLastEnqueuedPlaylistName(name)
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

    suspend fun lastEnqueuedPlaylistName() = playbackSettingsRepository.lastEnqueuedPlaylistName()
    suspend fun repeatMode(): RepeatMode = playbackSettingsRepository.repeatMode()
    suspend fun shuffleMode(): ShuffleMode = playbackSettingsRepository.shuffleMode()
    suspend fun lastPlayedMediaId() = playbackSettingsRepository.lastPlayedMediaId()
}