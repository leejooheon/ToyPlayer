package com.example.rxtest.domain.usecase.github

import com.example.rxtest.data.api.GithubApi
import com.example.rxtest.domain.common.ResultState
import com.example.rxtest.domain.entity.Entity
import io.reactivex.Single

interface GithubUseCase {
    fun getRepository(owner: String): Single<ResultState<List<Entity.Repository>>>
    fun getProjects(owner: String) : Single<ResultState<List<Entity.Projects>>>
}