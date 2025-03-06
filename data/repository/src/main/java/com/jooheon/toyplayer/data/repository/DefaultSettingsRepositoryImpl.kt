package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datastore.DefaultSettingsPreferencesDataSource
import com.jooheon.toyplayer.domain.model.SupportThemes
import com.jooheon.toyplayer.domain.repository.api.DefaultSettingsRepository
import kotlinx.coroutines.flow.Flow

class DefaultSettingsRepositoryImpl(
    private val settingsDataSource: DefaultSettingsPreferencesDataSource,
): DefaultSettingsRepository {

    override fun flowTheme(): Flow<SupportThemes> = settingsDataSource.flowTheme()

    override suspend fun setTheme(theme: SupportThemes) {
        settingsDataSource.updateTheme(theme)
    }
}