package com.jooheon.clean_architecture.data.repository

import com.jooheon.clean_architecture.data.datasource.SubwayRemoteDataSource
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.SubwayRepository

class SubwayRepositoryImpl(
    private val subwayRemoteDataSource: SubwayRemoteDataSource,
): SubwayRepository {
    override suspend fun getStationInfo(stationName: String): Resource<Entity.Station> {
        return subwayRemoteDataSource.getStationInfo(stationName)
    }
}