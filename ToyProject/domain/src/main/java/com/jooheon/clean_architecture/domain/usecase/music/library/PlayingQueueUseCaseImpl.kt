package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.library.PlayingQueueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayingQueueUseCaseImpl(
    applicationScope: CoroutineScope,
    private val playingQueueRepository: PlayingQueueRepository,
): PlayingQueueUseCase {
    private val _currentPlayingQueue = MutableStateFlow<List<Song>>(emptyList())
    private var autoPlayModel: Pair<Boolean, Song>? = null

    init {
        applicationScope.launch {
            update()
        }
    }
    override fun playingQueue() = _currentPlayingQueue.asStateFlow()
    override suspend fun getAutoPlayWhenQueueChanged(): Pair<Boolean, Song>? {
        val model = autoPlayModel
        autoPlayModel = null

        return model
    }

    override suspend fun openQueue(
        vararg song: Song,
        autoPlayWhenQueueChanged: Boolean
    ) {
        val songs = mutableListOf<Song>().apply {
            addAll(song)
        }
        playingQueueRepository.updatePlayingQueue(songs)

        song.firstOrNull()?.let {
            autoPlayModel = Pair(autoPlayWhenQueueChanged, it)
        }

        update()
    }

    override suspend fun addToPlayingQueue(
        vararg song: Song,
        autoPlayWhenQueueChanged: Boolean
    ) {
        val songs = playingQueue().value

        val newSongs = songs.toMutableList().apply {
            addAll(song)
        }
        playingQueueRepository.updatePlayingQueue(newSongs)

        song.firstOrNull()?.let {
            autoPlayModel = Pair(autoPlayWhenQueueChanged, it)
        }

        update()
    }

    override suspend fun deletePlayingQueue(vararg song: Song) {
        autoPlayModel = null
        val songs = playingQueue().value

        val newSongs = songs.toMutableList().apply {
            removeAll(song.toSet())
        }
        playingQueueRepository.updatePlayingQueue(newSongs)
        update()
    }

    override suspend fun clear() {
        playingQueueRepository.clear()
    }

    private suspend fun update() {
        val resource = withContext(Dispatchers.IO) {
            playingQueueRepository.getPlayingQueue()
        }
        when(resource) {
            is Resource.Success -> _currentPlayingQueue.tryEmit(resource.value)
            else -> _currentPlayingQueue.tryEmit(emptyList())
        }
    }
}