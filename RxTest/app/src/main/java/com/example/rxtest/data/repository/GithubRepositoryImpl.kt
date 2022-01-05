package com.example.rxtest.data.repository

import android.util.Log
import com.example.rxtest.data.datasource.GithubRemoteDataSource
import com.example.rxtest.data.datasource.TempDataSource
import com.example.rxtest.domain.common.Resource
import com.example.rxtest.domain.common.ResultState
import com.example.rxtest.domain.entity.Entity
import com.example.rxtest.domain.repository.GithubRepository
import io.reactivex.Single

// Repository패턴은... 사용하는애가 db에서 가져오는지, api로 가져오는지 신경안써도
class GithubRepositoryImpl(
    private val githubRemoteDataSource: GithubRemoteDataSource,
    private val databaseSource: TempDataSource
): GithubRepository {

    override suspend fun getRepository(owner: String): Resource<List<Entity.Repository>> {
        Log.d(TAG, "execute repository")
        return githubRemoteDataSource.getRepository(owner)
    }

    override fun getProjects(owner: String): Single<ResultState<List<Entity.Projects>>> =
        githubRemoteDataSource.getProjects(owner).map {
            ResultState.Success(it) as ResultState<List<Entity.Projects>>
        }.onErrorReturn {
            ResultState.Error(it, null)
        }


    companion object {
        val TAG = GithubRepositoryImpl::class.simpleName
    }
}