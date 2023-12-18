package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.library.PlayingQueueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlayingQueueUseCaseImpl(
    private val playingQueueRepository: PlayingQueueRepository,
): PlayingQueueUseCase {
    override suspend fun repeatModeChanged(repeatMode: Int) {
        playingQueueRepository.setRepeatMode(repeatMode)
    }
    override suspend fun shuffleModeChanged(shuffleEnabled: Boolean) {
        playingQueueRepository.setShuffleMode(shuffleEnabled)
    }
    override suspend fun repeatMode() = playingQueueRepository.getRepeatMode()

    override suspend fun shuffleMode() = playingQueueRepository.getShuffleMode()

    override suspend fun getPlayingQueuePosition(): Int {
        return playingQueueRepository.getPlayingQueuePosition()
    }
    override suspend fun setPlayingQueuePosition(position: Int) {
        playingQueueRepository.setPlayingQueuePosition(position)
    }

    override suspend fun playingQueue() = flow {
        emit(Resource.Loading)
        val resource = withContext(Dispatchers.IO) {
            playingQueueRepository.getPlayingQueue()
        }
        emit(resource)
    }

    override suspend fun getPlayingQueue(): List<Song> = withContext(Dispatchers.IO) {
        val resource = playingQueueRepository.getPlayingQueue()
        return@withContext (resource as? Resource.Success)?.value.defaultEmpty()
    }

    override suspend fun updatePlayingQueue(songs: List<Song>): Boolean {
        playingQueueRepository.clear()
        playingQueueRepository.updatePlayingQueue(songs)

        return true
    }

    override suspend fun clear() {
        playingQueueRepository.clear()
    }
}