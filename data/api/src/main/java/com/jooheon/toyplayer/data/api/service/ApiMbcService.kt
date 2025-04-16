package com.jooheon.toyplayer.data.api.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiMbcService {
    @GET("aacplay.ashx") // ?agent=webapp&channel=[채널코드]
    suspend fun getStreamUrl(
        @Query("agent") agent: String = "webapp",
        @Query("channel") channel: String,
    ): String
}