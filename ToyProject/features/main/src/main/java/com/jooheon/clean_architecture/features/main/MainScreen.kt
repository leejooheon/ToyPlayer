package com.jooheon.clean_architecture.features.main

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.main.model.MainScreenEvent
import com.jooheon.clean_architecture.features.main.navigation.BottomNavigationHost
import com.jooheon.clean_architecture.features.main.navigation.MyBottomNavigation
import com.jooheon.clean_architecture.features.main.model.MainScreenState
import com.jooheon.clean_architecture.features.main.navigation.currentBottomNavScreenAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val TAG = "MainScreen"

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun MainScreen(
    navigator: NavController,
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
) {
    val bottomNavController = rememberAnimatedNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val bottomBarVisibility = remember { mutableStateOf(true) }
    val bottomBarPadding = remember { mutableStateOf(60.dp) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
            bottomBar = { BottomBar(bottomNavController, bottomBarVisibility.value) },
            floatingActionButtonPosition = FabPosition.End,
            contentColor = MaterialTheme.colorScheme.surface,
            topBar = {
                TopBar(
                    drawerState = drawerState,
                    scope = scope,
                    onFavoriteClicked = { onEvent(MainScreenEvent.OnFavoriteIconCLick) },
                    onSearchClicked = { onEvent(MainScreenEvent.OnSearchIconClick)},
                    onSettingClicked = { onEvent(MainScreenEvent.OnSearchIconClick)},
                )
            },
            content = { paddingParent ->
                bottomBarPadding.value = paddingParent.calculateBottomPadding()
                BottomNavigationHost(
                    navController = bottomNavController,
                    navigator = navigator,
                    modifier = Modifier.padding(paddingParent),
                )
            }
        )
    }

    RegisterBackPressedHandler(
        isDoubleBackPressed = state.doubleBackPressedState,
        drawerState = drawerState,
        scope = scope,
        onBackPressed = { /** EVENT **/}
    )
}

@Composable
fun MyFloatingActionButton(
    viewModel: MainViewModel,
) {
    FloatingActionButton(
        shape = CircleShape,
        onClick = { viewModel.onFloatingButtonClicked() },
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary
    ) {
        Icon(
            imageVector = if (viewModel.floatingButtonState.value) {
                Icons.Default.ToggleOff
            } else Icons.Default.ToggleOn,
            contentDescription = "floating action button"
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun DrawerContent(
    drawerState: DrawerState,
    scope: CoroutineScope,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(paddingValues)
    ) {
        Spacer(Modifier.statusBarsHeight(additional = 24.dp))
        Text(
            modifier = Modifier.padding(16.dp),
            text = "drawerContent - 1",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = "drawerContent - 2",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            modifier = Modifier
                .padding(16.dp)
                .clickable { scope.launch { drawerState.close() } },
            text = "drawerContent - 3",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium
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
                    Log.d(TAG, "selectedScreen: $selectedScreen")

                    bottomNavController.navigate(selectedScreen.route) {
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
                text = "ToyProject",
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch { drawerState.open() }
                Log.d(TAG, "Menu IconButton")
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

@ExperimentalMaterial3Api
@Composable
fun RegisterBackPressedHandler (
    isDoubleBackPressed: Boolean,
    drawerState: DrawerState,
    scope: CoroutineScope,
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    BackHandler(
        enabled = drawerState.isOpen || isDoubleBackPressed
    ) {
        scope.launch {
            if(drawerState.isOpen) {
                scope.launch { drawerState.close() }
                return@launch
            }
            if(isDoubleBackPressed) {
                onBackPressed()
                Toast.makeText(context, "Press once more to exit.", Toast.LENGTH_SHORT).show()
                return@launch
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMainScreen() {
    val context = LocalContext.current

    PreviewTheme(false) {
        MainScreen(
            navigator = NavController(context),
            state = MainScreenState.default,
            onEvent = { _, -> }
        )
    }
}