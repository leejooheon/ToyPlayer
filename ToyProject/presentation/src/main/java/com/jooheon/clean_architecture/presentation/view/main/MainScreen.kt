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
import androidx.compose.material3.FabPosition
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
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jooheon.clean_architecture.presentation.MainActivity
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel

import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.showToastMessage
import com.jooheon.clean_architecture.presentation.view.custom.GithubSearchDialog
import com.jooheon.clean_architecture.presentation.view.main.bottom.*
import com.jooheon.clean_architecture.presentation.view.main.common.CollectEvent
import com.jooheon.clean_architecture.presentation.view.navigation.BottomNavigationHost
import com.jooheon.clean_architecture.presentation.view.navigation.MyBottomNavigation
import com.jooheon.clean_architecture.presentation.view.navigation.currentBottomNavScreenAsState
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

@Composable
fun MainScreen(
    navigator: NavController,
    viewModel: MainViewModel = hiltViewModel(sharedViewModel()),
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel(sharedViewModel()),
) {
    Log.d(TAG, "viewModel: ${musicPlayerViewModel}")
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
            topBar = { TopBar(viewModel, drawerState, scope) },
            bottomBar = { BottomBar(bottomNavController, bottomBarVisibility.value) },
//            floatingActionButton = { MyFloatingActionButton(viewModel) },
            floatingActionButtonPosition = FabPosition.End,
            contentColor = MaterialTheme.colorScheme.surface,
            content = { paddingParent ->
                bottomBarPadding.value = paddingParent.calculateBottomPadding()
                BottomNavigationHost(
                    navController = bottomNavController,
                    navigator = navigator,
                    modifier = Modifier.padding(paddingParent),
                )
            }
        )

        MusicBar(
            navigator = navigator,
            viewModel = musicPlayerViewModel,
            modifier = Modifier
                .padding(bottom = bottomBarPadding.value + 2.dp)
                .align(Alignment.BottomCenter)
        )
    }

    RegisterBackPressedHandler(viewModel, drawerState, scope)

    CollectEvent(
        event = musicPlayerViewModel.navigateToPlayListScreen,
        navigateTo = {
            bottomBarVisibility.value = false
//            navigator.navigate(
//                PlayListScreenDestination()
//            )
        }
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
private fun BoxScope.MusicBar(
    navigator: NavController,
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.musicState.collectAsState()

    AnimatedVisibility(
        visible = uiState.isMusicBottomBarVisible,
        enter = slideInVertically(
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
    ) {
        BottomMusicPlayer(
            song = uiState.currentPlayingMusic,
            isPlaying = uiState.isPlaying,
            onPlayPauseButtonPressed = viewModel::onPlayPauseButtonPressed,
            onPlayListButtonPressed = viewModel::onPlayListButtonPressed,
            onItemClick = {
//                navigator.navigate(
//                    MusicPlayerScreenDestination()
//                )
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
//                navigator.navigate(TestScreenDestination())
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
        MainScreen(NavController(context), viewModel, musicPlayerViewModel)
    }
}