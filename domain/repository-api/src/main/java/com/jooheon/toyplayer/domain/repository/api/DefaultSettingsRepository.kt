package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.SupportThemes
import kotlinx.coroutines.flow.Flow

interface DefaultSettingsRepository {
    fun flowTheme(): Flow<SupportThemes>
    suspend fun setTheme(theme: SupportThemes)
}