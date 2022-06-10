package com.jooheon.clean_architecture.presentation.view.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jooheon.clean_architecture.presentation.theme.themes.CustomTheme
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.showToastMessage
import com.jooheon.clean_architecture.presentation.view.custom.GithubSearchDialog
import com.jooheon.clean_architecture.presentation.view.destinations.TestScreenDestination
import com.jooheon.clean_architecture.presentation.view.main.bottom.MyBottomNavigation
import com.jooheon.clean_architecture.presentation.view.main.bottom.Screen
import com.jooheon.clean_architecture.presentation.view.main.bottom.currentScreenAsState
import com.jooheon.clean_architecture.presentation.view.main.github.HomeScreen
import com.jooheon.clean_architecture.presentation.view.main.search.SearchScreen
import com.jooheon.clean_architecture.presentation.view.main.watched.WatchedScreen
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

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
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

    Scaffold(
        containerColor = CustomTheme.colors.material3Colors.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = { TopBar(viewModel, navigator, drawerState, scope) },
        bottomBar = { BottomBar(bottomNavController) },
        floatingActionButton = { MyFloatingActionButton(scope, snackbarHostState) },
        floatingActionButtonPosition = FabPosition.Center,
        contentColor = CustomTheme.colors.material3Colors.background,
        content = { paddingValue ->
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = { DrawerContent(drawerState, scope, paddingValue) },
                content = { RegisterBottomNavigation(bottomNavController, navigator, paddingValue, isPreview) }
            )
        },
    )

    RegisterBackPressedHandler(viewModel, drawerState, scope)
}

@Composable
fun MyFloatingActionButton(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val floatingButtonText = remember { mutableStateOf("X")}

    FloatingActionButton(
        onClick = {
            floatingButtonText.value = "+"
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Jetpack Compose Snackbar",
                    actionLabel = "Ok"
                )

                when(result) {
                    SnackbarResult.Dismissed -> {
                        Log.d(TAG, "Snackbar dismissed")
                        floatingButtonText.value = "X"
                    }
                    SnackbarResult.ActionPerformed -> {
                        Log.d(TAG, "Snackbar action!")
                        floatingButtonText.value = "X"
                    }
                    else -> {

                    }
                }
            }
        },
        containerColor = CustomTheme.colors.material3Colors.tertiary
    ) {
        Text(
            text = floatingButtonText.value,
            color = CustomTheme.colors.material3Colors.onTertiary
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
            .background(CustomTheme.colors.material3Colors.primaryContainer)
            .padding(paddingValues)
    ) {
        Spacer(Modifier.statusBarsHeight(additional = 24.dp))
        Text(
            modifier = Modifier.padding(16.dp),
            text = "drawerContent - 1",
            color = CustomTheme.colors.material3Colors.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = "drawerContent - 2",
            color = CustomTheme.colors.material3Colors.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            modifier = Modifier
                .padding(16.dp)
                .clickable { scope.launch { drawerState.close() } },
            text = "drawerContent - 3",
            color = CustomTheme.colors.material3Colors.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun RegisterBottomNavigation(
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
            composable(Screen.Watched.route) {
                WatchedScreen()
            }
            composable(Screen.Search.route) {
                SearchScreen()
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
            .background(CustomTheme.colors.material3Colors.inverseSurface),
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
        backgroundColor = CustomTheme.colors.material3Colors.primary,
        title = {
            Text(
                text = "ToyProject",
                color = CustomTheme.colors.material3Colors.onPrimary,
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
                    tint = CustomTheme.colors.material3Colors.onPrimary,
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
                    tint = CustomTheme.colors.material3Colors.onPrimary,
                    contentDescription = "first IconButton description"
                )
            }
            IconButton(onClick = {
                openGithubSearchDialog.value = true
            }) {
                Icon(
                    Icons.Filled.Search,
                    tint = CustomTheme.colors.material3Colors.onPrimary,
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
                    tint = CustomTheme.colors.material3Colors.onPrimary,
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
//                    .background(CustomTheme.colors.material3Colors.inverseSurface, RoundedCornerShape(8.dp)),
//                action = {
//                    Text(
//                        text = data.actionLabel?.let { it } ?: run { "hide" },
//                        color = CustomTheme.colors.material3Colors.inverseOnSurface,
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .clickable { state.currentSnackbarData?.dismiss() },
//                        style = TextStyle(
//                            fontWeight = FontWeight.Bold,
//                            color = CustomTheme.colors.material3Colors.inverseOnSurface,
//                            fontSize = 18.sp
//                        )
//                    )
//                }
//            ) {
//                Text(
//                    text = data.message,
//                    color = CustomTheme.colors.material3Colors.inverseOnSurface
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