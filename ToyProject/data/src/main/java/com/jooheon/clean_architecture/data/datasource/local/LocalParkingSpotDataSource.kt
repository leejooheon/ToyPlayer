package com.jooheon.clean_architecture.data.datasource.local

import com.jooheon.clean_architecture.data.dao.parkingspot.ParkingSpotDao
import com.jooheon.clean_architecture.data.dao.parkingspot.ParkingSpotMapper
import com.jooheon.clean_architecture.domain.entity.Entity.ParkingSpot
import javax.inject.Inject

class LocalParkingSpotDataSource @Inject constructor(
    private val parkingSpotDao: ParkingSpotDao,
    private val parkingSpotMapper: ParkingSpotMapper,
): BaseLocalDataSource {
    suspend fun insertParkingSpot(spot: ParkingSpot): Boolean {
        val entity = parkingSpotMapper.map(spot)
        parkingSpotDao.insertParkingSpot(entity)
        return true
    }

    suspend fun deleteParkingSpot(spot: ParkingSpot): Boolean {
        val entity = parkingSpotMapper.map(spot)
        parkingSpotDao.deleteParkingSpot(entity)
        return true
    }

    fun getParkingSpots(): List<ParkingSpot> {
        return parkingSpotDao.getParkingSpots().map {
            parkingSpotMapper.mapInverse(it)
        }
    }
}