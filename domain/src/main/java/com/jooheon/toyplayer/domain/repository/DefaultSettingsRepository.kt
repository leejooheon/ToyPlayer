package com.jooheon.toyplayer.domain.repository

import com.jooheon.toyplayer.domain.entity.SupportThemes
import kotlinx.coroutines.flow.Flow

interface DefaultSettingsRepository {
    fun flowTheme(): Flow<SupportThemes>
    suspend fun setTheme(theme: SupportThemes)
}