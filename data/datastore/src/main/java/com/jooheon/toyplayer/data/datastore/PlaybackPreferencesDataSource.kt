package com.jooheon.toyplayer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.jooheon.toyplayer.data.datastore.di.DataStoreQualifier
import com.jooheon.toyplayer.data.datastore.model.PlaybackData
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaybackPreferencesDataSource @Inject constructor(
    @DataStoreQualifier.Playback private val dataStore: DataStore<Preferences>,
) {
    object PreferencesKey {
        internal val LAST_PLAYED_POSITION = longPreferencesKey("LAST_PLAYED_POSITION")

        internal val SKIP_DURATION = longPreferencesKey("SKIP_DURATION")
        internal val REPEAT_MODE = intPreferencesKey("REPEAT_MODE")
        internal val SHUFFLE_MODE = booleanPreferencesKey("SHUFFLE_MODE")
    }

    val playbackData = dataStore.data.map { preferences ->
        PlaybackData(
            lastPlayedPosition = preferences[PreferencesKey.LAST_PLAYED_POSITION].defaultZero(),
            skipDuration = preferences[PreferencesKey.SKIP_DURATION].defaultZero(),
            repeatMode = preferences[PreferencesKey.REPEAT_MODE].defaultZero(),
            shuffleMode = preferences[PreferencesKey.SHUFFLE_MODE].defaultFalse(),
        )
    }

    suspend fun updateLastPlayedPosition(position: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.LAST_PLAYED_POSITION] = position
        }
    }
    suspend fun updateSkipDuration(duration: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.SKIP_DURATION] = duration
        }
    }
    suspend fun updateRepeatMode(repeatMode: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.REPEAT_MODE] = repeatMode
        }
    }
    suspend fun updateShuffleMode(shuffleMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.SHUFFLE_MODE] = shuffleMode
        }
    }
}