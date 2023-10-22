package com.jooheon.clean_architecture.toyproject.features.common.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty


@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
    parentRoute: String? = null
): T {
    val navGraphRoute = parentRoute ?: destination.parent?.route ?: destination.route.defaultEmpty()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel<T>(parentEntry)
}