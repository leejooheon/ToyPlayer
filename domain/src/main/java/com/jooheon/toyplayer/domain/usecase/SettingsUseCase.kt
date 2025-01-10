package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.entity.SupportThemes
import com.jooheon.toyplayer.domain.repository.DefaultSettingsRepository
import com.jooheon.toyplayer.domain.repository.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val defaultSettingsRepository: DefaultSettingsRepository,
    private val playbackSettingsRepository: PlaybackSettingsRepository,
) {
    fun flowTheme()
        = defaultSettingsRepository.flowTheme()

    suspend fun setTheme(theme: SupportThemes) {
        defaultSettingsRepository.setTheme(theme)
    }

    fun flowSkipDuration(): Flow<Long>
        = playbackSettingsRepository.flowSkipDuration()

    suspend fun setSkipDuration(duration: Long) {
        playbackSettingsRepository.setSkipDuration(duration)
    }
}