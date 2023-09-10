package com.jooheon.clean_architecture.features.main.navigation

import android.os.Bundle
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.Serializable

@Stable
@Composable
fun NavController.currentBottomNavScreenAsState(): State<ScreenNavigation> {
    val selectedItem = remember { mutableStateOf<ScreenNavigation>(ScreenNavigation.BottomSheet.Music) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == ScreenNavigation.BottomSheet.Github.route } -> {
                    selectedItem.value = ScreenNavigation.BottomSheet.Github
                }
                destination.hierarchy.any { it.route == ScreenNavigation.BottomSheet.Wiki.route } -> {
                    selectedItem.value = ScreenNavigation.BottomSheet.Wiki
                }
                destination.hierarchy.any { it.route == ScreenNavigation.BottomSheet.Map.route } -> {
                    selectedItem.value = ScreenNavigation.BottomSheet.Map
                }
                destination.hierarchy.any { it.route == ScreenNavigation.BottomSheet.Music.route } -> {
                    selectedItem.value = ScreenNavigation.BottomSheet.Music
                }
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}

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

// https://pluu.github.io/blog/android/2022/02/04/compose-pending-argument-part-2/
inline fun <reified T : Serializable> createSerializableNavType(
    isNullableAllowed: Boolean = false
): NavType<T> {
    return object : NavType<T>(isNullableAllowed) {
        override val name: String
            get() = "SupportSerializable"

        override fun put(bundle: Bundle, key: String, value: T) {
            bundle.putSerializable(key, value) // Bundle에 Serializable 타입으로 추가
        }

        override fun get(bundle: Bundle, key: String): T? {
            return bundle.getSerializable(key) as? T // Bundle에서 Serializable 타입으로 꺼낸다
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString(value) // String 전달된 Parsing 방법을 정의
        }
    }
}