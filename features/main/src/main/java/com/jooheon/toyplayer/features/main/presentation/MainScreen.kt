package com.jooheon.toyplayer.features.main.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.ObserveAsEvents
import com.jooheon.toyplayer.features.common.compose.SnackbarController
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.utils.VersionUtil
import com.jooheon.toyplayer.features.main.MainViewModel
import com.jooheon.toyplayer.features.main.model.MainScreenEvent
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    mainNavigator: MainNavigator = rememberMainNavigator(),
    viewModel: MainViewModel = hiltViewModel()
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

            if(result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    viewModel.navigateTo.observeWithLifecycle {
        mainNavigator.navController.navigate(ScreenNavigation.Setting.Main)
    }

    MainScreen(
        navigator = mainNavigator,
        onEvent = viewModel::dispatch
    )

    MaybeRequestMediaPermission(
        onPermissionGranted = {
            viewModel.dispatch(MainScreenEvent.OnPermissionGranted)
        }
    )
}

@Composable
private fun MainScreen(
    navigator: MainNavigator,
    onEvent: (MainScreenEvent) -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier,
        topBar = {
            // TODO
        },
        content = { padding ->
            MainNavHost(
                navigator = navigator,
                padding = padding,
            )
        },
        bottomBar = {
            MainBottomBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(start = 8.dp, end = 8.dp, bottom = 28.dp),
                visible = navigator.shouldShowBottomBar(),
                tabs = MainTab.entries.toPersistentList(),
                currentTab = navigator.currentTab,
                onTabSelected = { navigator.navigate(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    )
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
        MainScreen(
            navigator = rememberMainNavigator(),
            onEvent = { _ -> }
        )
    }
}