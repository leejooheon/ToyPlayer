package com.jooheon.toyplayer.domain.usecase.firebase

import com.jooheon.toyplayer.domain.repository.FirebaseTokenRepository

class FirebaseTokenUseCaseImpl(
    private val firebaseTokenRepository: FirebaseTokenRepository
): FirebaseTokenUseCase {
    override fun getToken(): String = firebaseTokenRepository.getToken()
    override fun setToken(token: String) = firebaseTokenRepository.setToken(token)
}