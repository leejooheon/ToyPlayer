package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.music.RepeatMode
import com.jooheon.toyplayer.domain.model.music.ShuffleMode

interface PlaybackSettingsRepository {
    suspend fun setLastEnqueuedPlaylistName(name: String)
    suspend fun setLastPlayedMediaId(mediaId: String)
    suspend fun setRepeatMode(repeatMode: Int)
    suspend fun setShuffleMode(shuffleEnabled: Boolean)

    suspend fun lastEnqueuedPlaylistName(): String
    suspend fun repeatMode(): RepeatMode
    suspend fun shuffleMode(): ShuffleMode
    suspend fun lastPlayedMediaId(): String
}