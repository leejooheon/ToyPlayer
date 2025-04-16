package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.repository.api.DefaultSettingsRepository
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
}