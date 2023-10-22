package com.jooheon.clean_architecture.data.dao.parkingspot

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jooheon.clean_architecture.domain.entity.Entity.ParkingSpot

@Entity
data class ParkingSpotEntity(
    @PrimaryKey val id: Int? = null,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double,
)