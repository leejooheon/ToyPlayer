package com.jooheon.clean_architecture.features.github.main.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.github.main.components.RepositoryColumn
import com.jooheon.clean_architecture.features.github.main.components.SearchView
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.github.main.data.GithubEvent
import com.jooheon.clean_architecture.features.github.main.data.GithubState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


val CardWidth = 170.dp
val CardPadding = 16.dp
@ExperimentalComposeUiApi
@Composable
fun GithubScreen(
    navigator: NavController,
    state: GithubState,
    navigateChannel: Flow<GithubState>,
    onEvent: (GithubEvent, GithubState) -> Unit,
) {
    val localFocusManager = LocalFocusManager.current

    var idState by rememberSaveable { mutableStateOf(state.id) }

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
            content = idState,
            onTextChanged = { idState = it },
            onButtonClicked = { onEvent(GithubEvent.GetGithubRepositoryData, state.copy(id = idState)) }
        )
        RepositoryColumn(
            state = state,
            onRepositoryClick = {
                onEvent(GithubEvent.GoToDetailScreen, state.copy(selectedItem = it))
            },
        )
    }
    ObserveEvents(navigator, state, navigateChannel)
}
@Composable
private fun ObserveEvents(
    navigator: NavController,
    state: GithubState,
    navigateChannel: Flow<GithubState>,
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->
                lifecycleOwner.lifecycleScope.launch {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        navigateChannel.collectLatest { state ->
                            state.selectedItem ?: return@collectLatest

                            navigator.navigate(
                                ScreenNavigation.Detail.GithubDetail.createRoute(
                                    githubId = state.id,
                                    repository = state.selectedItem
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
    PreviewTheme(true) {
        GithubScreen(
            navigator = NavController(context),
            state = GithubState.default.copy(
                items = listOf(
                    Entity.Repository.default.copy(name = "1111"),
                    Entity.Repository.default.copy(name = "2222"),
                    Entity.Repository.default.copy(name = "3333"),
                    Entity.Repository.default.copy(name = "4444"),
                )
            ),
            navigateChannel = Channel<GithubState>().receiveAsFlow(),
            onEvent = { _, _ -> }
        )
    }
}