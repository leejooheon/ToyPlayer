package com.jooheon.clean_architecture.domain.usecase.github

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow

interface GithubUseCase: BaseUseCase {
    fun getRepository(owner: String): Flow<Resource<List<Entity.Repository>>>
    fun getBranch(owner: String, repository: String): Flow<Resource<List<Entity.Branch>>>
    fun getCommit(owner: String, repository: String): Flow<Resource<List<Entity.Commit>>>
    fun getBranchAndCommit(owner: String, repository: String): Flow<Pair<Resource<List<Entity.Branch>>,Resource<List<Entity.Commit>>>>
}