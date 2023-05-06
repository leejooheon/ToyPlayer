package com.jooheon.clean_architecture.domain.usecase.music

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.MusicPlayListRepository

class MusicPlayListUsecaseImpl(
    private val musicPlayListRepository: MusicPlayListRepository
): MusicPlayListUsecase {

    override suspend fun getLocalSongList(uri: String): Resource<MutableList<Song>> {
        val resource = musicPlayListRepository.getLocalSongList(uri)
        return resource
    }

    override suspend fun getStreamingUrlList(): Resource<MutableList<Song>> {
        val resource = musicPlayListRepository.getStreamingUrlList()
        return resource
    }
}