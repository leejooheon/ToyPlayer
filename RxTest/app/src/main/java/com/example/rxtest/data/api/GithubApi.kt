package com.example.rxtest.data.api

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GithubApi {
    @GET("users/{owner}/repos") //  {owner}(사용자)의 레파지토리들을 반환
    fun getRepository(@Path("owner") owner: String) : Single<List<Dto.Repository>>

    @POST("orgs/{owner}/projects")
    fun getProjects(@Path("owner") owner: String) : Single<List<Dto.Projects>>

    sealed class Dto {
        data class Repository(
            @SerializedName("name") val name: String,
            @SerializedName("id") val id: String,
            @SerializedName("created_at") val date: String,
            @SerializedName("html_url") val url: String
        ) : Dto()

        data class Projects(
            @SerializedName("accept") val accept: String,
            @SerializedName("org") val org: String,
            @SerializedName("state") val state: String,
            @SerializedName("per_page") val perPage: String,
            @SerializedName("page") val page: String
        ) : Dto()

        data class Markdown(
            @SerializedName("html") val html: String
        ) : Dto()
    }
}