package com.jooheon.clean_architecture.presentation.view.main.map

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.maps.android.compose.MapProperties
import com.jooheon.clean_architecture.domain.entity.Entity

data class MapState(
    val properties: MapProperties = MapProperties(),
    var parkingSpots: MutableState<List<Entity.ParkingSpot>?> = mutableStateOf(emptyList()),
    val isFalloutMap: Boolean = false
)