package com.jooheon.clean_architecture.usecase.github

import com.jooheon.clean_architecture.common.Resource
import com.jooheon.clean_architecture.common.ResultState
import com.jooheon.clean_architecture.entity.Entity
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface GithubUseCase {
    fun getRepository(owner: String): Flow<Resource<List<Entity.Repository>>>
    fun getProjects(owner: String) : Single<ResultState<List<Entity.Projects>>>
}