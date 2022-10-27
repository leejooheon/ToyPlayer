package com.jooheon.clean_architecture.presentation.view.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jooheon.clean_architecture.presentation.MainActivity
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.ObserveAlertDialogState
import com.jooheon.clean_architecture.presentation.utils.showToastMessage
import com.jooheon.clean_architecture.presentation.view.custom.GithubSearchDialog
import com.jooheon.clean_architecture.presentation.view.destinations.TestScreenDestination
import com.jooheon.clean_architecture.presentation.view.main.bottom.MyBottomNavigation
import com.jooheon.clean_architecture.presentation.view.main.bottom.Screen
import com.jooheon.clean_architecture.presentation.view.main.bottom.currentScreenAsState
import com.jooheon.clean_architecture.presentation.view.main.github.HomeScreen
import com.jooheon.clean_architecture.presentation.view.main.search.ExoPlayerScreen
import com.jooheon.clean_architecture.presentation.view.main.map.MapScreen
import com.jooheon.clean_architecture.presentation.view.main.wikipedia.WikipediaScreen
import com.jooheon.clean_architecture.presentation.view.temp.EmptyGithubUseCase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val TAG = "MainScreen"

@Composable
fun sharedViewModel() = LocalContext.current as MainActivity

//https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary
@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel(sharedViewModel()),
    isPreview:Boolean = false
) {
    val bottomNavController = rememberAnimatedNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
//    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = { TopBar(viewModel, navigator, drawerState, scope) },
        bottomBar = { BottomBar(bottomNavController) },
        floatingActionButton = { MyFloatingActionButton(viewModel) },
        floatingActionButtonPosition = FabPosition.Center,
        contentColor = MaterialTheme.colorScheme.background,
        content = { paddingParent ->
            RegisterBottomNavigation(viewModel, bottomNavController, navigator, paddingParent, isPreview)
//            BottomSheetScaffold(
//                modifier = Modifier.padding(paddingParent),
//                sheetBackgroundColor = MaterialTheme.colorScheme.surface,
//                sheetContent = { BottomSheetContent() }
//            ) { paddingValue ->
//                ModalNavigationDrawer(
//                    drawerState = drawerState,
//                    drawerContent = { DrawerContent(drawerState, scope, paddingValue) },
//                    content = { RegisterBottomNavigation(viewModel, bottomNavController, navigator, paddingValue, isPreview) }
//                )
//            }
        }
    )
    RegisterBackPressedHandler(viewModel, drawerState, scope)
}

@Composable
private fun BottomSheetContent() {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "this is bottom sheet",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge
        )
    }
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
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(paddingValues)
    ) {
        Spacer(Modifier.statusBarsHeight(additional = 24.dp))
        Text(
            modifier = Modifier.padding(16.dp),
            text = "drawerContent - 1",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = "drawerContent - 2",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            modifier = Modifier
                .padding(16.dp)
                .clickable { scope.launch { drawerState.close() } },
            text = "drawerContent - 3",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalComposeUiApi
@Composable
fun RegisterBottomNavigation(
    viewModel: MainViewModel,
    navController: NavHostController,
    navigator: DestinationsNavigator,
    paddingValues: PaddingValues,
    isPreview:Boolean
) {
    // NavHost가 Preview에서 에러나는현상이 있어 Flag로 막아둠.
    if(isPreview) { return }
    Box(modifier = Modifier.padding(paddingValues)) {
        NavHost(navController, startDestination = Screen.Github.route) {
            composable(Screen.Github.route) {
                HomeScreen(navigator)
            }
            composable(Screen.Wiki.route) {
                WikipediaScreen(navigator)
            }
            composable(Screen.Map.route) {
                MapScreen(navigator, viewModel)
            }
            composable(Screen.Search.route) {
                ExoPlayerScreen()
            }
        }
    }
}

@Composable
fun BottomBar(bottomNavController: NavController) {
    val currentSelectedItem by bottomNavController.currentScreenAsState()
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

@ExperimentalMaterial3Api
@Composable
fun TopBar(
    viewModel: MainViewModel,
    navigator: DestinationsNavigator,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val openGithubSearchDialog = remember { mutableStateOf(false) }
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
                    contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = {
                viewModel.onFavoriteClicked()
                Log.d(TAG, "Favorite IconButton")
            }) {
                Icon(
                    Icons.Filled.Favorite,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "first IconButton description"
                )
            }
            IconButton(onClick = {
                openGithubSearchDialog.value = true
            }) {
                Icon(
                    Icons.Filled.Search,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "second IconButton description"
                )
            }
            IconButton(onClick = {
//                        viewModel.onSettingClicked()
                Log.d(TAG, "Settings IconButton")
                navigator.navigate(TestScreenDestination())
            }) {
                Icon(
                    Icons.Filled.Settings,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null)
            }
        }
    )

    if(openGithubSearchDialog.value) {
        GithubSearchDialog(openGithubSearchDialog, onDismiss = { owner ->
            if (!owner.isEmpty()) {
                Log.d(TAG, owner)
//                viewModel.callRepositoryApi(owner)
            }
        })
    }
}

//@Composable
//fun MySnackHost(state: SnackbarHostState) {
//    SnackbarHost(
//        hostState = state,
//        snackbar = { data ->
//            Snackbar(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .background(MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(8.dp)),
//                action = {
//                    Text(
//                        text = data.actionLabel?.let { it } ?: run { "hide" },
//                        color = MaterialTheme.colorScheme.inverseOnSurface,
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .clickable { state.currentSnackbarData?.dismiss() },
//                        style = TextStyle(
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.inverseOnSurface,
//                            fontSize = 18.sp
//                        )
//                    )
//                }
//            ) {
//                Text(
//                    text = data.message,
//                    color = MaterialTheme.colorScheme.inverseOnSurface
//                )
//            }
//        }
//    )
//}

@ExperimentalMaterial3Api
@Composable
fun RegisterBackPressedHandler (
    viewModel: MainViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val context = LocalContext.current
    BackHandler(
        enabled = drawerState.isOpen || viewModel.isDoubleBackPressed.value
    ) {
        scope.launch {
            if(drawerState.isOpen) {
                scope.launch { drawerState.close() }
                return@launch
            }
            if(viewModel.isDoubleBackPressed.value) {
                viewModel.onBackPressed()
                showToastMessage(context, "Press once more to exit.")
                return@launch
            }
        }
    }
}


@Preview
@Composable
fun PreviewMainScreen() {
    val viewModel = MainViewModel(EmptyGithubUseCase())
    PreviewTheme(true) {
        MainScreen(EmptyDestinationsNavigator, viewModel, true)
    }
}