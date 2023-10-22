package com.jooheon.clean_architecture.domain.usecase.subway

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.SubwayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SubwayUseCaseImpl(
    private val subwayRepository: SubwayRepository
): SubwayUseCase {
    override fun getStationInfo(stationName: String) = flow {
        emit(Resource.Loading)
        val response = subwayRepository.getStationInfo(stationName)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun getStationInfoSync(stationName: String): Resource<Entity.Station> {
        val response = subwayRepository.getStationInfo(stationName)
        return response
    }
}