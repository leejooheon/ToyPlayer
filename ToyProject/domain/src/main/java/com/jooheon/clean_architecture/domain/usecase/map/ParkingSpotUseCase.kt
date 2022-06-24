package com.jooheon.clean_architecture.domain.usecase.map

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow

interface ParkingSpotUseCase: BaseUseCase {
    fun insertParkingSpot(spot: Entity.ParkingSpot): Flow<Resource<Boolean>>
    fun deleteParkingSpot(spot: Entity.ParkingSpot): Flow<Resource<Boolean>>
    fun getParkingSpots(): Flow<Resource<List<Entity.ParkingSpot>>>
}