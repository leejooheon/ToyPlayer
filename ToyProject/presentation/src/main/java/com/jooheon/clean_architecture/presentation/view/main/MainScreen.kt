package com.jooheon.clean_architecture.presentation.view.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel

import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.showToastMessage
import com.jooheon.clean_architecture.presentation.view.custom.GithubSearchDialog
import com.jooheon.clean_architecture.presentation.view.destinations.PlayListScreenDestination
import com.jooheon.clean_architecture.presentation.view.destinations.TestScreenDestination
import com.jooheon.clean_architecture.presentation.view.main.bottom.MyBottomNavigation
import com.jooheon.clean_architecture.presentation.view.main.bottom.Screen
import com.jooheon.clean_architecture.presentation.view.main.bottom.currentScreenAsState
import com.jooheon.clean_architecture.presentation.view.main.common.MusicBottomBar
import com.jooheon.clean_architecture.presentation.view.main.github.HomeScreen
import com.jooheon.clean_architecture.presentation.view.main.search.ExoPlayerScreen
import com.jooheon.clean_architecture.presentation.view.main.map.MapScreen
import com.jooheon.clean_architecture.presentation.view.main.wikipedia.WikipediaScreen
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
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
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel(sharedViewModel()),
    isPreview:Boolean = false
) {
    val bottomNavController = rememberAnimatedNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val bottomBarVisibility = remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = { TopBar(viewModel, navigator, drawerState, scope) },
        bottomBar = { BottomBar(bottomNavController, bottomBarVisibility.value) },
//        floatingActionButton = { MyFloatingActionButton(viewModel) },
//        floatingActionButtonPosition = FabPosition.End,
        contentColor = MaterialTheme.colorScheme.surface,
        content = { paddingParent ->
            RegisterBottomNavigation(
                mainViewModel = viewModel,
                musicPlayerViewModel = musicPlayerViewModel,
                navController = bottomNavController,
                navigator = navigator,
                modifier = Modifier.padding(paddingParent),
            )
        }
    )
    RegisterBackPressedHandler(viewModel, drawerState, scope)

    CollectEvents(
        event = musicPlayerViewModel.navigateToPlayListScreen,
        navigateTo = {
            bottomBarVisibility.value = false
            navigator.navigate(
                PlayListScreenDestination()
            )
        }
    )
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

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalComposeUiApi
@Composable
fun RegisterBottomNavigation(
    mainViewModel: MainViewModel,
    musicPlayerViewModel: MusicPlayerViewModel,
    navController: NavHostController,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        NavHost(navController, startDestination = Screen.Github.route) {
            composable(Screen.Github.route) {
                HomeScreen(navigator)
            }
            composable(Screen.Wiki.route) {
                WikipediaScreen(navigator)
            }
            composable(Screen.Map.route) {
                MapScreen(navigator, mainViewModel)
            }
            composable(Screen.Search.route) {
                ExoPlayerScreen()
            }
        }
        MusicBar(
            viewModel = musicPlayerViewModel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MusicBar(
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier,
    isPreview: Boolean = false
) {
    val uiState by viewModel.musicState.collectAsState()

    AnimatedVisibility(
        visible = if(!isPreview) uiState.isMusicBottomBarVisible else true,
        enter = scaleIn(),
        exit = ExitTransition.None,
        modifier = modifier
    ) {
        MusicBottomBar(
            song = uiState.currentPlayingMusic,
            isPlaying = uiState.isPlaying,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            onItemClick = viewModel::onMusicBottomBarPressed,
            onPlayPauseButtonPressed = viewModel::onPlayPauseButtonPressed,
            onPlayListButtonPressed = viewModel::onPlayListButtonPressed
        )
    }
}

@Composable
fun BottomBar(
    bottomNavController: NavController,
    visibility: Boolean
) {
    AnimatedVisibility(
        visible = visibility,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
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
    )
}

@ExperimentalMaterial3Api
@Composable
fun TopBar(
    viewModel: MainViewModel,
    navigator: DestinationsNavigator,
    drawerState: DrawerState,
    scope: CoroutineScope,
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

@Composable
fun CollectEvents(
    event: SharedFlow<Boolean>,
    navigateTo: () -> Unit
) {
    LaunchedEffect(Unit) {
        event.collectLatest {
            if (it) navigateTo()
        }
    }
}

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
private fun PreviewMusicBar() {
    val context = LocalContext.current
    val musicPlayerUseCase = MusicPlayerUseCase(EmptyMusicUseCase())
    val musicPlayerViewModel = MusicPlayerViewModel(
        context = context,
        dispatcher= Dispatchers.IO,
        musicController = MusicController(context, musicPlayerUseCase, true)
    )

    PreviewTheme(false) {
        MusicBar(
            viewModel = musicPlayerViewModel,
            modifier = Modifier.fillMaxWidth(),
            isPreview = true
        )
    }
}

@Preview
@Composable
private fun PreviewMainScreen() {
    val context = LocalContext.current
    val viewModel = MainViewModel(EmptyMusicUseCase())
    val musicPlayerUseCase = MusicPlayerUseCase(EmptyMusicUseCase())
    val musicPlayerViewModel = MusicPlayerViewModel(
        context = context,
        dispatcher= Dispatchers.IO,
        musicController = MusicController(context, musicPlayerUseCase, true)
    )
    PreviewTheme(false) {
        MainScreen(EmptyDestinationsNavigator, viewModel, musicPlayerViewModel, true)
    }
}