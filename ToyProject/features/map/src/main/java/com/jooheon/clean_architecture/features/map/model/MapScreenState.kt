package com.jooheon.clean_architecture.features.map.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.maps.android.compose.MapProperties
import com.jooheon.clean_architecture.domain.entity.Entity
import java.text.DecimalFormat

data class MapScreenState(
    val properties: MutableState<MapProperties> = mutableStateOf(MapProperties()),
    val parkingSpots: MutableState<List<Entity.ParkingSpot>?> = mutableStateOf(emptyList()),
    val isFalloutMap: MutableState<Boolean> = mutableStateOf(true)
) {
    companion object {
        val decimalFormat = DecimalFormat("#.####")

        val default = MapScreenState(
            properties = mutableStateOf(MapProperties()),
            parkingSpots = mutableStateOf(emptyList()),
            isFalloutMap = mutableStateOf(true)
        )
    }
}