package com.jooheon.clean_architecture.data.dao.parkingspot

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jooheon.clean_architecture.domain.entity.Entity.ParkingSpot

/**
 * TODO
    - Domain Layer에는 어떠한 dependency도 있어서는 안되어서 room에 대한 dependency때문에 entity를 2개로 나눴다..
    추후에 개선을 해야할것같다.
**/

@Entity
data class ParkingSpotEntity(
    val lat: Double,
    val lng: Double,
    @PrimaryKey val id: Int? = null
)

fun ParkingSpotEntity.toParkingSpot(): ParkingSpot {
    return ParkingSpot(
        lat = lat,
        lng = lng,
        id = id
    )
}

fun ParkingSpot.toParkingSpotEntity(): ParkingSpotEntity {
    return ParkingSpotEntity(
        lat = lat,
        lng = lng,
        id = id
    )
}