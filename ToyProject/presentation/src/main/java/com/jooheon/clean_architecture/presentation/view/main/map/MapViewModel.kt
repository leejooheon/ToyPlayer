package com.jooheon.clean_architecture.presentation.view.main.map

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.MapStyleOptions
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.map.ParkingSpotUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val parkingSpotUseCase: ParkingSpotUseCase
): BaseViewModel() {
    override val TAG = MapViewModel::class.java.simpleName
    var state by mutableStateOf(MapState())

    init {
        updateParkingSpots()
    }

    fun onEvent(event: MapEvent) {
        when(event) {
            is MapEvent.ToggleFalloutMap -> {
                state = state.copy(
                    properties = state.properties.copy(
                        mapStyleOptions = if(state.isFalloutMap) {
                            null
                        } else {
                            MapStyleOptions(MapStyle.json)
                        }
                    ),
                    isFalloutMap = !state.isFalloutMap
                )
            }
            is MapEvent.OnMapLongClick -> {
                viewModelScope.launch {
                    insertParkingSpot(event.latLng.latitude, event.latLng.longitude)
                }
            }
            is MapEvent.OnInfoWindowLongClick -> {
                viewModelScope.launch {
                    deleteParkingSpot(event.spot)
                }
            }
        }
    }

    private fun insertParkingSpot(lat: Double, lng: Double) {
        val spot = Entity.ParkingSpot(lat, lng, null)
        Log.d(TAG, "insertParkingSpot: ${spot}")

        parkingSpotUseCase.insertParkingSpot(spot).onEach { resource ->
            handleResponse(resource)
            if(resource is Resource.Success) {
                handleAlertDialogState(UiText.DynamicString("insert sucess"))
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
                    handleAlertDialogState(UiText.DynamicString("delete sucess"))
                    updateParkingSpots()
                }
            }.launchIn(viewModelScope)
        } ?: handleAlertDialogState(UiText.DynamicString("spot id is null"))
    }

    private fun updateParkingSpots() {
        parkingSpotUseCase.getParkingSpots().onEach { resource ->
            if(resource is Resource.Success) {
                state.parkingSpots.value = resource.value
            }
        }.launchIn(viewModelScope)
    }
}