package com.jooheon.clean_architecture.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jooheon.clean_architecture.domain.entity.Entity
import javax.inject.Inject


class AppPreferences @Inject constructor(private val context: Context) {

    companion object {
        private const val APP_PREFERENCES_NAME = "APP-Cache"
        private const val SESSION_PREFERENCES_NAME = "APP-UserCache"
        private const val MODE = Context.MODE_PRIVATE

        private val USER_DATA = Pair("USER_DATA", "")
        private val FIRST_TIME = Pair("FIRST_TIME", true)
        private val FIREBASE_TOKEN = Pair("FIREBASE_TOKEN", "")

        private val REPEAT_MODE = Pair("REPEAT_MODE", Entity.RepeatMode.REPEAT_OFF.ordinal)
        private val SHUFFLE_MODE = Pair("SHUFFLE_MODE", Entity.ShuffleMode.NONE.ordinal)
        private val SKIP_DURATION = Pair("SKIP_DURATION", Entity.SkipForwardBackward.FIVE_SECOND.ordinal)
        private val LANGUAGE = Pair("LANGUAGE", Entity.SupportLaunguages.AUTO.ordinal)
        private val THEME = Pair("THEME", Entity.SupportThemes.AUTO.ordinal)
    }

    private val appPreferences: SharedPreferences = context.getSharedPreferences(APP_PREFERENCES_NAME, MODE)
    private val sessionPreferences: SharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES_NAME, MODE)

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var userData: Entity.User?
        get() {
            val value: String? = sessionPreferences.getString(USER_DATA.first, USER_DATA.second)
            return Gson().fromJson(value, Entity.User::class.java)
        }
        set(value) = sessionPreferences.edit {
            it.putString(USER_DATA.first, Gson().toJson(value))
        }

    val userToken: String?
        get() {
            val value: String? = sessionPreferences.getString(USER_DATA.first, USER_DATA.second)
            val user = Gson().fromJson(value, Entity.User::class.java)

            return if (user == null || user.token.isEmpty()) {
                null
            } else {
                user.token
            }
        }

    var isFirstTime: Boolean
        get() {
            return appPreferences.getBoolean(FIRST_TIME.first, FIRST_TIME.second)
        }
        set(value) = appPreferences.edit {
            it.putBoolean(FIRST_TIME.first, value)
        }

    var firebaseToken: String?
        get() {
            return sessionPreferences.getString(FIREBASE_TOKEN.first, FIREBASE_TOKEN.second)
        }
        set(value) = sessionPreferences.edit {
            it.putString(FIREBASE_TOKEN.first, value)
        }

    var repeatMode: Entity.RepeatMode
        get() {
            val ordinal = sessionPreferences.getInt(REPEAT_MODE.first, REPEAT_MODE.second)
            return Entity.RepeatMode.values()[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(REPEAT_MODE.first, value.ordinal)
        }

    var shuffleMode: Entity.ShuffleMode
        get() {
            val ordinal = sessionPreferences.getInt(SHUFFLE_MODE.first, SHUFFLE_MODE.second)
            return Entity.ShuffleMode.values()[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(SHUFFLE_MODE.first, value.ordinal)
        }

    var skipForwardBackward: Entity.SkipForwardBackward
        get() {
            val ordinal = sessionPreferences.getInt(SKIP_DURATION.first, SKIP_DURATION.second)
            return Entity.SkipForwardBackward.values()[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(SKIP_DURATION.first, value.ordinal)
        }

    var language: Entity.SupportLaunguages
        get() {
            val ordinal = sessionPreferences.getInt(LANGUAGE.first, LANGUAGE.second)
            return Entity.SupportLaunguages.values()[ordinal]
        }
        set(value) = sessionPreferences.edit {
            it.putInt(LANGUAGE.first, value.ordinal)
        }

    var theme: Entity.SupportThemes
        get() {
            val ordinal = sessionPreferences.getInt(THEME.first, THEME.second)
            return Entity.SupportThemes.values()[ordinal]
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