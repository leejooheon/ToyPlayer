package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.entity.Entity

interface ParkingSpotRepository {
    suspend fun insertParkingSpot(spot: Entity.ParkingSpot): Boolean
    suspend fun deleteParkingSpot(spot: Entity.ParkingSpot): Boolean
    fun getParkingSpots(): List<Entity.ParkingSpot>
}