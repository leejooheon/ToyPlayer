package com.jooheon.toyplayer.domain.usecase.firebase

interface FirebaseTokenUseCase {
    fun getToken(): String
    fun setToken(token: String)
}