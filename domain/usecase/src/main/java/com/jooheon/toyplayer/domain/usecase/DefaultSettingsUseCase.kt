package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.audio.AudioUsage
import com.jooheon.toyplayer.domain.repository.api.DefaultSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSettingsUseCase @Inject constructor(
    private val defaultSettingsRepository: DefaultSettingsRepository,
) {
    suspend fun lastEnqueuedPlaylistName() = defaultSettingsRepository.lastEnqueuedPlaylistName()
    suspend fun setLastEnqueuedPlaylistName(name: String) {
        defaultSettingsRepository.setLastEnqueuedPlaylistName(name)
    }

    suspend fun lastPlayedMediaId() = defaultSettingsRepository.lastPlayedMediaId()
    suspend fun setLastPlayedMediaId(mediaId: String) {
        defaultSettingsRepository.setLastPlayedMediaId(mediaId)
    }

    fun flowIsDarkTheme() = defaultSettingsRepository.flowIsDarkTheme()
    suspend fun updateIsDarkTheme(isDarkTheme: Boolean) {
        defaultSettingsRepository.updateIsDarkTheme(isDarkTheme)
    }

    fun flowAudioUsage(): Flow<AudioUsage> = defaultSettingsRepository.flowAudioUsage()

    suspend fun setAudioUsage(audioUsage: AudioUsage) {
        defaultSettingsRepository.setAudioUsage(audioUsage)
    }
}