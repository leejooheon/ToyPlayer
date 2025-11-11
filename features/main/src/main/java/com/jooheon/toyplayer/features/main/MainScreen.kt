package com.jooheon.toyplayer.features.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.features.common.controller.SnackbarController
import com.jooheon.toyplayer.features.common.utils.VersionUtil
import com.jooheon.toyplayer.features.commonui.ext.ObserveAsEvents
import com.jooheon.toyplayer.features.main.components.CustomSnackbarHost
import com.jooheon.toyplayer.features.main.navigation.MainNavigator
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navigator: Navigator,
    entryProviderBuilders: Set<EntryProviderInstaller>,
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(
        flow = SnackbarController.event,
        snackbarHostState
    ) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.uiText.asString(context),
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    MainScreenInternal(
        navigator = navigator,
        entryProviderBuilders = entryProviderBuilders,
        snackbarHostState = snackbarHostState,
    )

    MaybeRequestMediaPermission(
        onPermissionGranted = onPermissionGranted
    )
}

@Composable
private fun MainScreenInternal(
    navigator: Navigator,
    entryProviderBuilders: Set<EntryProviderInstaller>,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier,
    ) { _ ->
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = navigator.backStack,
            onBack = { navigator.popBackStack() },
            sceneStrategy = SinglePaneSceneStrategy(),
            entryProvider = entryProvider {
                entryProviderBuilders.forEach { builder -> this.builder() }
            }
        )
    }
}

@Composable
private fun MaybeRequestMediaPermission(
    onPermissionGranted: () -> Unit,
) {
    val permission = if(VersionUtil.hasTiramisu()) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val storagePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if(isGranted) onPermissionGranted()
        }
    )
    SideEffect {
        storagePermissionResultLauncher.launch(permission)
    }
}

@Preview
@Composable
private fun PreviewMainScreen() {
    ToyPlayerTheme {
        MainScreenInternal(
            navigator = MainNavigator(),
            entryProviderBuilders = emptySet(),
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}