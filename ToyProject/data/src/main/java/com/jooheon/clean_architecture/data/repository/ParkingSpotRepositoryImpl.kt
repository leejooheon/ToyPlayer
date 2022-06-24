package com.jooheon.clean_architecture.data.repository

import com.jooheon.clean_architecture.data.dao.parkingspot.toParkingSpot
import com.jooheon.clean_architecture.data.dao.parkingspot.toParkingSpotEntity
import com.jooheon.clean_architecture.data.datasource.local.ParkingSpotDataSource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.ParkingSpotRepository

class ParkingSpotRepositoryImpl(
    private val parkingSpotDataSource: ParkingSpotDataSource,
): ParkingSpotRepository {
    override suspend fun insertParkingSpot(spot: Entity.ParkingSpot): Boolean {
        return parkingSpotDataSource.insertParkingSpot(spot.toParkingSpotEntity())
    }

    override suspend fun deleteParkingSpot(spot: Entity.ParkingSpot): Boolean {
        return parkingSpotDataSource.deleteParkingSpot(spot.toParkingSpotEntity())
    }

    override fun getParkingSpots(): List<Entity.ParkingSpot> {
        return parkingSpotDataSource.getParkingSpots().map {
            it.toParkingSpot()
        }
    }
}