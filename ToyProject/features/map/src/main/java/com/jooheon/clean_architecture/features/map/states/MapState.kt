package com.jooheon.clean_architecture.features.map.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.maps.android.compose.MapProperties
import com.jooheon.clean_architecture.domain.entity.Entity

data class MapState(
    val properties: MutableState<MapProperties> = mutableStateOf(MapProperties()),
    val parkingSpots: MutableState<List<Entity.ParkingSpot>?> = mutableStateOf(emptyList()),
    val isFalloutMap: MutableState<Boolean> = mutableStateOf(true)
)