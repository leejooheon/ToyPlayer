package com.jooheon.clean_architecture.data.repository

import com.jooheon.clean_architecture.data.datasource.local.LocalParkingSpotDataSource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.ParkingSpotRepository

class ParkingSpotRepositoryImpl(
    private val localParkingSpotDataSource: LocalParkingSpotDataSource,
): ParkingSpotRepository {
    override suspend fun insertParkingSpot(spot: Entity.ParkingSpot): Boolean {
        return localParkingSpotDataSource.insertParkingSpot(spot)
    }

    override suspend fun deleteParkingSpot(spot: Entity.ParkingSpot): Boolean {
        return localParkingSpotDataSource.deleteParkingSpot(spot)
    }

    override fun getParkingSpots(): List<Entity.ParkingSpot> {
        return localParkingSpotDataSource.getParkingSpots()
    }
}