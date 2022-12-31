package com.jooheon.clean_architecture.presentation.view.main.github

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.ObserveAlertDialogState
import com.jooheon.clean_architecture.presentation.utils.ObserveLoadingState
import com.jooheon.clean_architecture.presentation.view.components.MyDivider
import com.jooheon.clean_architecture.presentation.view.home.repo.GithubRepositoryItem
import com.jooheon.clean_architecture.presentation.view.main.bottom.SearchView
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.navigation.ScreenNavigation
import com.jooheon.clean_architecture.presentation.view.temp.EmptyGithubUseCase
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@ExperimentalComposeUiApi
@Composable
fun HomeScreen(
    navigator: NavController,
    githubViewModel: GithubViewModel = hiltViewModel(),
    viewModel: MusicPlayerViewModel = hiltViewModel(sharedViewModel()),
    isPreview: Boolean = false
) {
    Log.d(TAG, "MusicPlayerViewModel: ${viewModel}")
    val localFocusManager = LocalFocusManager.current
    val githubId = githubViewModel.githubId.collectAsState()

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
            onTextChanged = { githubViewModel.githubId.value = it },
            onButtonClicked = { githubViewModel.callRepositoryApi() }
        )
        RepositoryItems(
            viewModel = githubViewModel,
            isPreview = isPreview
        )
    }

    ObserveAlertDialogState(githubViewModel)
    ObserveLoadingState(githubViewModel)
    ObserveEvents(
        navigator = navigator,
        viewModel = githubViewModel,
    )
}

@Composable
private fun RepositoryItems(
    viewModel: GithubViewModel,
    isPreview:Boolean = false
) {
    val githubId by viewModel.githubId.collectAsState() // FIXME: id는 다른데서 참조해야할듯
    val repositoryList by viewModel.repositoryList.collectAsState()

    MyDivider(thickness = 2.dp)

    if(repositoryList.isNotEmpty() && !isPreview) {
        GithubRepositoryItem(
            owner = githubId,
            repositoryList = repositoryList,
            onRepositoryClick = viewModel::onRepositoryClicked
        )
    }


    if(isPreview) {
        GithubRepositoryItem(
            owner = "owner",
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            repositoryList = EmptyGithubUseCase.repositoryDummyData(),
            onRepositoryClick = viewModel::onRepositoryClicked
        )
    }
}

@Composable
private fun ObserveEvents(
    navigator: NavController,
    viewModel: GithubViewModel,
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
    val musicPlayerUseCase = MusicPlayerUseCase(EmptyMusicUseCase())
    val musicPlayerViewModel = MusicPlayerViewModel(
        context = context,
        dispatcher= Dispatchers.IO,
        musicController = MusicController(context, musicPlayerUseCase, true)
    )

    val viewModel = GithubViewModel(EmptyGithubUseCase())
    PreviewTheme(true) {
        HomeScreen(NavController(context), viewModel, musicPlayerViewModel, true)
    }
}