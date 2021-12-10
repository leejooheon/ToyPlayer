package com.example.rxtest.data.datasource

import com.example.rxtest.data.api.GithubApi
import com.example.rxtest.data.common.extension.applyIoScheduler
import com.example.rxtest.data.mapper.map
import com.example.rxtest.domain.entity.Entity
import io.reactivex.Single

class GithubApiDataSource(private val api: GithubApi) : BaseDataSource {
    fun getRepository(owner: String): Single<List<Entity.Repository>> =
        api.getRepository(owner)
            .applyIoScheduler()
            .map { item -> item.map { it.map() } }

    fun getProjects(owner: String): Single<List<Entity.Projects>> =
        api.getProjects(owner)
            .applyIoScheduler()
            .map { item -> item.map { it.map() } }
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