package com.jooheon.clean_architecture.domain.usecase.music

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song

interface MusicPlayListUsecase {
    suspend fun getLocalSongList(uri: String): Resource<MutableList<Song>>
    suspend fun getStreamingUrlList(): Resource<MutableList<Song>>
}