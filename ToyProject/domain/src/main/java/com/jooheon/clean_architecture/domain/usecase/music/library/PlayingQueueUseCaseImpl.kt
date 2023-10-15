package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.library.PlayingQueueRepository
import kotlinx.coroutines.flow.flow

class PlayingQueueUseCaseImpl(
    private val playingQueueRepository: PlayingQueueRepository,
): PlayingQueueUseCase {
    override suspend fun getPlayingQueuePosition(): Int {
        return playingQueueRepository.getPlayingQueuePosition()
    }

    override suspend fun setPlayingQueuePosition(position: Int) {
        playingQueueRepository.setPlayingQueuePosition(position)
    }

    override suspend fun getPlayingQueue() = flow {
        emit(Resource.Loading)
        val resource = playingQueueRepository.getPlayingQueue()
        emit(resource)
    }

    override suspend fun updatePlayingQueue(vararg song: Song): Boolean {
        val newPlayingQueue = mutableListOf<Song>().apply {
            addAll(song)
        }
        playingQueueRepository.clear()
        playingQueueRepository.updatePlayingQueue(newPlayingQueue)

        return true
    }

    override suspend fun clear() {
        playingQueueRepository.clear()
    }
}