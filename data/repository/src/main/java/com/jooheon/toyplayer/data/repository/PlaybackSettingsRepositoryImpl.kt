package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datastore.PlaybackPreferencesDataSource
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.repository.api.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class PlaybackSettingsRepositoryImpl(
    private val preferencesDataSource: PlaybackPreferencesDataSource,
): PlaybackSettingsRepository {

    override suspend fun setRepeatMode(repeatMode: Int) {
        preferencesDataSource.updateRepeatMode(repeatMode)
    }

    override suspend fun setShuffleMode(shuffleEnabled: Boolean) {
        preferencesDataSource.updateShuffleMode(shuffleEnabled)
    }

    override suspend fun setSkipDuration(duration: Long) {
        preferencesDataSource.updateSkipDuration(duration)
    }

    override suspend fun setPlayingQueueKey(key: Long) {
        preferencesDataSource.updateLastPlayedPosition(key)
    }

    override fun flowRepeatMode(): Flow<com.jooheon.toyplayer.domain.model.music.RepeatMode>
        = preferencesDataSource.playbackData.map { data -> com.jooheon.toyplayer.domain.model.music.RepeatMode.getByValue(data.repeatMode) }

    override fun flowShuffleMode(): Flow<com.jooheon.toyplayer.domain.model.music.ShuffleMode>
        = preferencesDataSource.playbackData.map { data -> com.jooheon.toyplayer.domain.model.music.ShuffleMode.getByValue(data.shuffleMode) }

    override fun flowSkipDuration(): Flow<Long>
        = preferencesDataSource.playbackData.map { data -> data.skipDuration }

    override suspend fun getPlayingQueueKey(): Long {
        return preferencesDataSource.playbackData
            .map { it.lastPlayedPosition }
            .firstOrNull()
            .defaultZero()
    }
}