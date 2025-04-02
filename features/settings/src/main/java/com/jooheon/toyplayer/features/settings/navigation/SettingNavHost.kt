package com.jooheon.toyplayer.features.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.settings.presentation.equalizer.EqualizerScreen
import com.jooheon.toyplayer.features.settings.presentation.main.SettingScreen
import com.jooheon.toyplayer.features.settings.presentation.theme.ThemeScreen

fun NavGraphBuilder.settingNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
    onChangeDarkTheme: (Boolean) -> Unit,
) {
    composable<ScreenNavigation.Settings.Main> {
        SettingScreen(
            navigateTo = navigateTo,
        )
    }

    composable<ScreenNavigation.Settings.Theme> {
        ThemeScreen(
            onChangeDarkTheme = onChangeDarkTheme,
            onBackClick = { navigateTo.invoke(ScreenNavigation.Back) }
        )
    }
    composable<ScreenNavigation.Settings.Equalizer> {
        EqualizerScreen(
            onBackClick = { navigateTo.invoke(ScreenNavigation.Back) }
        )
    }
}