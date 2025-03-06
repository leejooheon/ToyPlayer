package com.jooheon.toyplayer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.jooheon.toyplayer.data.datastore.di.DataStoreQualifier
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.SupportThemes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSettingsPreferencesDataSource @Inject constructor(
    @DataStoreQualifier.Default private val dataStore: DataStore<Preferences>,
) {
    object PreferencesKey {
        internal val THEME = intPreferencesKey("THEME")
    }

    fun flowTheme(): Flow<SupportThemes> {
        return dataStore.data.map {
            val ordinal = it[PreferencesKey.THEME].defaultZero()
            SupportThemes.entries[ordinal]
        }
    }

    suspend fun updateTheme(theme: SupportThemes) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.THEME] = theme.ordinal
        }
    }
}
