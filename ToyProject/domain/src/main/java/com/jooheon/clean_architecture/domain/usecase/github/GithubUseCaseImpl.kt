package com.jooheon.clean_architecture.domain.usecase.github

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.GithubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GithubUseCaseImpl(
    private val githubRepository: GithubRepository
): GithubUseCase {

    override fun getRepository(owner: String): Flow<Resource<List<Entity.Repository>>> {
        return flow {
//            Log.d(TAG, "usecase start")
            emit(Resource.Loading)
            val result = githubRepository.getRepository(owner)
            githubRepository
            when(result) {
                is Resource.Success -> {
                    // do something ...
//                    Log.d(TAG, result.value.toString())
                }

                is Resource.Failure -> {
                    // do something ...
//                    Log.d(TAG, "code: ${result.code}, msg: ${result.message}, status: {$result.failureStatus}")
                }

                is Resource.Default -> {
                    // do something ...
                }
            }
//            Log.d(TAG, "usecase end")
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    override fun getBranch(owner: String, repository: String): Flow<Resource<List<Entity.Branch>>> {
        return flow {
            emit(Resource.Loading)
            val result = githubRepository.getBranch(owner, repository)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    override fun getCommit(owner: String, repository: String): Flow<Resource<List<Entity.Commit>>> {
        return flow {
            emit(Resource.Loading)
            val result = githubRepository.getCommit(owner, repository)
            emit(result)
        }
        // flowOn affects the upstream flow ↑
        .flowOn(Dispatchers.IO) // main thread에서 call했지 flowOn을 사용하여 IO thread로 변경한다.
        // the downstream flow ↓ is not affected
    }

    override fun getBranchAndCommit(
        owner: String,
        repository: String
    ): Flow<Pair<Resource<List<Entity.Branch>>, Resource<List<Entity.Commit>>>> {
        return flow {
            emit(Pair(Resource.Loading, Resource.Loading))
            val commitResponse = githubRepository.getCommit(owner, repository)
            val branchResponse = githubRepository.getBranch(owner, repository)
            emit(Pair(branchResponse, commitResponse))
        }.flowOn(Dispatchers.IO)
    }
    // Pair<Resource<List<Entity.Branch>>, Resource<List<Entity.Commit>>>>
    companion object {
        val TAG = GithubUseCaseImpl::class.simpleName
    }
}