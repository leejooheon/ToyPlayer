package com.jooheon.clean_architecture.domain.usecase.music.list

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.MusicListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicListUseCase(
    private val musicListRepository: MusicListRepository,
): IMusicListUseCase {
    private val _localSongList = MutableStateFlow<List<Song>>(emptyList())
    val localSongList = _localSongList.asStateFlow()

    private val _streamingSongList = MutableStateFlow<List<Song>>(emptyList())
    val streamingSongList = _streamingSongList.asStateFlow()

    private val _assetSongList = MutableStateFlow<List<Song>>(emptyList())
    val assetSongList = _assetSongList.asStateFlow()

    private val _musicListType = MutableStateFlow(MusicListType.All)
    val musicListType = _musicListType.asStateFlow()

    init {
        _musicListType.tryEmit(getMusicListType())
    }

    override suspend fun loadSongList(storageUrl: String) {
        loadStreamingUrlList()
        loadSongListFromAsset()
        loadLocalSongList(storageUrl)
    }

    override suspend fun loadLocalSongList(storageUrl: String) = withContext(Dispatchers.IO) {
        val resource = musicListRepository.getLocalMusicList(storageUrl)
        val list = when(resource) {
            is Resource.Success -> resource.value
            is Resource.Failure -> emptyList()
            else -> return@withContext
        }
        _localSongList.emit(list)
    }

    override suspend fun loadStreamingUrlList() = withContext(Dispatchers.IO) {
        val resource = musicListRepository.getStreamingMusicList()
        val list = when(resource) {
            is Resource.Success -> resource.value
            is Resource.Failure -> emptyList()
            else -> return@withContext
        }
        _streamingSongList.emit(list)
    }

    override suspend fun loadSongListFromAsset() = withContext(Dispatchers.IO) {
        val resource = musicListRepository.getMusicFromAsset()
        val list = when(resource) {
            is Resource.Success -> resource.value
            is Resource.Failure -> emptyList()
            else -> return@withContext
        }

        _assetSongList.emit(list)
    }

    override fun getMusicListType(): MusicListType {
        return musicListRepository.getMusicListType()
    }

    override fun setMusicListType(musicListType: MusicListType) {
        musicListRepository.setMusicListType(musicListType)
        _musicListType.tryEmit(musicListType)
    }
}