package com.jooheon.toyplayer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jooheon.toyplayer.data.datastore.di.DataStoreQualifier
import com.jooheon.toyplayer.data.datastore.model.DefaultSettingsData
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSettingsPreferencesDataSource @Inject constructor(
    @param:DataStoreQualifier.Default private val dataStore: DataStore<Preferences>,
) {
    object PreferencesKey {
        internal val LAST_ENQUEUED_PLAYLIST_NAME = stringPreferencesKey("LAST_ENQUEUED_PLAYLIST_NAME")
        internal val LAST_PLAYED_MEDIA_ID = stringPreferencesKey("LAST_PLAYED_MEDIA_ID")
        internal val THEME = booleanPreferencesKey("THEME")
        internal val AUDIO_USAGE = intPreferencesKey("AUDIO_USAGE")
    }

    val defaultSettingsData = dataStore.data.map { preferences ->
        DefaultSettingsData(
            lastEnqueuedPlaylistName = preferences[PreferencesKey.LAST_ENQUEUED_PLAYLIST_NAME].defaultEmpty(),
            lastPlayedMediaId = preferences[PreferencesKey.LAST_PLAYED_MEDIA_ID].defaultEmpty(),
            isDarkTheme = preferences[PreferencesKey.THEME].defaultFalse(),
            audioUsage = preferences[PreferencesKey.AUDIO_USAGE].defaultZero()
        )
    }

    suspend fun updateIsDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.THEME] = isDarkTheme
        }
    }

    suspend fun setLastEnqueuedPlaylistName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.LAST_ENQUEUED_PLAYLIST_NAME] = name
        }
    }
    suspend fun setLastPlayedMediaId(mediaId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.LAST_PLAYED_MEDIA_ID] = mediaId
        }
    }

    suspend fun setAudioUsage(audioUsage: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.AUDIO_USAGE] = audioUsage
        }
    }
}
