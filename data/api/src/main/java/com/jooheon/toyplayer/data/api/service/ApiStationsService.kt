package com.jooheon.toyplayer.data.api.service

import com.jooheon.toyplayer.data.api.response.StationResponse
import com.jooheon.toyplayer.data.api.response.StreamResponse
import retrofit2.http.GET

interface ApiStationsService {
    @GET("/app/main/data/api/src/main/assets/kbs_stations.json")
    suspend fun getKbsStations(): List<StationResponse>

    @GET("/app/main/data/api/src/main/assets/mbc_stations.json")
    suspend fun getMbcStations(): List<StationResponse>

    @GET("/app/main/data/api/src/main/assets/sbs_stations.json")
    suspend fun getSbsStations(): List<StationResponse>

    @GET("/app/main/data/api/src/main/assets/etc_stations.json")
    suspend fun getEtcStations(): List<StationResponse>

    @GET("/app/main/data/api/src/main/assets/stream_stations.json")
    suspend fun getStreamStations(): List<StreamResponse>
}