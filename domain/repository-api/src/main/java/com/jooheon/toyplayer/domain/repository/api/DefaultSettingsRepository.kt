package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.audio.AudioUsage
import kotlinx.coroutines.flow.Flow

interface DefaultSettingsRepository {
    fun flowIsDarkTheme(): Flow<Boolean>
    suspend fun updateIsDarkTheme(isDarkTheme: Boolean)

    suspend fun setLastEnqueuedPlaylistName(name: String)
    suspend fun lastEnqueuedPlaylistName(): String

    suspend fun setLastPlayedMediaId(mediaId: String)
    suspend fun lastPlayedMediaId(): String

    suspend fun setAudioUsage(audioUsage: AudioUsage)
    fun flowAudioUsage(): Flow<AudioUsage>
}