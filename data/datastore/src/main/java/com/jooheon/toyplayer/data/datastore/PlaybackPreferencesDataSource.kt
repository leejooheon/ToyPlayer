package com.jooheon.toyplayer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jooheon.toyplayer.data.datastore.di.DataStoreQualifier
import com.jooheon.toyplayer.data.datastore.model.PlaybackData
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaybackPreferencesDataSource @Inject constructor(
    @DataStoreQualifier.Playback private val dataStore: DataStore<Preferences>,
) {
    object PreferencesKey {
        internal val RECENT_PLAYLIST_ID = intPreferencesKey("RECENT_PLAYLIST_ID")
        internal val LAST_PLAYED_MEDIA_ID = stringPreferencesKey("LAST_PLAYED_MEDIA_ID")
        internal val REPEAT_MODE = intPreferencesKey("REPEAT_MODE")
        internal val SHUFFLE_MODE = booleanPreferencesKey("SHUFFLE_MODE")
    }

    val playbackData = dataStore.data.map { preferences ->
        PlaybackData(
            playlistId = preferences[PreferencesKey.RECENT_PLAYLIST_ID].default(-1),
            lastPlayedMediaId = preferences[PreferencesKey.LAST_PLAYED_MEDIA_ID].defaultEmpty(),
            repeatMode = preferences[PreferencesKey.REPEAT_MODE].defaultZero(),
            shuffleMode = preferences[PreferencesKey.SHUFFLE_MODE].defaultFalse(),
        )
    }

    suspend fun setRecentPlaylistId(id: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.RECENT_PLAYLIST_ID] = id
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