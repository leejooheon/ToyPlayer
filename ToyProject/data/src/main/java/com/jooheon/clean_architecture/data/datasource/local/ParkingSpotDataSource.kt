package com.jooheon.clean_architecture.data.datasource.local

import com.jooheon.clean_architecture.data.dao.parkingspot.ParkingSpotDao
import com.jooheon.clean_architecture.data.dao.parkingspot.ParkingSpotEntity
import javax.inject.Inject

class ParkingSpotDataSource @Inject constructor(
    private val parkingSpotDao: ParkingSpotDao,
): BaseLocalDataSource {
    suspend fun insertParkingSpot(spot: ParkingSpotEntity): Boolean {
        parkingSpotDao.insertParkingSpot(spot)
        return true
    }

    suspend fun deleteParkingSpot(spot: ParkingSpotEntity): Boolean {
        parkingSpotDao.deleteParkingSpot(spot)
        return true
    }

    fun getParkingSpots(): List<ParkingSpotEntity> {
        return parkingSpotDao.getParkingSpots()
    }
}