package com.jooheon.toyplayer.features.main.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import timber.log.Timber

class MainNavigator: Navigator {
    override val backStack: SnapshotStateList<ScreenNavigation> = mutableStateListOf(ScreenNavigation.Splash)

    override fun navigateTo(destination: ScreenNavigation) {
        backStack.add(destination)

        when (destination) {
            is ScreenNavigation.Player -> {
                backStack.remove(ScreenNavigation.Splash)
            }
            else -> { /** nothing **/ }
        }
        printBackStack()
    }

    override fun popBackStack() {
        backStack.removeLastOrNull()
        printBackStack()
    }

    private fun printBackStack() {
        Timber.d("backStack: $backStack")
    }
}