package com.jooheon.clean_architecture.domain.usecase.wikipedia

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.WikipediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class WikipediaUseCaseImpl(
    private val wikipediaRepository: WikipediaRepository
): WikipediaUseCase {

    override suspend fun getDetail(word: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        val response = wikipediaRepository.getDetail(word)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun getSummary(word: String): Flow<Resource<Entity.Summary>> = flow {
        emit(Resource.Loading)
        val response = wikipediaRepository.getSummary(word)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun getRelated(word: String): Flow<Resource<Entity.Related>> = flow {
        emit(Resource.Loading)
        val response = wikipediaRepository.getRelated(word)
        emit(response)
    }.flowOn(Dispatchers.IO)

    companion object {
        val TAG = WikipediaUseCaseImpl::class.simpleName
    }
}