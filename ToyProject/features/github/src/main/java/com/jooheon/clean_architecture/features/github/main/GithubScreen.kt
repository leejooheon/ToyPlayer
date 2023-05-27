package com.jooheon.clean_architecture.features.github.main

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.github.main.components.RepositoryColumn
import com.jooheon.clean_architecture.features.github.main.components.SearchView
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


val CardWidth = 170.dp
val CardPadding = 16.dp
@ExperimentalComposeUiApi
@Composable
fun GithubScreen(
    navigator: NavController,
    viewModel: GithubScreenViewModel = hiltViewModel(),
    isPreview: Boolean = false
) {
    val localFocusManager = LocalFocusManager.current
    val githubId = viewModel.githubId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }
    ) {
        SearchView(
            title = "input your\ngithub id",
            content = githubId.value,
            onTextChanged = { viewModel.githubId.value = it },
            onButtonClicked = { viewModel.callRepositoryApi() }
        )
        RepositoryColumn(
            idState = viewModel.githubId,
            itemsState = viewModel.repositoryList,
            onRepositoryClick = viewModel::onRepositoryClicked,
            isPreview = isPreview
        )
    }
    ObserveEvents(navigator, viewModel)
}
@Composable
private fun ObserveEvents(
    navigator: NavController,
    viewModel: GithubScreenViewModel,
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->
                lifecycleOwner.lifecycleScope.launch {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.navigateToGithubDetailScreen.collectLatest {
                            navigator.navigate(
                                ScreenNavigation.Detail.GithubDetail.createRoute(
                                    githubId = viewModel.githubId.value,
                                    repository = it
                                )
                            ) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
}
@ExperimentalComposeUiApi
@Preview
@Composable
private fun HomeScreenPreview() {
    val context = LocalContext.current
    val viewModel = GithubScreenViewModel(EmptyGithubUseCase())
    PreviewTheme(true) {
        GithubScreen(
            NavController(context),
            viewModel,
            true
        )
    }
}