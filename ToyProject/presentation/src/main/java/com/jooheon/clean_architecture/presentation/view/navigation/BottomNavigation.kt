package com.jooheon.clean_architecture.presentation.view.navigation

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.BottomNavigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jooheon.clean_architecture.features.common.compose.theme.colors.AlphaNearOpaque
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.UiText

@Composable
internal fun MyBottomNavigation(
    selectedNavigation: ScreenNavigation,
    onNavigationSelected: (ScreenNavigation) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = AlphaNearOpaque),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
        modifier = modifier
    ) {
        ScreenNavigation.BottomSheet.items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = rememberVectorPainter(item.iconImageVector),
                        contentDescription = UiText.StringResource(item.contentDescriptionResId).asString()
                    )
                    BottomNavigationItemIcon(
                        item = item,
                        selected = selectedNavigation == item.screen
                    )
                },
                label = {
                    Text(
                        text = UiText.StringResource(item.labelResId).asString(),
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
                contentDescription = UiText.StringResource(item.contentDescriptionResId).asString(),
            )
        }
    } else {
        Icon(
            painter = painter,
            contentDescription = UiText.StringResource(item.contentDescriptionResId).asString(),
        )
    }
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
