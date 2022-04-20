package com.jooheon.clean_architecture.domain.usecase.firebase

import com.jooheon.clean_architecture.domain.repository.FirebaseTokenRepository
import javax.inject.Inject

class FirebaseTokenUseCaseImpl(
    private val firebaseTokenRepository: FirebaseTokenRepository
): FirebaseTokenUseCase {
    override fun getToken(): String = firebaseTokenRepository.getToken()
    override fun setToken(token: String) = firebaseTokenRepository.setToken(token)
}