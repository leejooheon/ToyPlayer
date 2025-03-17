package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.music.RepeatMode
import com.jooheon.toyplayer.domain.model.music.ShuffleMode
import kotlinx.coroutines.flow.Flow

interface PlaybackSettingsRepository {
    suspend fun setRecentPlaylistId(id: Int)
    suspend fun setLastPlayedMediaId(mediaId: String)
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)

    fun flowPlaylistId(): Flow<Int>
    suspend fun repeatMode(): RepeatMode
    suspend fun shuffleMode(): ShuffleMode
    suspend fun lastPlayedMediaId(): String
    suspend fun playlistId(): Int
}