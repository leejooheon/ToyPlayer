package com.jooheon.toyplayer.features.main.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ui.TopAppBar
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.common.utils.VersionUtil
import com.jooheon.toyplayer.features.main.MainViewModel
import com.jooheon.toyplayer.features.main.model.MainScreenEvent
import com.jooheon.toyplayer.features.main.navigation.BottomNavigationHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    viewModel.navigateTo.observeWithLifecycle {
        navController.navigate(ScreenNavigation.Setting.Main)
    }

    MainScreen(
        navController = navController,
        onEvent = viewModel::dispatch
    )

    MaybeRequestMediaPermission(
        onPermissionGranted = {
            viewModel.dispatch(MainScreenEvent.OnPermissionGranted)
        }
    )
}

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
private fun MainScreen(
    navController: NavController,
    onEvent: (MainScreenEvent) -> Unit
) {
    val bottomNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val bottomBarVisibility = remember { mutableStateOf(true) }
    val bottomBarPadding = remember { mutableStateOf(60.dp) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackBarHostState)},
            bottomBar = { BottomBar(bottomNavController, bottomBarVisibility.value) },
            floatingActionButtonPosition = FabPosition.End,
            contentColor = MaterialTheme.colorScheme.surface,
            topBar = {
                TopBar(
                    drawerState = drawerState,
                    scope = scope,
                    onFavoriteClicked = { onEvent(MainScreenEvent.OnFavoriteIconCLick) },
                    onSearchClicked = { onEvent(MainScreenEvent.OnSearchIconClick)},
                    onSettingClicked = { onEvent(MainScreenEvent.OnSettingIconClick)},
                )
            },
            content = { paddingParent ->
                bottomBarPadding.value = paddingParent.calculateBottomPadding()
                BottomNavigationHost(
                    navController = bottomNavController,
                    navigator = navController,
                    modifier = Modifier.padding(paddingParent),
                )
            }
        )
    }
}

@Composable
fun BottomBar(
    bottomNavController: NavController,
    visibility: Boolean
) {
    val currentSelectedItem by bottomNavController.currentBottomNavScreenAsState()

    AnimatedVisibility(
        visible = visibility,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            MyBottomNavigation(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseSurface),
                selectedNavigation = currentSelectedItem,
                onNavigationSelected = { selectedScreen ->
                    Timber.d("selectedScreen: $selectedScreen")

                    bottomNavController.navigate(selectedScreen) {
                        launchSingleTop = true
                        restoreState = true

                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                }
            )
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun TopBar(
    drawerState: DrawerState,
    scope: CoroutineScope,
    onFavoriteClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onSettingClicked: () -> Unit,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.primary,
        title = {
            Text(
                text = "ToyPlayer",
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch { drawerState.open() }
                Timber.d("Menu IconButton")
            }) {
                Icon(
                    Icons.Filled.Menu,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = onFavoriteClicked) {
                Icon(
                    Icons.Filled.Favorite,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "first IconButton description"
                )
            }
            IconButton(onClick = onSearchClicked) {
                Icon(
                    Icons.Filled.Search,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "second IconButton description"
                )
            }
            IconButton(onClick = onSettingClicked) {
                Icon(
                    Icons.Filled.Settings,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null)
            }
        }
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
    val context = LocalContext.current

    PreviewTheme(false) {
        MainScreen(
            navController = NavController(context),
            onEvent = { _ -> }
        )
    }
}