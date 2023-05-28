package com.jooheon.clean_architecture.features.github.main.model

import androidx.navigation.NavController
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation

enum class GithubScreenEvent {
     GetGithubRepositoryData, GoToDetailScreen;

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