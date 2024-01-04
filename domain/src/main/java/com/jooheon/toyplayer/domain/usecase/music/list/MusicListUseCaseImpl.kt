package com.jooheon.toyplayer.domain.usecase.music.list

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.repository.MusicListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicListUseCaseImpl(
    private val musicListRepository: MusicListRepository,
): MusicListUseCase {
    private val _localSongList = MutableStateFlow<List<Song>>(emptyList())
    override val localSongList = _localSongList.asStateFlow()

    private val _streamingSongList = MutableStateFlow<List<Song>>(emptyList())
    override val streamingSongList = _streamingSongList.asStateFlow()

    private val _assetSongList = MutableStateFlow<List<Song>>(emptyList())
    override val assetSongList = _assetSongList.asStateFlow()

    private val _musicListType = MutableStateFlow(MusicListType.All)
    override val musicListType = _musicListType.asStateFlow()

    init {
        _musicListType.tryEmit(getMusicListType())
    }

    override suspend fun initialize() {
        val localSongList = getLocalSongList()
        val streamingSongList = getStreamingUrlList()
        val assetSongList = getSongListFromAsset()

        _localSongList.tryEmit(localSongList)
        _streamingSongList.tryEmit(streamingSongList)
        _assetSongList.tryEmit(assetSongList)
    }

    override suspend fun getLocalSongList(): List<Song> {
        val resource = musicListRepository.getLocalMusicList()

        return if (resource is Resource.Success) resource.value.toList()
        else emptyList()
    }

    override suspend fun getStreamingUrlList(): List<Song> {
        val resource = musicListRepository.getStreamingMusicList()

        return if (resource is Resource.Success) resource.value.toList()
        else emptyList()
    }

    override suspend fun getSongListFromAsset(): List<Song> {
        val resource = musicListRepository.getMusicFromAsset()

        return if (resource is Resource.Success) resource.value.toList()
        else emptyList()
    }

    override fun getMusicListType(): MusicListType {
        return musicListRepository.getMusicListType()
    }

    override fun setMusicListType(musicListType: MusicListType) {
        musicListRepository.setMusicListType(musicListType)
        _musicListType.tryEmit(musicListType)
    }
}