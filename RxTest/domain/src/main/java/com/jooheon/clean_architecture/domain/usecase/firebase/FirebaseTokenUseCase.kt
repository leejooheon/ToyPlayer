package com.jooheon.clean_architecture.domain.usecase.firebase

interface FirebaseTokenUseCase {
    fun getToken(): String
    fun setToken(token: String)
}