package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.ResultState
import com.jooheon.clean_architecture.domain.entity.Entity
import io.reactivex.Single

interface GithubRepository: BaseRepository {
    suspend fun getRepository(owner: String): Resource<List<Entity.Repository>>
    fun getProjects(owner: String) : Single<ResultState<List<Entity.Projects>>>
}