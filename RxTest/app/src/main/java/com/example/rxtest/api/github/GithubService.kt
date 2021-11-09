package com.example.rxtest.api.github

import com.example.rxtest.api.github.model.GithubRepository
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {
    @GET("users/{owner}/repos") //  {owner}(사용자)의 레파지토리들을 반환
    fun getRepos(@Path("owner") owner: String) : Single<List<GithubRepository>>
}