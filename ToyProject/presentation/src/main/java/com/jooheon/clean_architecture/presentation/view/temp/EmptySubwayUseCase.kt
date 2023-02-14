package com.jooheon.clean_architecture.presentation.view.temp

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.subway.SubwayUseCase
import kotlinx.coroutines.flow.Flow

class EmptySubwayUseCase: SubwayUseCase {
    override fun getStationInfo(stationName: String): Flow<Resource<Entity.Station>> {
        TODO("Not yet implemented")
    }

    override suspend fun getStationInfoSync(stationName: String): Resource<Entity.Station> {
        TODO("Not yet implemented")
    }
}