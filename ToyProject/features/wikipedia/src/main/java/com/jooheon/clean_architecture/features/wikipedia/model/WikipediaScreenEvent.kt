package com.jooheon.clean_architecture.features.wikipedia.model

import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation

sealed class WikipediaScreenEvent {
    data class OnSearchButtonClick(val searchWord: String): WikipediaScreenEvent()
    data class OnRelatedPageItemClick(val item: Entity.Related.Page): WikipediaScreenEvent()

    companion object {
        fun navigateToDetailScreen(
            navigator: NavController,
            item: Entity.Related.Page
        ) {
            navigator.navigate(
                ScreenNavigation.Detail.WikipediaDetail.createRoute(item)
            ) {
                launchSingleTop = true
            }
        }
    }
}