package com.jooheon.toyplayer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jooheon.toyplayer.data.datastore.di.DataStoreQualifier
import com.jooheon.toyplayer.data.datastore.model.PlayerSettingsData
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerSettingsPreferencesDataSource @Inject constructor(
    @DataStoreQualifier.Playback private val dataStore: DataStore<Preferences>,
) {
    object PreferencesKey {
        internal val REPEAT_MODE = intPreferencesKey("REPEAT_MODE")
        internal val SHUFFLE_MODE = booleanPreferencesKey("SHUFFLE_MODE")
        internal val VOLUME = floatPreferencesKey("VOLUME")
        internal val EQUALIZER_PRESET = stringPreferencesKey("EQUALIZER_PRESET")
        internal val CHANNEL_BALANCE = floatPreferencesKey("CHANNEL_BALANCE")
        internal val BASS_BOOST = intPreferencesKey("BASS_BOOST")
    }

    val playerSettingsData = dataStore.data.map { preferences ->
        PlayerSettingsData(
            repeatMode = preferences[PreferencesKey.REPEAT_MODE].defaultZero(),
            shuffleMode = preferences[PreferencesKey.SHUFFLE_MODE].defaultFalse(),
            preset = preferences[PreferencesKey.EQUALIZER_PRESET].defaultEmpty(),
            volume = preferences[PreferencesKey.VOLUME].default(1f),
            channelBalance = preferences[PreferencesKey.CHANNEL_BALANCE].defaultZero(),
            bassBoost = preferences[PreferencesKey.BASS_BOOST].defaultZero(),
        )
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
    suspend fun setEqualizerPreset(preset: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.EQUALIZER_PRESET] = preset
        }
    }
    suspend fun setVolume(volume: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.VOLUME] = volume
        }
    }
    suspend fun setChannelBalance(channelBalance: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.CHANNEL_BALANCE] = channelBalance
        }
    }
    suspend fun setBassBoost(percent: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.BASS_BOOST] = percent
        }
    }
}