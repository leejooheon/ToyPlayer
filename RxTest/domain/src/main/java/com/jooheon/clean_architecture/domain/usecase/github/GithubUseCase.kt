package com.jooheon.clean_architecture.domain.usecase.github

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import kotlinx.coroutines.flow.Flow

interface GithubUseCase {
    fun getRepository(owner: String): Flow<Resource<List<Entity.Repository>>>
    fun getBranch(owner: String, repository: String): Flow<Resource<List<Entity.Branch>>>
}