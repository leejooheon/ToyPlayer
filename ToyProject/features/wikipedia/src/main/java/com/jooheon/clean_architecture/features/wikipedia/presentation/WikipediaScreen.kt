package com.jooheon.clean_architecture.features.wikipedia.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.wikipedia.presentation.components.SearchView
import com.jooheon.clean_architecture.features.wikipedia.model.WikipediaScreenEvent
import com.jooheon.clean_architecture.features.wikipedia.model.WikipediaScreenState

private const val TAG = "WikipediaScreen"

@ExperimentalComposeUiApi
@Composable
fun WikipediaScreen(
    state: WikipediaScreenState,
    onEvent: (WikipediaScreenEvent, WikipediaScreenState) -> Unit
) {
    val localFocusManager = LocalFocusManager.current
    var searchWordState by rememberSaveable { mutableStateOf(state.searchWord) }
    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = { localFocusManager.clearFocus() })
            }
    ) {
        SearchView(
            title = "input wiki\nKeyword",
            content = searchWordState,
            onTextChanged = { searchWordState = it },
            onButtonClicked = { onEvent(WikipediaScreenEvent.GetData, state.copy(searchWord = searchWordState)) }
        )
        WikipediaListView(
            state = state,
            onEvent = onEvent
        )
    }
}

@Composable
private fun WikipediaListView(
    state: WikipediaScreenState,
    onEvent: (WikipediaScreenEvent, WikipediaScreenState) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(state.relatedItems) { index, page ->
                WikipediaListItem(
                    index = index,
                    page = page,
                    onClicked = {
                        onEvent(WikipediaScreenEvent.GoToDetailScreen, state.copy(selectedItem = it))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WikipediaListItem(
    index: Int,
    page: Entity.Related.Page,
    onClicked: ((Entity.Related.Page) -> Unit)? = null) {
    val title = page.displaytitle ?: run { "data is empty." }
    val description = page.extract ?: run { "data is empty." }
    val imgUrl = page.thumbnail?.source ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onClicked?.let { it(page) }
            },
        shape = RoundedCornerShape(8.dp),

//        backgroundColor = MaterialTheme.colorScheme.background,
//        elevation = 5.dp
    ) {
        Row(modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
        ) {
            CoilImage(
                url = imgUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
            )

            Column(modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    text = description,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewWikipediaScreen() {
//    val context = LocalContext.current
//    val viewModel = WikipediaScreenViewModel(EmptyWikipediaUseCase())
    PreviewTheme(false) {
        WikipediaScreen(
            state = WikipediaScreenState.default.copy(
                relatedItems = listOf(
                    Entity.Related.Page.default.copy(displaytitle = "display_title - 1111"),
                    Entity.Related.Page.default.copy(displaytitle = "display_title - 2222"),
                    Entity.Related.Page.default.copy(displaytitle = "display_title - 3333"),
                    Entity.Related.Page.default.copy(displaytitle = "display_title - 4444"),
                )
            ),
            onEvent = { _, _ -> }
        )
    }
}
