package com.jooheon.clean_architecture.presentation.view.main.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.jooheon.clean_architecture.presentation.utils.ObserveAlertDialogState
import com.jooheon.clean_architecture.presentation.utils.ObserveLoadingState
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Composable
fun MapScreen(
    navigator: DestinationsNavigator,
    sharedViewModel: MainViewModel,
    viewModel: MapViewModel = hiltViewModel(),
    isPreview: Boolean = false,
) {
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true
        )
    }

    val cameraPositionState = CameraPositionState(
        CameraPosition(LatLng(37.5033311460182, 126.94775238633156), 17f, 0f, 0f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = viewModel.state.properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
            onMapLongClick = {
                viewModel.onEvent(MapEvent.OnMapLongClick(it))
            }
        ) {
            val parkingSpots = viewModel.state.parkingSpots.value
            parkingSpots?.forEach { spot ->
                Marker(
                    position = LatLng(spot.lat, spot.lng),
                    title = "Parking spot (${spot.lat}, ${spot.lng})",
                    snippet = "Long click to delete",
                    onInfoWindowLongClick = {
                        viewModel.onEvent(
                            MapEvent.OnInfoWindowLongClick(spot)
                        )
                    },
                    onClick = {
                        it.showInfoWindow()
                        true
                    },
                    icon = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN
                    )
                )
            }
        }
    }

    if(!isPreview) {
        observeFloatingActionEvent(sharedViewModel, viewModel)
    }

    ObserveAlertDialogState(viewModel)
    ObserveLoadingState(viewModel)
}

@Composable
fun observeFloatingActionEvent(
    sharedViewModel: MainViewModel,
    viewModel: MapViewModel,
) {
    LaunchedEffect(Unit) {
        sharedViewModel.floatingActionClicked.collect {
            viewModel.onEvent(MapEvent.ToggleFalloutMap)
        }
    }
}