package com.jooheon.clean_architecture.data.repository

import com.jooheon.clean_architecture.data.local.AppPreferences
import com.jooheon.clean_architecture.domain.repository.FirebaseTokenRepository

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