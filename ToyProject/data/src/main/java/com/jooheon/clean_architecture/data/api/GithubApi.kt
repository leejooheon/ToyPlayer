package com.jooheon.clean_architecture.data.api

import com.jooheon.clean_architecture.domain.entity.Entity
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {
    @GET("users/{owner}/repos") //  {owner}(사용자)의 레파지토리들을 반환
    suspend fun getRepository(@Path("owner") owner: String) : List<Entity.Repository>

    @GET("repos/{owner}/{repository}/branches")
    suspend fun getBranch(@Path("owner") owner: String, @Path("repository") repository: String): List<Entity.Branch>

    @GET("repos/{owner}/{repository}/commits")
    suspend fun getCommit(@Path("owner") owner: String, @Path("repository") repository: String): List<Entity.Commit>
}