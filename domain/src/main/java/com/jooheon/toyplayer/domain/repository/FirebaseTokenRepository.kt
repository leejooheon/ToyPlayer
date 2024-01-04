package com.jooheon.toyplayer.domain.repository

interface FirebaseTokenRepository {
    fun setToken(token: String)
    fun getToken(): String
}