package com.jooheon.clean_architecture.presentation.view.main.map

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.ObserveAlertDialogState
import com.jooheon.clean_architecture.presentation.utils.ShowAlertDialog
import com.jooheon.clean_architecture.presentation.utils.UiText
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import com.jooheon.clean_architecture.presentation.view.temp.EmptyParkingSpotUseCase
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import kotlinx.coroutines.launch

private val DEFAULT_LATLNG:LatLng = LatLng(37.5033311460182, 126.94775238633156)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navigator: NavController,
    sharedViewModel: MainViewModel = hiltViewModel(sharedViewModel()),
    viewModel: MapViewModel = hiltViewModel(),
    isPreview: Boolean = false,
) {
    val uiSettings = remember { MapUiSettings(zoomControlsEnabled = true) }

    val cameraPositionState = remember { CameraPositionState(
        CameraPosition(DEFAULT_LATLNG, 17f, 0f, 0f))
    }

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = viewModel.mapState.properties.value,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
            onMapLongClick = {
                viewModel.onEvent(MapEvent.OnMapLongClick(it))
            },
            onMyLocationClick = {
                Log.d("Jooheon", "onMyLocationButtonClick")
            }
        ) {
            val parkingSpots = viewModel.mapState.parkingSpots.value
            parkingSpots?.forEach {
                ParkingSpotMarker(viewModel, it)
            }
        }

        PermissionButton(viewModel, permissionState)
    }

    ObserveEvents(sharedViewModel, viewModel, permissionState)
    ObserveDialogStates(viewModel, permissionState)

    ObserveAlertDialogState(viewModel)
}

@ExperimentalPermissionsApi
@Composable
private fun PermissionButton(
    viewModel: MapViewModel,
    permissionState: PermissionState
) {
    if(permissionState.status.isGranted) {
        return
    }

    Button(
        onClick = {
            viewModel.onFindMyLocationButtonClicked(permissionState)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Text(
            text = "Need permission",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ParkingSpotMarker(
    viewModel: MapViewModel,
    spot: Entity.ParkingSpot
) {
    val latitude = viewModel.decimalFormat.format(spot.lat)
    val longitude = viewModel.decimalFormat.format(spot.lng)
    Marker(
        state = MarkerState(position = LatLng(spot.lat, spot.lng)) ,
        title = "Spot (${latitude}, ${longitude})",
        snippet = "Long click to delete",
        onInfoWindowLongClick = { viewModel.onEvent(MapEvent.OnInfoWindowLongClick(spot)) },
        onClick = {
            it.showInfoWindow()
            true
        },
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    )
}

@ExperimentalPermissionsApi
@Composable
private fun ObserveDialogStates(
    viewModel: MapViewModel,
    permissionState: PermissionState
) {
    val askPermissionState = viewModel.permissionChannel.collectAsState(initial = null).value
    askPermissionState?.let {
        ShowAlertDialog(
            openDialog = mutableStateOf(true),
            content = UiText.DynamicString("Need permission\nfor find your location"),
            viewModel = viewModel,
            onOkButtonClicked = {
                viewModel.dismissAlertDialog()
                permissionState.launchPermissionRequest()
            }
        )
    }

    val insertDialogState = viewModel.insertDialogChannel.collectAsState(initial = null).value
    insertDialogState?.let {
        val content = UiText.DynamicString("Add this spot?")
        ShowAlertDialog(
            openDialog = mutableStateOf(true),
            content = content,
            viewModel = viewModel,
            onOkButtonClicked = {
                viewModel.insertParkingSpot(it)
            }
        )
    }
}

@ExperimentalPermissionsApi
@Composable
private fun ObserveEvents(
    sharedViewModel: MainViewModel,
    viewModel: MapViewModel,
    permissionState: PermissionState
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->
                lifecycleOwner.lifecycleScope.launch {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        sharedViewModel.floatingActionClicked.collect {
                            viewModel.onEvent(MapEvent.ToggleFalloutMap)
                        }
                    }
                }

                if(event == Lifecycle.Event.ON_RESUME) {
                    viewModel.onEvent(
                        MapEvent.OnLocationPermissionChanged(permissionState.status.isGranted)
                    )
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
}

@Preview
@Composable
private fun PreviewMapScreen() {
    val context = LocalContext.current
    val viewModel = MapViewModel(EmptyParkingSpotUseCase())
    PreviewTheme(true) {
        MapScreen(
            navigator = NavController(context),
            sharedViewModel = MainViewModel(EmptyMusicUseCase()),
            viewModel = viewModel,
            isPreview = true
        )
    }
}