package com.jooheon.clean_architecture.data.datasource

import android.util.Log
import com.jooheon.clean_architecture.data.api.WikipediaApi
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import javax.inject.Inject

class WikipediaRemoteDataSource @Inject constructor(private val api: WikipediaApi): BaseRemoteDataSource() {
    suspend fun getDetail(word: String): Resource<String> {
        Log.d(GithubRemoteDataSource.TAG, "execute getDetail")
        return safeApiCall { api.getDetailInfo(word)}
    }

    suspend fun getSummary(word: String): Resource<Entity.Summary> {
        Log.d(GithubRemoteDataSource.TAG, "execute getDetail")
        return safeApiCall { api.getSummary(word)}
    }

    suspend fun getRelated(word: String): Resource<Entity.Related> {
        Log.d(GithubRemoteDataSource.TAG, "execute getDetail")
        return safeApiCall { api.getRelated(word)}
    }
}