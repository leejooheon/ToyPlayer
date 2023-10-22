package com.jooheon.clean_architecture.domain.usecase.music.list

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.MusicListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicListUseCase(
    private val applicationScope: CoroutineScope,
    private val musicListRepository: MusicListRepository,
): IMusicListUseCase {
    private val _localSongList = MutableStateFlow<List<Song>>(emptyList())
    val localSongList = _localSongList.asStateFlow()

    private val _streamingSongList = MutableStateFlow<List<Song>>(emptyList())
    val streamingSongList = _streamingSongList.asStateFlow()

    private val _musicListType = MutableStateFlow(MusicListType.All)
    val musicListType = _musicListType.asStateFlow()

    init {
        _musicListType.tryEmit(getMusicListType())
    }

    override fun loadSongList(storageUrl: String) {
        loadLocalSongList(storageUrl)
        loadStreamingUrlList()
    }

    override fun loadLocalSongList(storageUrl: String) {
        applicationScope.launch {
            val resource = musicListRepository.getLocalMusicList(storageUrl)
            val list = when(resource) {
                is Resource.Success -> resource.value
                is Resource.Failure -> emptyList()
                else -> return@launch
            }
            _localSongList.tryEmit(list)
        }
    }

    override fun loadStreamingUrlList() {
        applicationScope.launch {
            val resource = musicListRepository.getStreamingMusicList()
            val list = when(resource) {
                is Resource.Success -> resource.value
                is Resource.Failure -> emptyList()
                else -> return@launch
            }
            _streamingSongList.tryEmit(list)
        }
    }

    override fun getMusicListType(): MusicListType {
        return musicListRepository.getMusicListType()
    }

    override fun setMusicListType(musicListType: MusicListType) {
        musicListRepository.setMusicListType(musicListType)
        _musicListType.tryEmit(musicListType)
    }
}