package com.jooheon.clean_architecture.repository

import com.jooheon.clean_architecture.common.Resource
import com.jooheon.clean_architecture.common.ResultState
import com.jooheon.clean_architecture.entity.Entity
import io.reactivex.Single

interface GithubRepository: BaseRepository {
    suspend fun getRepository(owner: String): Resource<List<Entity.Repository>>
    fun getProjects(owner: String) : Single<ResultState<List<Entity.Projects>>>
}