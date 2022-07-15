package com.jooheon.clean_architecture.presentation.view.temp

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.map.ParkingSpotUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EmptyParkingSpotUseCase: ParkingSpotUseCase {
    override fun insertParkingSpot(spot: Entity.ParkingSpot): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteParkingSpot(spot: Entity.ParkingSpot): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun getParkingSpots() = flow {
        val data: List<Entity.ParkingSpot> = emptyList()
        emit(Resource.Success(data))
    }
}