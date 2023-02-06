package com.jooheon.clean_architecture.domain.usecase.subway

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow

interface SubwayUseCase: BaseUseCase {
    fun getStationInfo(stationName: String): Flow<Resource<Entity.Station>>
}