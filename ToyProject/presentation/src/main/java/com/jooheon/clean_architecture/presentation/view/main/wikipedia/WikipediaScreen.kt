package com.jooheon.clean_architecture.presentation.view.main.wikipedia

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.main.ScreenNavigation
import com.jooheon.clean_architecture.presentation.utils.ObserveAlertDialogState
import com.jooheon.clean_architecture.presentation.utils.ObserveLoadingState
import com.jooheon.clean_architecture.presentation.view.temp.EmptyWikipediaUseCase

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "WikipediaScreen"

@ExperimentalComposeUiApi
@Composable
fun WikipediaScreen(
    navigator: NavController,
    viewModel: WikipediaViewModel = hiltViewModel(),
    isPreview: Boolean = false
) {
    val localFocusManager = LocalFocusManager.current
    val searchWord = viewModel.searchWord.collectAsState()
    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                }
            )
        }
    ) {

//        SearchView(
//            title = "input wiki\nKeyword",
//            content = searchWord.value,
//            onTextChanged = { viewModel.searchWord.value = it },
//            onButtonClicked = { viewModel.callRelatedApi() }
//        )
        WikipediaListView(viewModel, isPreview)
    }
    ObserveAlertDialogState(viewModel)
    ObserveLoadingState(viewModel)
    ObserveEvents(navigator, viewModel)
}

@Composable
private fun WikipediaListView(
    viewModel: WikipediaViewModel,
    isPreview: Boolean
) {
    val relatedList by viewModel.relatedResponse.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            relatedList?.pages?.let { pages ->
                itemsIndexed(pages) { index, page ->
                    WikipediaListItem(
                        index = index,
                        page = page,
                        onClicked = viewModel::onRelatedItemClicked
                    )
                }
            }
            if(isPreview) {
                items(10) { index ->
                    val page = EmptyWikipediaUseCase.dummyData(index)
                    WikipediaListItem(index, page)
                }
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


@Composable
private fun ObserveEvents(
    navigator: NavController,
    viewModel: WikipediaViewModel,
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->
                lifecycleOwner.lifecycleScope.launch {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.navigateToWikipediaDetailScreen.collectLatest {
                            navigator.navigate(
                                ScreenNavigation.Detail.WikipediaDetail.createRoute(it)
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
fun PreviewWikipediaScreen() {
    val context = LocalContext.current
    val viewModel = WikipediaViewModel(EmptyWikipediaUseCase())
    PreviewTheme(false) {
        WikipediaScreen(NavController(context), viewModel, true)
    }
}
