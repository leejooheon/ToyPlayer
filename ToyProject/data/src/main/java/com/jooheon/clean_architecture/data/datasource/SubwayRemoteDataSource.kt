package com.jooheon.clean_architecture.data.datasource

import com.jooheon.clean_architecture.data.api.SubwayApi
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import javax.inject.Inject

class SubwayRemoteDataSource @Inject constructor(private val api: SubwayApi): BaseRemoteDataSource() {
    suspend fun getStationInfo(stationName: String): Resource<Entity.Station> {
        return safeApiCall {
            api.getSubway(
                apiKey = API_KEY,
                stationName = stationName
            )
        }
    }

    companion object {
        const val API_KEY = "sample"
    }
}