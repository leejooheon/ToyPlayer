package com.jooheon.clean_architecture.presentation.view.main.github

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsHeight
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.ObserveAlertDialogState
import com.jooheon.clean_architecture.presentation.utils.ObserveLoadingState
import com.jooheon.clean_architecture.presentation.view.components.MyDivider
import com.jooheon.clean_architecture.presentation.view.destinations.RepositoryDetailScreenDestination
import com.jooheon.clean_architecture.presentation.view.home.repo.GithubRepositoryItem
import com.jooheon.clean_architecture.presentation.view.main.bottom.SearchView
import com.jooheon.clean_architecture.presentation.view.temp.EmptyGithubUseCase
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator


private const val TAG = "HomeScreen"

@ExperimentalComposeUiApi
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    githubViewModel: GithubViewModel = hiltViewModel(),
    isPreview: Boolean = false
) {
    val localFocusManager = LocalFocusManager.current
    Column(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                localFocusManager.clearFocus()
            })
        }
    ) {
        SearchView(
            title = "input your\ngithub id",
            content = githubViewModel.githubId.value,
            onTextChanged = { githubViewModel.githubId.value = it },
            onButtonClicked = { githubViewModel.callRepositoryApi() }
        )
        RepositoryItems(githubViewModel, navigator, isPreview)
    }
    ObserveAlertDialogState(githubViewModel)
    ObserveLoadingState(githubViewModel)
}

@SuppressLint("UnrememberedMutableState", "StateFlowValueCalledInComposition")
@Composable
fun RepositoryItems(
    viewModel: GithubViewModel,
    navigator:DestinationsNavigator,
    isPreview:Boolean = false
) {
    val githubId = viewModel.githubId.value // FIXME: id는 다른데서 참조해야할듯
    val response = viewModel.repositoryResponse.value

    MyDivider(thickness = 2.dp)

    response?.let {
        GithubRepositoryItem(
            owner = githubId,
            repositoryList = it) { item ->
            Log.d(TAG, "onClicked: $item")
            navigator.navigate(RepositoryDetailScreenDestination(githubId, item)) {
                launchSingleTop = true
            }
        }
    }

    if(isPreview) {
        GithubRepositoryItem(
            owner = "owner",
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            repositoryList = EmptyGithubUseCase.repositoryDummyData(),
            onRepositoryClick = {
                // nothing
                navigator.navigate(RepositoryDetailScreenDestination(githubId, it)) {
                    launchSingleTop = true
                }
            }
        )
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun HomeScreenPreview() {
    val viewModel = GithubViewModel(EmptyGithubUseCase())
    PreviewTheme(true) {
        HomeScreen(EmptyDestinationsNavigator, viewModel, true)
    }
}