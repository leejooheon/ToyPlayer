package com.example.rxtest.data.api

import com.example.rxtest.domain.common.BaseResponse
import com.example.rxtest.domain.entity.Entity
import com.google.gson.annotations.SerializedName
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