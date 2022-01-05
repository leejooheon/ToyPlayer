package com.example.rxtest.domain.usecase.github

import com.example.rxtest.domain.common.BaseResponse
import com.example.rxtest.domain.common.Resource
import com.example.rxtest.domain.common.ResultState
import com.example.rxtest.domain.entity.Entity
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface GithubUseCase {
    fun getRepository(owner: String): Flow<Resource<List<Entity.Repository>>>
    fun getProjects(owner: String) : Single<ResultState<List<Entity.Projects>>>
}