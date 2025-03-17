package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.SupportThemes
import com.jooheon.toyplayer.domain.repository.api.DefaultSettingsRepository
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val defaultSettingsRepository: DefaultSettingsRepository,
) {
    fun flowTheme()
        = defaultSettingsRepository.flowTheme()

    suspend fun setTheme(theme: SupportThemes) {
        defaultSettingsRepository.setTheme(theme)
    }
}