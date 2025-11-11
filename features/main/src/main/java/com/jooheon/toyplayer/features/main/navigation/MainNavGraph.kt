package com.jooheon.toyplayer.features.main.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.library.main.LibraryScreen
import com.jooheon.toyplayer.features.player.PlayerScreen
import com.jooheon.toyplayer.features.splash.SplashScreen

class MainEntryProviderInstaller(
    private val navigator: Navigator,
): EntryProviderInstaller {
    override operator fun invoke(
        builder: EntryProviderScope<ScreenNavigation>
    ) = with(builder) {
        entry<ScreenNavigation.Splash> {
            SplashScreen(
                navigateTo = navigator::navigateTo
            )
        }
        entry<ScreenNavigation.Player> {
            PlayerScreen(
                navigateTo = navigator::navigateTo
            )
        }
        entry<ScreenNavigation.Library> {
            LibraryScreen(
                navigateTo = navigator::navigateTo,
                onBack = navigator::popBackStack
            )
        }
    }
}