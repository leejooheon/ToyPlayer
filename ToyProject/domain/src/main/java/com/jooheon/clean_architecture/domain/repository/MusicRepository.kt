package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity

interface MusicRepository: BaseRepository {
    suspend fun getAlbums(uri: String): Resource<List<Entity.Song>>
    suspend fun getSongs(uri: String): Resource<List<Entity.Song>>
}