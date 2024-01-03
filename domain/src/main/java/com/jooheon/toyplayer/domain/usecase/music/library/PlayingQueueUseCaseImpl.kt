package com.jooheon.toyplayer.domain.usecase.music.library

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.repository.library.PlayingQueueRepository
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

    override suspend fun getPlayingQueueKey(): Long {
        return playingQueueRepository.getPlayingQueueKey()
    }

    override suspend fun setPlayingQueueKey(key: Long) {
        playingQueueRepository.setPlayingQueueKey(key)
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
        clear()
        playingQueueRepository.updatePlayingQueue(songs)

        return true
    }

    override suspend fun clear() {
        setPlayingQueueKey(-1L)
        playingQueueRepository.clear()
    }
}