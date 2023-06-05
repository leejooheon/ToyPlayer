package com.jooheon.clean_architecture.data.dao.parkingspot

import com.jooheon.clean_architecture.domain.common.Mapper
import com.jooheon.clean_architecture.domain.entity.Entity.ParkingSpot

class ParkingSpotMapper: Mapper<ParkingSpot, ParkingSpotEntity>() {
    override fun map(data: ParkingSpot): ParkingSpotEntity {
        return ParkingSpotEntity(
            id = data.id,
            lat = data.lat,
            lng = data.lng,
        )
    }

    override fun mapInverse(data: ParkingSpotEntity): ParkingSpot {
        return ParkingSpot(
            id = data.id,
            lat = data.lat,
            lng = data.lng,
        )
    }
}