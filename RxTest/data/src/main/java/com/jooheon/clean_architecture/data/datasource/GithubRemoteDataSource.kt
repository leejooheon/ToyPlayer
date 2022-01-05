package com.jooheon.clean_architecture.data.datasource

import android.util.Log
import com.jooheon.clean_architecture.data.api.GithubApi
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import io.reactivex.Single
import javax.inject.Inject

class GithubRemoteDataSource @Inject constructor(private val api: GithubApi) : BaseRemoteDataSource() {
    suspend fun getRepository(owner: String): Resource<List<Entity.Repository>> {
        Log.d(TAG, "execute datasource")
        return safeApiCall { api.getRepository(owner) }
    }

    fun getProjects(owner: String): Single<List<Entity.Projects>> {
        return api.getProjects(owner)
    }
    companion object {
        val TAG = GithubRemoteDataSource::class.simpleName
    }
}

//@SuppressLint("CheckResult") // TODO: 이 annotatiton이 뭐지>??
//fun getRepository(
//    apiSource: GithubApiDataSource,
//    owner: String,
//    onResult: (result: ResultState<List<Entity.Repository>>) -> Unit
//) {
//    apiSource.getRepository(owner).subscribe({ data ->
//        onResult(ResultState.Success(data))
//    }, { throwable ->
//        onResult(ResultState.Error(throwable, null)) // FIXME: null을 다른거루??, composite disposable은 안넣음??
//    })
//}
//
//interface GithubApiDataSource : BaseDataSource {
//
//    fun getRepository(owner: String) : Single<List<Entity.Repository>>
//
//}