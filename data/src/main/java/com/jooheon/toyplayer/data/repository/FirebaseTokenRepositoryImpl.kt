package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.domain.repository.FirebaseTokenRepository

class FirebaseTokenRepositoryImpl(
    private val appPreferences: AppPreferences
): FirebaseTokenRepository {
    override fun getToken(): String {
        appPreferences.firebaseToken?.let {
            return it
        }
        return "Nothing"
    }

    override fun setToken(token: String) {
        appPreferences.firebaseToken = token
    }
}