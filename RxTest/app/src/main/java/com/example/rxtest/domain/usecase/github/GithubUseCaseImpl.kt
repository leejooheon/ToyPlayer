package com.example.rxtest.domain.usecase.github

import android.annotation.SuppressLint
import android.util.Log
import com.example.rxtest.data.repository.GithubRepositoryImpl
import com.example.rxtest.domain.common.BaseResponse
import com.example.rxtest.domain.common.Resource
import com.example.rxtest.domain.common.ResultState
import com.example.rxtest.domain.entity.Entity
import com.example.rxtest.domain.repository.GithubRepository
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow

class GithubUseCaseImpl(
    private val repository: GithubRepository
): GithubUseCase {
    @SuppressLint("CheckResult")
    override fun getRepository(owner: String): Flow<Resource<List<Entity.Repository>>> {
        return flow {
            Log.d(TAG, "usecase start")
            emit(Resource.Loading)
            val result = repository.getRepository(owner)

            when(result) {
                is Resource.Success -> {
                    // do something ...
                    Log.d(TAG, result.value.toString())
                }

                is Resource.Failure -> {
                    // do something ...
                    Log.d(TAG, "code: ${result.code}, msg: ${result.message}, status: {$result.failureStatus}")
                }

                is Resource.Default -> {
                    // do something ...
                }
            }
            Log.d(TAG, "usecase end")
            emit(result)
        }.flowOn(Dispatchers.IO)
    }



    override fun getProjects(owner: String): Single<ResultState<List<Entity.Projects>>> =
        repository.getProjects(owner)


    companion object {
        val TAG = GithubUseCaseImpl::class.simpleName
    }
}