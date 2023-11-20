package com.jooheon.clean_architecture.domain.usecase.map

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.ParkingSpotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ParkingSpotUseCaseImpl(
    private val parkingSpotRepository: ParkingSpotRepository
): ParkingSpotUseCase {
    override fun insertParkingSpot(spot: Entity.ParkingSpot) = flow {
        emit(Resource.Loading)
        val result = parkingSpotRepository.insertParkingSpot(spot)
        emit(Resource.Success(result))
    }.flowOn(Dispatchers.IO)

    override fun deleteParkingSpot(spot: Entity.ParkingSpot) = flow {
        emit(Resource.Loading)
        val result = parkingSpotRepository.deleteParkingSpot(spot)
        emit(Resource.Success(result))
    }.flowOn(Dispatchers.IO)

    override fun getParkingSpots() = flow {
        val result = parkingSpotRepository.getParkingSpots()
        emit(Resource.Success(result))
    }.flowOn(Dispatchers.IO)
}