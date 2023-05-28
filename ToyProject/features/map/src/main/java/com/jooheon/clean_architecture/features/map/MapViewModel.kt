package com.jooheon.clean_architecture.features.map

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
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.map.states.MapEvent
import com.jooheon.clean_architecture.features.map.states.MapState
import com.jooheon.clean_architecture.features.map.states.MapStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val parkingSpotUseCase: ParkingSpotUseCase
): BaseViewModel() {
    override val TAG = MapViewModel::class.java.simpleName

    private val _mapState = mutableStateOf(MapState())
    val mapState = _mapState.value

    private val _insertDialogChannel = Channel<LatLng?>()
    val insertDialogChannel = _insertDialogChannel.receiveAsFlow()

    val decimalFormat = DecimalFormat("#.####")

    private val _permissionChannel = Channel<Unit?>()
    val permissionChannel = _permissionChannel.receiveAsFlow()

    private val _floatingActionClicked = Channel<Unit>()
    val floatingActionClicked = _floatingActionClicked.receiveAsFlow()

    init { updateParkingSpots() }

    @ExperimentalPermissionsApi
    fun onFindMyLocationButtonClicked(permissionState: PermissionState) {
        Log.d(TAG, "shouldShowRationale: ${permissionState.status.shouldShowRationale}, granted: ${permissionState.status.isGranted}")
        when(permissionState.status) {
            is PermissionStatus.Granted -> {
                Log.d(TAG, "isGranted")
                handleAlertDialogState(UiText.DynamicString("Permission is already granted."))
            }
            is PermissionStatus.Denied -> {
                if(permissionState.status.shouldShowRationale) {
                    viewModelScope.launch {
                        _permissionChannel.send(Unit)
                    }
                } else {
                    permissionState.launchPermissionRequest()
                    // https://github.com/google/accompanist/issues/1214
                }
            }
        }
    }

    fun onEvent(event: MapEvent) {
        when(event) {
            is MapEvent.ToggleFalloutMap -> {
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
            is MapEvent.OnMapLongClick -> {
                viewModelScope.launch {
                    _insertDialogChannel.send(event.latLng)
                }
            }
            is MapEvent.OnInfoWindowLongClick -> {
                viewModelScope.launch {
                    deleteParkingSpot(event.spot)
                }
            }
            is MapEvent.OnLocationPermissionChanged -> {
                val isGranted = event.isGranted
                val state = _mapState.value
                state.properties.value = MapProperties(
                    isMyLocationEnabled = isGranted,
                    mapStyleOptions = if(state.isFalloutMap.value) {
                        null
                    } else {
                        MapStyleOptions(MapStyle.json)
                    }
                )
            }
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
                mapState.parkingSpots.value = resource.value
            }
        }.launchIn(viewModelScope)
    }

    private fun isAlreadyInsertedSpot(spot: LatLng): Boolean {
        mapState.parkingSpots.value?.forEach {
            val newLatitude = decimalFormat.format(spot.latitude)
            val newLongitude = decimalFormat.format(spot.longitude)

            val insertedLatitude = decimalFormat.format(it.lat)
            val insertedLongitude = decimalFormat.format(it.lng)

            if(insertedLatitude == newLatitude && insertedLongitude == newLongitude) {
                return true
            }
        }
        return false
    }

    override fun dismissAlertDialog() {
        super.dismissAlertDialog()
        viewModelScope.launch {
            _insertDialogChannel.send(null)
            _permissionChannel.send(null)
        }
    }
}