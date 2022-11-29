package com.jooheon.clean_architecture.domain.usecase.music

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow

interface MusicUseCase: BaseUseCase {
    fun getAlbums(uri: String): Flow<Resource<List<Entity.Song>>>
    fun getSongs(uri: String): Flow<Resource<List<Entity.Song>>>
    fun getSongsSync(uri: String): Flow<Resource<List<Entity.Song>>>
}