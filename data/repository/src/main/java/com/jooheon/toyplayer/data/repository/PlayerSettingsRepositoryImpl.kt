package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datastore.PlayerSettingsPreferencesDataSource
import com.jooheon.toyplayer.domain.repository.api.PlayerSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerSettingsRepositoryImpl(
    private val preferencesDataSource: PlayerSettingsPreferencesDataSource,
): PlayerSettingsRepository {
    override suspend fun setRepeatMode(repeatMode: Int) {
        preferencesDataSource.setRepeatMode(repeatMode)
    }
    override suspend fun setShuffleMode(shuffleEnabled: Boolean) {
        preferencesDataSource.setShuffleMode(shuffleEnabled)
    }
    override suspend fun setVolume(volume: Float) {
        preferencesDataSource.setVolume(volume)
    }

    override suspend fun flowRepeatMode(): Flow<Int> {
        return preferencesDataSource.playerSettingsData.map { it.repeatMode }
    }
    override suspend fun flowShuffleMode(): Flow<Boolean> {
        return preferencesDataSource.playerSettingsData.map { settingsData -> settingsData.shuffleMode }
    }
    override suspend fun flowVolume(): Flow<Float> {
        return preferencesDataSource.playerSettingsData.map { settingsData -> settingsData.volume }
    }
}