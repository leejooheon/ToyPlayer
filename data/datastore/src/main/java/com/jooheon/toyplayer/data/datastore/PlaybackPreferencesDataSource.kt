package com.jooheon.toyplayer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jooheon.toyplayer.data.datastore.di.DataStoreQualifier
import com.jooheon.toyplayer.data.datastore.model.PlaybackData
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaybackPreferencesDataSource @Inject constructor(
    @DataStoreQualifier.Playback private val dataStore: DataStore<Preferences>,
) {
    object PreferencesKey {
        internal val LAST_ENQUEUED_PLAYLIST_NAME = stringPreferencesKey("LAST_ENQUEUED_PLAYLIST_NAME")
        internal val LAST_PLAYED_MEDIA_ID = stringPreferencesKey("LAST_PLAYED_MEDIA_ID")
        internal val REPEAT_MODE = intPreferencesKey("REPEAT_MODE")
        internal val SHUFFLE_MODE = booleanPreferencesKey("SHUFFLE_MODE")
    }

    val playbackData = dataStore.data.map { preferences ->
        PlaybackData(
            lastEnqueuedPlaylistName = preferences[PreferencesKey.LAST_ENQUEUED_PLAYLIST_NAME].defaultEmpty(),
            lastPlayedMediaId = preferences[PreferencesKey.LAST_PLAYED_MEDIA_ID].defaultEmpty(),
            repeatMode = preferences[PreferencesKey.REPEAT_MODE].defaultZero(),
            shuffleMode = preferences[PreferencesKey.SHUFFLE_MODE].defaultFalse(),
        )
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
    suspend fun setRepeatMode(repeatMode: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.REPEAT_MODE] = repeatMode
        }
    }
    suspend fun setShuffleMode(shuffleMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.SHUFFLE_MODE] = shuffleMode
        }
    }
}