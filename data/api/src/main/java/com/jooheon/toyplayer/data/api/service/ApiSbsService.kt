package com.jooheon.toyplayer.data.api.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiSbsService {
    @GET("livestream/{channel_code}/{channel_name}")
    suspend fun getStreamUrl(
        @Path("channel_code") channelCode: String,
        @Path("channel_name") channelName: String,
        @Query("protocol") protocol: String = "hls",
        @Query("ssl") ssl: String = "Y"
    ): String
}