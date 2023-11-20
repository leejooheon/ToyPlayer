package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity

interface SubwayRepository: BaseRepository {
    suspend fun getStationInfo(stationName: String): Resource<Entity.Station>
}