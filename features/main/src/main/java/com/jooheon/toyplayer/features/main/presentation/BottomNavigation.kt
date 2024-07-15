package com.jooheon.toyplayer.features.main.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.BottomNavigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation.Bottom.Album.route
import com.jooheon.toyplayer.features.common.compose.theme.colors.AlphaNearOpaque
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.main.navigation.BottomScreenNavigationItem
import timber.log.Timber

@Composable
internal fun MyBottomNavigation(
    selectedNavigation: ScreenNavigation.Bottom,
    onNavigationSelected: (ScreenNavigation.Bottom) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = AlphaNearOpaque),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
        modifier = modifier
    ) {
        BottomScreenNavigationItem.items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = rememberVectorPainter(item.iconImageVector),
                        contentDescription = item.contentDescription.asString()
                    )
                    BottomNavigationItemIcon(
                        item = item,
                        selected = selectedNavigation == item.screen
                    )
                },
                label = {
                    Text(
                        text = item.label.asString(),
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
private fun BottomNavigationItemIcon(item: BottomScreenNavigationItem, selected: Boolean) {
    val painter = rememberVectorPainter(item.iconImageVector)
    val selectedPainter = rememberVectorPainter(item.selectedImageVector)

    Crossfade(targetState = selected, label = "") {
        Icon(
            painter = if (it) selectedPainter else painter,
            contentDescription = item.contentDescription.asString(),
        )
    }
}

@Stable
@Composable
fun NavController.currentBottomNavScreenAsState(): State<ScreenNavigation.Bottom> {
    val selectedItem = remember { mutableStateOf<ScreenNavigation.Bottom>(ScreenNavigation.Bottom.Song) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = when(destination.route) {
                ScreenNavigation.Bottom.Song.route() -> ScreenNavigation.Bottom.Song
                ScreenNavigation.Bottom.Album.route() -> ScreenNavigation.Bottom.Album
                ScreenNavigation.Bottom.Artist.route() -> ScreenNavigation.Bottom.Artist
                ScreenNavigation.Bottom.Cache.route() -> ScreenNavigation.Bottom.Cache
                ScreenNavigation.Bottom.Playlist.route() -> ScreenNavigation.Bottom.Playlist
                else -> ScreenNavigation.Bottom.Song
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewBottomNav() {
    val bottomNavController = rememberAnimatedNavController()
    val currentSelectedItem by bottomNavController.currentBottomNavScreenAsState()
    PreviewTheme(false) {
        MyBottomNavigation(
            modifier = Modifier.fillMaxWidth(),
            selectedNavigation = currentSelectedItem,
            onNavigationSelected = { }
        )
    }
}
