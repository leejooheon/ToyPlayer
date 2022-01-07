package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity

interface GithubRepository: BaseRepository {
    suspend fun getRepository(owner: String): Resource<List<Entity.Repository>>
}