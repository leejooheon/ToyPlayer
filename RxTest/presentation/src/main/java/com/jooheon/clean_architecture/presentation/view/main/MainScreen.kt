package com.jooheon.clean_architecture.presentation.view.main

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jooheon.clean_architecture.presentation.theme.CustomTheme
import com.jooheon.clean_architecture.presentation.view.custom.GithubSearchDialog
import com.jooheon.clean_architecture.presentation.view.destinations.TestScreenDestination
import com.jooheon.clean_architecture.presentation.view.home.*
import com.jooheon.clean_architecture.presentation.view.main.bottom.MyBottomNavigation
import com.jooheon.clean_architecture.presentation.view.main.bottom.Screen
import com.jooheon.clean_architecture.presentation.view.main.bottom.currentScreenAsState
import com.jooheon.clean_architecture.presentation.view.main.following.FollowingScreen
import com.jooheon.clean_architecture.presentation.view.main.home.HomeScreen
import com.jooheon.clean_architecture.presentation.view.main.search.SearchScreen
import com.jooheon.clean_architecture.presentation.view.main.watched.WatchedScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

const val TAG = "MainScreen"

@Destination(start = true)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    navigator: DestinationsNavigator
) {
    val viewModel: MainViewModel = hiltViewModel()
    val bottomNavController = rememberAnimatedNavController()

    Scaffold(
        backgroundColor = CustomTheme.colors.uiBackground,
        topBar = { TopBar(viewModel, navigator) },
        bottomBar = { BottomBar(bottomNavController) },
        drawerContent = { DrawerContent() },
        content = { RegisterBottomNavigation(viewModel, bottomNavController) }
    )
}

@Composable
fun DrawerContent() {
    Text(text = "drawerContent")
}

@Composable
fun RegisterBottomNavigation(
    viewModel: MainViewModel,
    navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel)
        }
        composable(Screen.Following.route) {
            FollowingScreen()
        }
        composable(Screen.Watched.route) {
            WatchedScreen()
        }
        composable(Screen.Search.route) {
            SearchScreen()
        }
    }
}

@Composable
fun BottomBar(bottomNavController: NavController) {
    val currentSelectedItem by bottomNavController.currentScreenAsState()
    MyBottomNavigation(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
fun TopBar(
    viewModel: MainViewModel,
    navigator: DestinationsNavigator
) {

    TopAppBar(
        title = { Text(text = "My ToyProject") },
        backgroundColor = Color.White,
        navigationIcon = {
            IconButton(onClick = {
                viewModel.onNavigationClicked()
                Log.d(TAG, "Menu IconButton")
            }) {
                Icon(Icons.Filled.Menu, contentDescription = null)
            }
        },
        actions = {
            val openDialog = remember { mutableStateOf(false) }
            if(openDialog.value) {
                GithubSearchDialog(openDialog = openDialog, onDismiss = { owner ->
                    if (!owner.isEmpty()) {
                        Log.d(TAG, owner)
                        viewModel.callRepositoryApi(owner)
                    }
                })
            }

            IconButton(onClick = {
                viewModel.onFavoriteClicked()
                Log.d(TAG, "Favorite IconButton")
            }) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "first IconButton description"
                )
            }
            IconButton(onClick = {
                openDialog.value = true
            }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "second IconButton description"
                )
            }
            IconButton(onClick = {
//                        viewModel.onSettingClicked()
                Log.d(TAG, "Settings IconButton")
                navigator.navigate(TestScreenDestination())
            }) {
                Icon(Icons.Filled.Settings, contentDescription = null)
            }
        }
    )
}