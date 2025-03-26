package com.jooheon.toyplayer.features.main.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import timber.log.Timber

class MainNavigator(
    val navController: NavHostController,
) {
    internal val startDestination = ScreenNavigation.Splash

    internal val navigateTo: (ScreenNavigation) -> Unit = { destination ->
        printBackStack(destination)
        when (destination) {
            is ScreenNavigation.Player -> {
                navController.navigate(ScreenNavigation.Player) {
                    launchSingleTop = true
                    popUpTo(ScreenNavigation.Splash) { inclusive = true }
                }
            }
            is ScreenNavigation.Back -> popBackStack()
            else -> navController.navigate(destination)
        }
        printBackStack(destination)
    }

    private fun popBackStack() {
        val success = navController.popBackStack()
        Timber.d("popBackStack result: $success")
    }

    @SuppressLint("RestrictedApi")
    fun printBackStack(destination: ScreenNavigation) {
        val backStackEntries = navController.currentBackStack.value.map { it.destination.route }
        Timber.d("backStack[$destination]: $backStackEntries")
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}