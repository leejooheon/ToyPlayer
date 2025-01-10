package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datastore.PlaybackPreferencesDataSource
import com.jooheon.toyplayer.domain.common.extension.defaultZero
import com.jooheon.toyplayer.domain.entity.music.RepeatMode
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import com.jooheon.toyplayer.domain.repository.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

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

    override fun flowRepeatMode(): Flow<RepeatMode>
        = preferencesDataSource.playbackData.map { data -> RepeatMode.getByValue(data.repeatMode) }

    override fun flowShuffleMode(): Flow<ShuffleMode>
        = preferencesDataSource.playbackData.map { data -> ShuffleMode.getByValue(data.shuffleMode) }

    override fun flowSkipDuration(): Flow<Long>
        = preferencesDataSource.playbackData.map { data -> data.skipDuration }

    override suspend fun getPlayingQueueKey(): Long {
        return preferencesDataSource.playbackData
            .map { it.lastPlayedPosition }
            .firstOrNull()
            .defaultZero()
    }
}