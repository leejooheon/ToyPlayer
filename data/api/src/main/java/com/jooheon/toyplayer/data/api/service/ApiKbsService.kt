package com.jooheon.toyplayer.data.api.service

import com.jooheon.toyplayer.data.api.response.KbsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiKbsService {
    @GET("landing/live/channel_code/{code}")
    suspend fun getStreamUrl(@Path("code") code: String): KbsResponse
}