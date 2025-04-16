package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datastore.DefaultSettingsPreferencesDataSource
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.repository.api.DefaultSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DefaultSettingsRepositoryImpl(
    private val settingsDataSource: DefaultSettingsPreferencesDataSource,
): DefaultSettingsRepository {

    override fun flowIsDarkTheme(): Flow<Boolean> = settingsDataSource.flowIsDarkTheme()

    override suspend fun updateIsDarkTheme(isDarkTheme: Boolean) {
        settingsDataSource.updateIsDarkTheme(isDarkTheme)
    }

    override suspend fun setLastEnqueuedPlaylistName(name: String) {
        settingsDataSource.setLastEnqueuedPlaylistName(name)
    }

    override suspend fun setLastPlayedMediaId(mediaId: String) {
        settingsDataSource.setLastPlayedMediaId(mediaId)
    }

    override suspend fun lastEnqueuedPlaylistName(): String {
        return settingsDataSource.defaultSettingsData
            .map { it.lastEnqueuedPlaylistName }
            .firstOrNull()
            .defaultEmpty()
    }

    override suspend fun lastPlayedMediaId(): String {
        return settingsDataSource.defaultSettingsData
            .map { it.lastPlayedMediaId }
            .firstOrNull()
            .defaultEmpty()
    }
}