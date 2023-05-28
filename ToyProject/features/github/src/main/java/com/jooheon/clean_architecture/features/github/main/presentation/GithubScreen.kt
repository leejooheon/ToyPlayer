package com.jooheon.clean_architecture.features.github.main.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.github.main.presentation.components.RepositoryColumn
import com.jooheon.clean_architecture.features.github.main.presentation.components.SearchView
import com.jooheon.clean_architecture.features.github.main.model.GithubEvent
import com.jooheon.clean_architecture.features.github.main.model.GithubState


val CardWidth = 170.dp
val CardPadding = 16.dp
@ExperimentalComposeUiApi
@Composable
fun GithubScreen(
    state: GithubState,
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
}
@ExperimentalComposeUiApi
@Preview
@Composable
private fun HomeScreenPreview() {
    PreviewTheme(true) {
        GithubScreen(
            state = GithubState.default.copy(
                items = listOf(
                    Entity.Repository.default.copy(name = "1111"),
                    Entity.Repository.default.copy(name = "2222"),
                    Entity.Repository.default.copy(name = "3333"),
                    Entity.Repository.default.copy(name = "4444"),
                )
            ),
            onEvent = { _, _ -> }
        )
    }
}