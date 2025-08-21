package com.jooheon.toyplayer.features.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import timber.log.Timber

class MainNavigator() {
    private val _backStack = mutableStateListOf<ScreenNavigation>(startDestination)
    internal val backStack: List<ScreenNavigation> get() = _backStack.toList()

    internal val navigateTo: (ScreenNavigation) -> Unit = { destination ->
        _backStack.add(destination)
        when (destination) {
            is ScreenNavigation.Player -> {
                _backStack.remove(ScreenNavigation.Splash)
            }
            else -> { /** nothing **/ }
        }
        printBackStack()
    }

    internal fun popBackStack() {
        _backStack.removeLastOrNull()
    }

    private fun printBackStack() {
        Timber.d("backStack: $backStack")
    }

    companion object {
        private val startDestination = ScreenNavigation.Splash
    }
}

@Composable
internal fun rememberMainNavigator(): MainNavigator = remember {
    MainNavigator()
}