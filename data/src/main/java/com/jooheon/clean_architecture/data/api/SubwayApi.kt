package com.jooheon.clean_architecture.data.api

import com.jooheon.clean_architecture.domain.entity.Entity
import retrofit2.http.GET
import retrofit2.http.Path

interface SubwayApi {
    @GET("{api_key}/json/realtimeStationArrival/0/5/{station_name}")
    suspend fun getSubway(
        @Path("api_key") apiKey: String,
        @Path("station_name") stationName: String
    ) : Entity.Station
}
