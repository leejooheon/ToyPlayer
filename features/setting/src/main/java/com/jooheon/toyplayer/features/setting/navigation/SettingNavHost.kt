package com.jooheon.toyplayer.features.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.setting.presentation.equalizer.EqualizerScreen
import com.jooheon.toyplayer.features.setting.presentation.language.LanguageScreen
import com.jooheon.toyplayer.features.setting.presentation.main.SettingScreen
import com.jooheon.toyplayer.features.setting.presentation.theme.ThemeScreen

fun NavGraphBuilder.settingNavGraph(
    onBackClick: () -> Unit,
    navigate: (ScreenNavigation.Setting) -> Unit,
) {
    composable<ScreenNavigation.Setting.Main> {
        SettingScreen()
    }

    composable<ScreenNavigation.Setting.Language> {
        LanguageScreen()
    }

    composable<ScreenNavigation.Setting.Theme> {
        ThemeScreen()
    }

    composable<ScreenNavigation.Setting.Equalizer> {
        EqualizerScreen()
    }

}