package com.jooheon.clean_architecture.presentation.view.main.wikipedia

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.CustomTheme
import com.jooheon.clean_architecture.presentation.theme.ProvideCustomColors
import com.jooheon.clean_architecture.presentation.utils.*
import com.jooheon.clean_architecture.presentation.view.temp.EmptyWikipediaUseCase
import com.jooheon.clean_architecture.presentation.view.temp.PreviewPallete

private const val TAG = "WikipediaScreen"

@ExperimentalComposeUiApi
@Composable
fun WikipediaScreen(
    viewModel: WikipediaViewModel = hiltViewModel(),
    isPreview: Boolean = false
) {
    Column {
        SearchView(viewModel)
        WikipediaListView(viewModel, isPreview)
    }
    ObserveAlertDialogState(viewModel)
    ObserveLoadingState(viewModel)
}

@ExperimentalComposeUiApi
@Composable
private fun SearchView(
    viewModel: WikipediaViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val maxCharacterSize = 10
        var text by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current


        Text(
            modifier = Modifier.padding(10.dp),
            text = "Input wiki\nKeyword",
            color = CustomTheme.colors.textPrimary,
            textAlign = TextAlign.Start,

            style = MaterialTheme.typography.h5,
            overflow = TextOverflow.Ellipsis
        )

        OutlinedTextField(
            modifier = Modifier.width(150.dp),
            value = text,
            onValueChange = {
                if(it.length <= maxCharacterSize) {
                    text = it
                }
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            label = { Text(text = "Input") },
            placeholder = {
                Text(
                    text = "github id",
                    style = TextStyle(
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = CustomTheme.colors.textPrimary,
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Green,
                unfocusedLabelColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )

        OutlinedButton(
            modifier = Modifier
                .padding(start = 10.dp),
            onClick = {
                keyboardController?.hide()
                viewModel.callRelatedApi(text) // FIXME
                Log.d(TAG, "onClick!!")
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = CustomTheme.colors.uiBackground
            )
        ) {
            Text("확인")
        }
    }
}

@Composable
private fun WikipediaListView(
    viewModel: WikipediaViewModel,
    isPreview: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            viewModel.relatedResponse.value?.pages?.let { pages ->
                itemsIndexed(pages) { index, page ->
                    WikipediaListItem(index, page)
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

@Composable
private fun WikipediaListItem(index: Int, page: Entity.Related.Page) {
    val title = page.displaytitle ?: run { "data is empty." }
    val description = page.extract ?: run { "data is empty." }
    val imgUrl = page.thumbnail?.source ?: run { R.drawable.ic_logo_github }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = CustomTheme.colors.uiBackground,
        elevation = 5.dp
    ) {
        Row(modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
        ) {
            Image(
                painter = rememberImagePainter(
                    data = imgUrl,
                    builder = {
                        crossfade(true)
                        placeholder(drawableResId = R.drawable.ic_logo_github)
                    }
                ),
                contentDescription = "description",
                modifier = Modifier.width(120.dp),
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
                    color = CustomTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                )
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    text = description,
                    color = CustomTheme.colors.textSecondary,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
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
    ProvideCustomColors(colors = PreviewPallete) {
        WikipediaScreen(viewModel, true)
    }
}
