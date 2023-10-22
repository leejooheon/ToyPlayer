package com.jooheon.clean_architecture.data.api

import com.jooheon.clean_architecture.domain.entity.Entity
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaApi {
    @GET("api/rest_v1/page/html/{word}") //  {owner}(사용자)의 레파지토리들을 반환
    suspend fun getDetailInfo(@Path("word") word: String) : String

    @GET("api/rest_v1/page/summary/{word}")
    suspend fun getSummary(@Path("word") word: String): Entity.Summary

    @GET("api/rest_v1/page/related/{word}")
    suspend fun getRelated(@Path("word") word: String): Entity.Related
}