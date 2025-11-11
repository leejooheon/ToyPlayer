package com.jooheon.toyplayer.features.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.settings.presentation.equalizer.EqualizerScreen
import com.jooheon.toyplayer.features.settings.presentation.main.SettingScreen
import com.jooheon.toyplayer.features.settings.presentation.theme.ThemeScreen

class SettingsEntryProviderInstaller(
    private val navigator: Navigator,
): EntryProviderInstaller {
    override operator fun invoke(
        builder: EntryProviderScope<ScreenNavigation>
    ) = with(builder) {
        entry(ScreenNavigation.Settings.Main) {
            SettingScreen(
                navigateTo = navigator::navigateTo,
                onBack = navigator::popBackStack,
            )
        }
        entry(ScreenNavigation.Settings.Theme) {
            ThemeScreen(
                onBack = navigator::popBackStack,
            )
        }
        entry(ScreenNavigation.Settings.Equalizer) {
            EqualizerScreen(
                onBack = navigator::popBackStack,
            )
        }
    }
}