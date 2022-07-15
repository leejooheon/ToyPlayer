package com.jooheon.clean_architecture.presentation.view.main.map

import com.google.android.gms.maps.model.LatLng
import com.jooheon.clean_architecture.domain.entity.Entity

sealed class MapEvent {
    object ToggleFalloutMap: MapEvent()
    data class OnMapLongClick(val latLng: LatLng): MapEvent()
    data class OnInfoWindowLongClick(val spot: Entity.ParkingSpot): MapEvent()
    data class OnLocationPermissionChanged(val isGranted: Boolean): MapEvent()
}