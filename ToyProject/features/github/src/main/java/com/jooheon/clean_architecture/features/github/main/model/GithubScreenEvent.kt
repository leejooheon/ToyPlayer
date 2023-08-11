package com.jooheon.clean_architecture.features.github.main.model

import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation

sealed class GithubScreenEvent {
     data class OnSearchButtonClick(val githubId: String): GithubScreenEvent()
     data class OnRepositoryClick(val repository: Entity.Repository?): GithubScreenEvent()

     companion object {
          fun navigateToDetailScreen(
               navigator: NavController,
               state: GithubScreenState
          ) {
               val item = state.selectedItem ?: return
               navigator.navigate(
                    ScreenNavigation.Detail.GithubDetail.createRoute(
                         githubId = state.id,
                         repository = item
                    )
               ) {
                    launchSingleTop = true
               }
          }
     }
}
