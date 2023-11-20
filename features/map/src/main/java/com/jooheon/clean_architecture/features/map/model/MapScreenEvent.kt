package com.jooheon.clean_architecture.features.map.model

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.android.gms.maps.model.LatLng
import com.jooheon.clean_architecture.domain.entity.Entity

sealed class MapScreenEvent {
    object ToggleFalloutMap: MapScreenEvent()
    data class OnInsertParkingSpot(val latLng: LatLng): MapScreenEvent()
    data class OnInfoWindowLongClick(val spot: Entity.ParkingSpot): MapScreenEvent()
    data class OnLocationPermissionChanged(val isGranted: Boolean): MapScreenEvent()
}