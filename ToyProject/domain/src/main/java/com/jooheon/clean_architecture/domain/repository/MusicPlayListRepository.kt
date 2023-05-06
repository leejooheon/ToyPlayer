package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song

interface MusicPlayListRepository {
    suspend fun getLocalSongList(uri: String): Resource<MutableList<Song>>
    suspend fun getStreamingUrlList(): Resource<MutableList<Song>>
}