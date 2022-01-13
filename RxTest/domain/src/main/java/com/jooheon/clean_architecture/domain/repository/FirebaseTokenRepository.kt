package com.jooheon.clean_architecture.domain.repository

interface FirebaseTokenRepository {
    fun setToken(token: String)
    fun getToken(): String
}