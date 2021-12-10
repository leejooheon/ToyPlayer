package com.example.rxtest.data.repository

import com.example.rxtest.data.datasource.GithubApiDataSource
import com.example.rxtest.domain.common.ResultState
import com.example.rxtest.domain.entity.Entity
import com.example.rxtest.domain.repository.GithubRepository
import io.reactivex.Single
import java.util.*

// Repository패턴은... 사용하는애가 db에서 가져오는지, api로 가져오는지 신경안써도
class GithubRepositoryImpl(
    private val apiSource: GithubApiDataSource,
    private val databaseSource: Objects // 현재 미구현 상태
): GithubRepository {
    override fun getRepository(owner: String): Single<ResultState<List<Entity.Repository>>> =
        apiSource.getRepository(owner).map {
            ResultState.Success(it) as ResultState<List<Entity.Repository>>
        }.onErrorReturn {
            ResultState.Error(it, null);
        }

    override fun getProjects(owner: String): Single<ResultState<List<Entity.Projects>>> =
        apiSource.getProjects(owner).map {
            ResultState.Success(it) as ResultState<List<Entity.Projects>>
        }.onErrorReturn {
            ResultState.Error(it, null)
        }
}