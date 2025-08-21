package com.jooheon.toyplayer.features.settings.navigation

import androidx.navigation3.runtime.NavEntry
import com.jooheon.toyplayer.core.navigation.NavMapper
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.settings.presentation.equalizer.EqualizerScreen
import com.jooheon.toyplayer.features.settings.presentation.main.SettingScreen
import com.jooheon.toyplayer.features.settings.presentation.theme.ThemeScreen

fun settingsNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
    onBack: () -> Unit,
): NavMapper = { key ->
    when (key) {
        is ScreenNavigation.Settings.Main  -> NavEntry(key) {
            SettingScreen(
                navigateTo = navigateTo,
                onBack = onBack,
            )
        }
        is ScreenNavigation.Settings.Theme  -> NavEntry(key) {
            ThemeScreen(
                onChangeDarkTheme = {}, // fIXME
                onBack = onBack,
            )
        }
        is ScreenNavigation.Settings.Equalizer -> NavEntry(key) {
            EqualizerScreen(
                onBack = onBack
            )
        }
        else -> null
    }
}