package com.example.rxtest.domain.repository

import com.example.rxtest.domain.common.ResultState
import com.example.rxtest.domain.entity.Entity
import io.reactivex.Single

interface GithubRepository: BaseRepository {
    fun getRepository(owner: String): Single<ResultState<List<Entity.Repository>>>
    fun getProjects(owner: String) : Single<ResultState<List<Entity.Projects>>>
}