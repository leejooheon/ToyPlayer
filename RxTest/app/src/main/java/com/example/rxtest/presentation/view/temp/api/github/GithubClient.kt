package com.example.rxtest.presentation.view.temp.api.github

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class GithubClient {
    companion object {
        private const val BASE_URL = "https://api.github.com"

        fun getClient(): GithubService = Retrofit.Builder() .baseUrl(BASE_URL)
            .client(OkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // Retrofit Call Type을 Rx의 Single 또는 Observable로 변환
            .addConverterFactory(GsonConverterFactory.create()) // Response를 사용자 정의 Type으로 변환
            .build()
            .create(GithubService::class.java)
    }
}