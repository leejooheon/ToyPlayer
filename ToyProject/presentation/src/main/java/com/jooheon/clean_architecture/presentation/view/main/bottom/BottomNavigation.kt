package com.jooheon.clean_architecture.presentation.view.main.bottom

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
//import androidx.compose.material.icons.outlined.Visibility
//import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.BottomNavigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.AlphaNearOpaque
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme


sealed class Screen(val route: String) {
    object Github : Screen("github")
    object Wiki : Screen("wiki")
    object Map : Screen("map")
    object Search : Screen("search")
}

private val BottomNavigationItems = listOf(
    BottomNavigationItem.ImageVectorIcon(
        screen = Screen.Github,
        labelResId = R.string.github_title,
        contentDescriptionResId = R.string.cd_github_title,
        iconImageVector = Icons.Outlined.Search,
        selectedImageVector = Icons.Filled.Search,
    ),
    BottomNavigationItem.ImageVectorIcon(
        screen = Screen.Wiki,
        labelResId = R.string.wikipedia_title,
        contentDescriptionResId = R.string.cd_wikipedia_title,
        iconImageVector = Icons.Outlined.FavoriteBorder,
        selectedImageVector = Icons.Default.Favorite,
    ),
    BottomNavigationItem.ImageVectorIcon(
        screen = Screen.Map,
        labelResId = R.string.map_title,
        contentDescriptionResId = R.string.cd_map_title,
        iconImageVector = Icons.Outlined.Add,
        selectedImageVector = Icons.Default.Add,
    ),
    BottomNavigationItem.ImageVectorIcon(
        screen = Screen.Search,
        labelResId = R.string.search_title,
        contentDescriptionResId = R.string.cd_search_title,
        iconImageVector = Icons.Outlined.Build,
        selectedImageVector = Icons.Default.Build,
    ),
)

@Stable
@Composable
fun NavController.currentScreenAsState(): State<Screen> {
    val selectedItem = remember { mutableStateOf<Screen>(Screen.Github) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == Screen.Github.route } -> {
                    selectedItem.value = Screen.Github
                }
                destination.hierarchy.any { it.route == Screen.Wiki.route } -> {
                    selectedItem.value = Screen.Wiki
                }
                destination.hierarchy.any { it.route == Screen.Map.route } -> {
                    selectedItem.value = Screen.Map
                }
                destination.hierarchy.any { it.route == Screen.Search.route } -> {
                    selectedItem.value = Screen.Search
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
internal fun MyBottomNavigation(
    selectedNavigation: Screen,
    onNavigationSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = AlphaNearOpaque),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
        modifier = modifier
    ) {
        BottomNavigationItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = rememberVectorPainter(item.iconImageVector),
                        contentDescription = stringResource(item.contentDescriptionResId)
                    )
                    BottomNavigationItemIcon(
                        item = item,
                        selected = selectedNavigation == item.screen
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelResId),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = selectedNavigation == item.screen,
                onClick = { onNavigationSelected(item.screen) },
            )
        }
    }
}

@Composable
private fun BottomNavigationItemIcon(item: BottomNavigationItem, selected: Boolean) {
    val painter = when (item) {
        is BottomNavigationItem.ResourceIcon -> painterResource(item.iconResId)
        is BottomNavigationItem.ImageVectorIcon -> rememberVectorPainter(item.iconImageVector)
    }
    val selectedPainter = when (item) {
        is BottomNavigationItem.ResourceIcon -> item.selectedIconResId?.let { painterResource(it) }
        is BottomNavigationItem.ImageVectorIcon -> item.selectedImageVector?.let { rememberVectorPainter(it) }
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

private sealed class BottomNavigationItem(
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
    ) : BottomNavigationItem(screen, labelResId, contentDescriptionResId)

    class ImageVectorIcon(
        screen: Screen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        val iconImageVector: ImageVector,
        val selectedImageVector: ImageVector? = null,
    ) : BottomNavigationItem(screen, labelResId, contentDescriptionResId)
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewBottomNav() {
    val bottomNavController = rememberAnimatedNavController()
    val currentSelectedItem by bottomNavController.currentScreenAsState()
    PreviewTheme(false) {
        MyBottomNavigation(
            modifier = Modifier.fillMaxWidth(),
            selectedNavigation = currentSelectedItem,
            onNavigationSelected = { }
        )
    }
}
