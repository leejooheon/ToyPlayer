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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.ObserveAlertDialogState
import com.jooheon.clean_architecture.presentation.utils.ObserveLoadingState
import com.jooheon.clean_architecture.presentation.view.destinations.WikipediaDatailScreenDestination
import com.jooheon.clean_architecture.presentation.view.main.bottom.SearchView
import com.jooheon.clean_architecture.presentation.view.temp.EmptyWikipediaUseCase
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

private const val TAG = "WikipediaScreen"

@ExperimentalComposeUiApi
@Composable
fun WikipediaScreen(
    navigator: DestinationsNavigator,
    viewModel: WikipediaViewModel = hiltViewModel(),
    isPreview: Boolean = false
) {
    val localFocusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                }
            )
        }
    ) {

        SearchView(
            title = "input wiki\nKeyword",
            content = viewModel.searchWord.value,
            onTextChanged = { viewModel.searchWord.value = it },
            onButtonClicked = { viewModel.callRelatedApi() }
        )
        WikipediaListView(navigator, viewModel, isPreview)
    }
    ObserveAlertDialogState(viewModel)
    ObserveLoadingState(viewModel)
}

//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//private fun SearchView(
//    viewModel: WikipediaViewModel
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(100.dp)
//            .padding(top = 10.dp),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        val maxCharacterSize = 10
//        var text by remember { mutableStateOf(viewModel.searchWord.value) }
//        val keyboardController = LocalSoftwareKeyboardController.current
//
//        Text(
//            modifier = Modifier.padding(10.dp),
//            text = "Input wiki\nKeyword",
//            color = MaterialTheme.colorScheme.primary,
//            textAlign = TextAlign.Start,
//            style = MaterialTheme.typography.bodyLarge,
//            overflow = TextOverflow.Ellipsis
//        )
//
//        OutlinedTextField(
//            modifier = Modifier.width(120.dp),
//            value = text,
//            onValueChange = {
//                if(it.length <= maxCharacterSize) {
//                    viewModel.searchWord.value = it
//                    text = it
//                }
//            },
//            singleLine = true,
//            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
//            label = {
//                Text(
//                    text = "Input",
//                    color = MaterialTheme.colorScheme.tertiary
//                )
//            },
//            placeholder = {
//                Text(
//                    text = "this is placeholder",
//                    style = TextStyle(
//                        color = MaterialTheme.colorScheme.secondary,
//                        textAlign = TextAlign.Center
//                    )
//                )
//            },
////            colors = outlinedTextFieldColor(),
//            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
//            keyboardActions = KeyboardActions(
//                onDone = { keyboardController?.hide() }
//            )
//        )
//
//        OutlinedButton(
//            modifier = Modifier
//                .padding(horizontal = 10.dp),
//            onClick = {
//                keyboardController?.hide()
//                viewModel.callRelatedApi()
//            },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.secondary
//            )
//        ) {
//            Text(
//                text = "확인",
//                color = MaterialTheme.colorScheme.onSecondary
//            )
//        }
//    }
//}

@Composable
private fun WikipediaListView(
    navigator: DestinationsNavigator,
    viewModel: WikipediaViewModel,
    isPreview: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            viewModel.relatedResponse.value?.pages?.let { pages ->
                itemsIndexed(pages) { index, page ->
                    WikipediaListItem(index, page) {
                        Log.d(TAG, "onClicked: ${it.displaytitle}")
                        navigator.navigate(WikipediaDatailScreenDestination.invoke("Bug"))
                    }
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
    val imgUrl = page.thumbnail?.source ?: run { R.drawable.ic_logo_github }

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
            Image(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = imgUrl).apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                        placeholder(drawableResId = R.drawable.ic_logo_github)
                    }).build()
                ),
                contentDescription = "description",
                contentScale = ContentScale.Crop,
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
    val viewModel = WikipediaViewModel(EmptyWikipediaUseCase())
    PreviewTheme(false) {
        WikipediaScreen(EmptyDestinationsNavigator, viewModel, true)
    }
}
