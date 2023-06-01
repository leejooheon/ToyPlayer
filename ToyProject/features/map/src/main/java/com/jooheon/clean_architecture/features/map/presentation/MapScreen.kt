package com.jooheon.clean_architecture.features.map.presentation

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.*
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.map.presentation.components.EmptyParkingSpotUseCase
import com.jooheon.clean_architecture.features.map.presentation.components.ShowAlertDialog
import com.jooheon.clean_architecture.features.map.model.MapScreenEvent
import com.jooheon.clean_architecture.features.map.model.MapScreenState
import kotlinx.coroutines.launch

private val DEFAULT_LATLNG:LatLng = LatLng(37.5033311460182, 126.94775238633156)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    state: MapScreenState,
    onEvent: (MapScreenEvent) -> Unit,
) {
    val uiSettings = remember { MapUiSettings(zoomControlsEnabled = true) }

    val cameraPositionState = remember { CameraPositionState(
        CameraPosition(DEFAULT_LATLNG, 17f, 0f, 0f))
    }

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    var insertDialogState by remember { mutableStateOf<LatLng?>(null) }
    var permissionDialogState by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = state.properties.value,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
            onMapLongClick = { insertDialogState = it },
            onMyLocationClick = { Log.d("Jooheon", "onMyLocationButtonClick") }
        ) {
            val parkingSpots = state.parkingSpots.value
            parkingSpots?.forEach { spot ->
                ParkingSpotMarker(
                    spot = spot,
                    onInfoWindowLongClick = { onEvent(MapScreenEvent.OnInfoWindowLongClick(spot)) }
                )
            }
        }

        PermissionButton(
            permissionState = permissionState,
            onLocationPermissionButtonClicked = {
                permissionDialogState = true
            }
        )
    }


    ObserveLifecycleEvent(
        onResume = {
            MapScreenEvent.OnLocationPermissionChanged(permissionState.status.isGranted)
        }
    )

    CollectInsertDialog(
        insertDialogState = insertDialogState,
        onInsertButtonClicked = {
            insertDialogState = null
            it ?: return@CollectInsertDialog

            onEvent(MapScreenEvent.OnInsertParkingSpot(it))
        }
    )

    CollectPermissionDialog(
        permissionDialogState = permissionDialogState,
        onInteraction = {
            permissionDialogState = false

            if(permissionState.status !is PermissionStatus.Granted) {
                permissionState.launchPermissionRequest()
            }
        }
    )
}

@ExperimentalPermissionsApi
@Composable
private fun PermissionButton(
    permissionState: PermissionState,
    onLocationPermissionButtonClicked: () -> Unit,
) {
    if(permissionState.status.isGranted) {
        return
    }

    Button(
        onClick = onLocationPermissionButtonClicked,
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
    spot: Entity.ParkingSpot,
    onInfoWindowLongClick: (Marker) -> Unit,
) {
    val latitude = MapScreenState.decimalFormat.format(spot.lat)
    val longitude = MapScreenState.decimalFormat.format(spot.lng)
    Marker(
        state = MarkerState(position = LatLng(spot.lat, spot.lng)) ,
        title = "Spot (${latitude}, ${longitude})",
        snippet = "Long click to delete",
        onInfoWindowLongClick = onInfoWindowLongClick, //{ viewModel.dispatch(MapScreenEvent.OnInfoWindowLongClick(spot)) },
        onClick = {
            it.showInfoWindow()
            true
        },
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    )
}

@Composable
private fun CollectInsertDialog(
    insertDialogState: LatLng?,
    onInsertButtonClicked: (LatLng?) -> Unit
) {
    if(insertDialogState == null)
        return

    val content = UiText.DynamicString("Add this spot?")
    ShowAlertDialog(
        openDialog = mutableStateOf(true),
        content = content,
        onOkButtonClicked = { onInsertButtonClicked(insertDialogState) },
        onDismiss = { onInsertButtonClicked(null) }
    )
}

@Composable
private fun CollectPermissionDialog(
    permissionDialogState: Boolean,
//    permissionState: PermissionState,
    onInteraction: (Unit?) -> Unit
) {
    if(!permissionDialogState) {
        return
    }

    ShowAlertDialog(
        openDialog = mutableStateOf(true),
        content = UiText.DynamicString("Need permission\nfor find your location"),
        onOkButtonClicked = { onInteraction(Unit) },
        onDismiss = { onInteraction(null) }
    )
}

@ExperimentalPermissionsApi
@Composable
private fun ObserveLifecycleEvent(
    onResume: () -> Unit
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->
                if(event == Lifecycle.Event.ON_RESUME) {
                    onResume()
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
            state = MapScreenState.default,
            onEvent = { _ -> }
        )
    }
}