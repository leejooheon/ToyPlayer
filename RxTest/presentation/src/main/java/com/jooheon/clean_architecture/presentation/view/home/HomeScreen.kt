package com.jooheon.clean_architecture.presentation.view.home

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.AppBarAlphas
import com.jooheon.clean_architecture.presentation.utils.HandleApiFailure
import com.jooheon.clean_architecture.presentation.utils.ShowLoading
import com.jooheon.clean_architecture.presentation.view.NavGraphs
import com.jooheon.clean_architecture.presentation.view.custom.GithubRepositoryCard
import com.jooheon.clean_architecture.presentation.view.custom.GithubSearchDialog
import com.jooheon.clean_architecture.presentation.view.destinations.TestScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect

private const val TAG = "Home"

@Destination(start = true)
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val navController = rememberAnimatedNavController()

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            Log.d(TAG, "id: ${it.id}, dest: ${it.destination.displayName}")
        }
    }

    val configuration = LocalConfiguration.current

    Scaffold(
        bottomBar = {
            val currentSelectedItem by navController.currentScreenAsState()
            HomeBottomNavigation(
                selectedNavigation = currentSelectedItem,
                onNavigationSelected = { selectedScreen ->
                    navController.navigate(selectedScreen.route) {
                        launchSingleTop = true
                        restoreState = true

                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        topBar = {
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
        },
        drawerContent = {
            Text(text = "drawerContent")
        }
    ) {
        LaunchInGithubRepositoryComposition(viewModel)
    }
}


// TODO: NavGraphBuilder로 해서 옮기자
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun LaunchInGithubRepositoryComposition(viewModel: HomeViewModel) {
    val response = viewModel.repositoryResponse.value

    when(response) {
        is Resource.Loading -> {
            ShowLoading()
        }
        is Resource.Success -> {
            DrawRepositories(viewModel, response.value)
        }
        is Resource.Failure -> {
            HandleApiFailure(response = response)
        }
        is Resource.Default -> {
            InfoText(text = "Resource.Default")
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun DrawRepositories(
    viewModel: HomeViewModel,
    repositories: List<Entity.Repository>
) {
    val repositoryList = remember { mutableStateListOf<Entity.Repository>() }

    LaunchedEffect(Unit) {
        val list = repositories
        repositoryList.addAll(list)
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Fixed(2)
    ) {
        itemsIndexed(repositories) { index, repository ->
            val modifier = Modifier
                .fillMaxWidth(0.5f) // half width
                .padding(16.dp)
            GithubRepositoryCard(
                modifier,
                repository,
                onItemClicked = {
//                        viewModel.callCommitApi(it.name)
                    viewModel.multipleApiTest(it.name)
                },
                onInfoButtonClicked = {

                }
            )
        }
    }
}

@Composable
fun InfoText(text: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
    }
}

internal sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object Following : Screen("following")
    object Watched : Screen("watched")
    object Search : Screen("search")
}

@Stable
@Composable
private fun NavController.currentScreenAsState(): State<Screen> {
    val selectedItem = remember { mutableStateOf<Screen>(Screen.Home) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == Screen.Home.route } -> {
                    selectedItem.value = Screen.Home
                }
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}

@Composable
internal fun HomeBottomNavigation(
    selectedNavigation: Screen,
    onNavigationSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = AppBarAlphas.translucentBarAlpha()),
        contentColor = contentColorFor(MaterialTheme.colors.surface),
        modifier = modifier
    ) {
        HomeNavigationItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                    HomeNavigationItemIcon(
                        item = item,
                        selected = selectedNavigation == item.screen
                    )
                },
                label = { Text(text = stringResource(item.labelResId)) },
                selected = selectedNavigation == item.screen,
                onClick = { onNavigationSelected(item.screen) },
            )
        }
    }
}

@Composable
private fun HomeNavigationItemIcon(item: HomeNavigationItem, selected: Boolean) {
    val painter = when (item) {
        is HomeNavigationItem.ResourceIcon -> painterResource(item.iconResId)
        is HomeNavigationItem.ImageVectorIcon -> rememberVectorPainter(item.iconImageVector)
    }
    val selectedPainter = when (item) {
        is HomeNavigationItem.ResourceIcon -> item.selectedIconResId?.let { painterResource(it) }
        is HomeNavigationItem.ImageVectorIcon -> item.selectedImageVector?.let { rememberVectorPainter(it) }
    }

    if (selectedPainter != null) {
        Crossfade(targetState = selected) {
            Icon(
                painter = if (it) selectedPainter else painter,
                contentDescription = stringResource(item.contentDescriptionResId),
            )
        }
    } else {
        Icon(
            painter = painter,
            contentDescription = stringResource(item.contentDescriptionResId),
        )
    }
}

private sealed class HomeNavigationItem(
    val screen: Screen,
    @StringRes val labelResId: Int,
    @StringRes val contentDescriptionResId: Int,
) {
    class ResourceIcon(
        screen: Screen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        @DrawableRes val iconResId: Int,
        @DrawableRes val selectedIconResId: Int? = null,
    ) : HomeNavigationItem(screen, labelResId, contentDescriptionResId)

    class ImageVectorIcon(
        screen: Screen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        val iconImageVector: ImageVector,
        val selectedImageVector: ImageVector? = null,
    ) : HomeNavigationItem(screen, labelResId, contentDescriptionResId)
}

private val HomeNavigationItems = listOf(
    HomeNavigationItem.ImageVectorIcon(
        screen = Screen.Home,
        labelResId = R.string.home_title,
        contentDescriptionResId = R.string.cd_home_title,
        iconImageVector = Icons.Outlined.Weekend,
        selectedImageVector = Icons.Default.Weekend,
    ),
    HomeNavigationItem.ImageVectorIcon(
        screen = Screen.Following,
        labelResId = R.string.following_title,
        contentDescriptionResId = R.string.cd_following_title,
        iconImageVector = Icons.Default.FavoriteBorder,
        selectedImageVector = Icons.Default.Favorite,
    ),
    HomeNavigationItem.ImageVectorIcon(
        screen = Screen.Watched,
        labelResId = R.string.watched_title,
        contentDescriptionResId = R.string.cd_watched_title,
        iconImageVector = Icons.Outlined.Visibility,
        selectedImageVector = Icons.Default.Visibility,
    ),
    HomeNavigationItem.ImageVectorIcon(
        screen = Screen.Search,
        labelResId = R.string.search_title,
        contentDescriptionResId = R.string.cd_search_title,
        iconImageVector = Icons.Default.Search,
    ),
)

@Preview
@Composable
fun PreviewHome() {
//    HomeScreen(navigator = DestinationsNavigator())
}