package com.jooheon.clean_architecture.features.wikipedia.model

import androidx.navigation.NavController
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation

enum class WikipediaScreenEvent {
    GetData, GoToDetailScreen;
    companion object {
        fun navigateToDetailScreen(
            navigator: NavController,
            state: WikipediaScreenState
        ) {
            val item = state.selectedItem ?: return
            navigator.navigate(
                ScreenNavigation.Detail.WikipediaDetail.createRoute(item)
            ) {
                launchSingleTop = true
            }
        }
    }
}