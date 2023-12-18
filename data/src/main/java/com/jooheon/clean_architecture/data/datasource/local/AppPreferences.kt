package com.jooheon.clean_architecture.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.SkipForwardBackward
import javax.inject.Inject


class AppPreferences @Inject constructor(private val context: Context) {

    companion object {
        private const val APP_PREFERENCES_NAME = "APP-Cache"
        private const val SESSION_PREFERENCES_NAME = "APP-UserCache"
        private const val MODE = Context.MODE_PRIVATE

        private val LAST_PLAYING_QUEUE_POSITION = Pair("LAST_PLAYING_QUEUE_POSITION", -1L)
        private val USER_DATA = Pair("USER_DATA", "")
        private val MUSIC_LIST_TYPE = Pair("MUSIC_LIST_TYPE", 0)
        private val FIREBASE_TOKEN = Pair("FIREBASE_TOKEN", "")

        private val REPEAT_MODE = Pair("REPEAT_MODE", RepeatMode.REPEAT_OFF.ordinal)
        private val SHUFFLE_MODE = Pair("SHUFFLE_MODE", ShuffleMode.NONE.ordinal)
        private val SKIP_DURATION = Pair("SKIP_DURATION", SkipForwardBackward.FIVE_SECOND.ordinal)
        private val LANGUAGE = Pair("LANGUAGE", Entity.SupportLaunguages.AUTO.ordinal)
        private val THEME = Pair("THEME", Entity.SupportThemes.LIGHT.ordinal)
    }

    private val appPreferences: SharedPreferences = context.getSharedPreferences(
        APP_PREFERENCES_NAME, MODE
    )
    private val sessionPreferences: SharedPreferences = context.getSharedPreferences(
        SESSION_PREFERENCES_NAME, MODE
    )

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var musicListType: Int
        get() {
            return appPreferences.getInt(MUSIC_LIST_TYPE.first, MUSIC_LIST_TYPE.second)
        }
        set(value) = appPreferences.edit {
            it.putInt(MUSIC_LIST_TYPE.first, value)
        }

    var lastPlayingQueuePosition: Long
        get() {
            return appPreferences.getLong(LAST_PLAYING_QUEUE_POSITION.first, LAST_PLAYING_QUEUE_POSITION.second)
        }
        set(value) = appPreferences.edit {
            it.putLong(LAST_PLAYING_QUEUE_POSITION.first, value)
        }

    var firebaseToken: String?
        get() {
            return sessionPreferences.getString(FIREBASE_TOKEN.first, FIREBASE_TOKEN.second)
        }
        set(value) = sessionPreferences.edit {
            it.putString(FIREBASE_TOKEN.first, value)
        }

    var repeatMode: RepeatMode
        get() {
            val ordinal = sessionPreferences.getInt(REPEAT_MODE.first, REPEAT_MODE.second)
            return RepeatMode.entries[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(REPEAT_MODE.first, value.ordinal)
        }

    var shuffleMode: ShuffleMode
        get() {
            val ordinal = sessionPreferences.getInt(SHUFFLE_MODE.first, SHUFFLE_MODE.second)
            return ShuffleMode.entries[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(SHUFFLE_MODE.first, value.ordinal)
        }

    var skipForwardBackward: SkipForwardBackward
        get() {
            val ordinal = sessionPreferences.getInt(SKIP_DURATION.first, SKIP_DURATION.second)
            return SkipForwardBackward.entries[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(SKIP_DURATION.first, value.ordinal)
        }

    var language: Entity.SupportLaunguages
        get() {
            val ordinal = sessionPreferences.getInt(LANGUAGE.first, LANGUAGE.second)
            return Entity.SupportLaunguages.entries[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(LANGUAGE.first, value.ordinal)
        }

    var theme: Entity.SupportThemes
        get() {
            val ordinal = sessionPreferences.getInt(THEME.first, THEME.second)
            return Entity.SupportThemes.entries[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(THEME.first, value.ordinal)
        }

    fun clearPreferences() {
        sessionPreferences.edit {
            it.clear().apply()
        }
    }
}