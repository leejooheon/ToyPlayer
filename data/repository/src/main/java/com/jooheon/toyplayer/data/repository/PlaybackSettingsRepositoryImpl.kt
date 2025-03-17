package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datastore.PlaybackPreferencesDataSource
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.RepeatMode
import com.jooheon.toyplayer.domain.model.music.ShuffleMode
import com.jooheon.toyplayer.domain.repository.api.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class PlaybackSettingsRepositoryImpl(
    private val preferencesDataSource: PlaybackPreferencesDataSource,
): PlaybackSettingsRepository {

    override suspend fun setRecentPlaylistId(id: Int) {
        preferencesDataSource.setRecentPlaylistId(id)
    }

    override suspend fun setLastPlayedMediaId(mediaId: String) {
        preferencesDataSource.setLastPlayedMediaId(mediaId)
    }

    override suspend fun setRepeatMode(repeatMode: Int) {
        preferencesDataSource.setRepeatMode(repeatMode)
    }

    override suspend fun setShuffleMode(shuffleEnabled: Boolean) {
        preferencesDataSource.setShuffleMode(shuffleEnabled)
    }

    override fun flowPlaylistId(): Flow<Int>
            = preferencesDataSource.playbackData.map { data -> data.playlistId }

    override suspend fun repeatMode(): RepeatMode {
        val raw = preferencesDataSource.playbackData
            .map { it.repeatMode }
            .firstOrNull()
            .defaultZero()

        return RepeatMode.getByValue(raw)
    }

    override suspend fun shuffleMode(): ShuffleMode {
        val raw = preferencesDataSource.playbackData
            .map { it.shuffleMode }
            .firstOrNull()
            .defaultFalse()

        return ShuffleMode.getByValue(raw)
    }

    override suspend fun lastPlayedMediaId(): String {
        return preferencesDataSource.playbackData
            .map { it.lastPlayedMediaId }
            .firstOrNull()
            .defaultEmpty()
    }

    override suspend fun playlistId(): Int {
        return preferencesDataSource.playbackData
            .map { it.playlistId }
            .first()
    }
}