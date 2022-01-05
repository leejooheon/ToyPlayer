package com.example.rxtest.data.api

import com.jooheon.clean_architecture.domain.entity.Entity
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GithubApi {
    @GET("users/{owner}/repos") //  {owner}(사용자)의 레파지토리들을 반환
    suspend fun getRepository(@Path("owner") owner: String) : List<Entity.Repository>

    @POST("orgs/{owner}/projects")
    fun getProjects(@Path("owner") owner: String) : Single<List<Entity.Projects>>
}