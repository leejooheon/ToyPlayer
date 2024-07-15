package com.jooheon.toyplayer.domain.usecase.music.list

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.repository.MusicListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MusicListUseCaseImpl(
    private val musicListRepository: MusicListRepository,
): MusicListUseCase {
    private val _musicListType = MutableStateFlow(MusicListType.All)
    override val musicListType = _musicListType.asStateFlow()

    init {
        _musicListType.tryEmit(getMusicListType())
    }

    override suspend fun getAllSongList(): List<Song> {
        val local = getLocalSongList()
        val stream = getStreamingUrlList()
        val asset = getSongListFromAsset()
        val allSongs = local + stream + asset

        return allSongs
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