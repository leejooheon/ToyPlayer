package com.jooheon.clean_architecture.domain.usecase.music

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MusicUseCaseImpl(
    private val musicRepository: MusicRepository,
): MusicUseCase {
    override fun getAlbums(uri: String) = flow {
        emit(Resource.Loading)
        val data = musicRepository.getAlbums(uri)
        emit(data)
    }.flowOn(Dispatchers.IO)

    override fun getSongs(uri: String) = flow {
        emit(Resource.Loading)
        val data = musicRepository.getSongs(uri)
        emit(data)
    }.flowOn(Dispatchers.IO)

    override fun getSongsSync(uri: String) = flow {
        val data = musicRepository.getSongs(uri)
        emit(data)
    }.flowOn(Dispatchers.IO)
}