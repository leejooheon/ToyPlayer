package com.jooheon.clean_architecture.features.map.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.map.ParkingSpotUseCase
import com.jooheon.clean_architecture.toyproject.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.map.model.MapScreenEvent
import com.jooheon.clean_architecture.features.map.model.MapScreenState
import com.jooheon.clean_architecture.features.map.model.MapStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val parkingSpotUseCase: ParkingSpotUseCase
): BaseViewModel() {
    override val TAG = MapViewModel::class.java.simpleName

    private val _mapState = MutableStateFlow(MapScreenState())
    val mapState = _mapState.asStateFlow()

    init { updateParkingSpots() }

    fun dispatch(event: MapScreenEvent) = viewModelScope.launch {
        when(event) {
            is MapScreenEvent.ToggleFalloutMap -> {
                val state = _mapState.value
                state.properties.value = MapProperties(
                    isMyLocationEnabled = state.properties.value.isMyLocationEnabled,
                    mapStyleOptions = if(state.isFalloutMap.value) {
                        null
                    } else {
                        MapStyleOptions(MapStyle.json)
                    }
                )

                state.isFalloutMap.value = !state.isFalloutMap.value
            }

            is MapScreenEvent.OnLocationPermissionChanged -> {
                val isGranted = event.isGranted
                val state = _mapState.value

                if(!isGranted) return@launch

                state.properties.value = MapProperties(
                    isMyLocationEnabled = isGranted,
                    mapStyleOptions = if(state.isFalloutMap.value) {
                        null
                    } else {
                        MapStyleOptions(MapStyle.json)
                    }
                )
            }

            is MapScreenEvent.OnInsertParkingSpot -> insertParkingSpot(event.latLng)
            is MapScreenEvent.OnInfoWindowLongClick -> deleteParkingSpot(event.spot)
        }
    }

    fun insertParkingSpot(latLng: LatLng) {
        if(isAlreadyInsertedSpot(latLng)) {
            handleAlertDialogState(UiText.DynamicString("Already inserted spot."))
            return
        }

        Log.d(TAG, "insertParkingSpot: ${latLng}")
        val spot = Entity.ParkingSpot(latLng.latitude, latLng.longitude, null)
        parkingSpotUseCase.insertParkingSpot(spot).onEach { resource ->
            handleResponse(resource)
            if(resource is Resource.Success) {
//                handleAlertDialogState(UiText.DynamicString("Added this spot."))
                updateParkingSpots()
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteParkingSpot(spot: Entity.ParkingSpot) {
        Log.d(TAG, "deleteParkingSpot: ${spot.id}")
        spot.id?.let {
            parkingSpotUseCase.deleteParkingSpot(spot).onEach { resource ->
                handleResponse(resource)
                if(resource is Resource.Success) {
                    handleAlertDialogState(UiText.DynamicString("Spot has been deleted."))
                    updateParkingSpots()
                }
            }.launchIn(viewModelScope)
        } ?: handleAlertDialogState(UiText.DynamicString("spot id is null"))
    }

    private fun updateParkingSpots() {
        parkingSpotUseCase.getParkingSpots().onEach { resource ->
            if(resource is Resource.Success) {
                mapState.value.parkingSpots.value = resource.value
            }
        }.launchIn(viewModelScope)
    }

    private fun isAlreadyInsertedSpot(spot: LatLng): Boolean {
        mapState.value.parkingSpots.value?.forEach {
            val newLatitude = MapScreenState.decimalFormat.format(spot.latitude)
            val newLongitude = MapScreenState.decimalFormat.format(spot.longitude)

            val insertedLatitude = MapScreenState.decimalFormat.format(it.lat)
            val insertedLongitude = MapScreenState.decimalFormat.format(it.lng)

            if(insertedLatitude == newLatitude && insertedLongitude == newLongitude) {
                return true
            }
        }
        return false
    }

    override fun dismissAlertDialog() {
        super.dismissAlertDialog()
    }
}