package com.jooheon.clean_architecture.presentation.view.main.bottom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.UiText
import com.jooheon.clean_architecture.presentation.view.components.outlinedTextFieldColor
import com.jooheon.clean_architecture.presentation.view.main.wikipedia.WikipediaViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyWikipediaUseCase
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
internal fun SearchView(
    title: String,
    content: String,
    onTextChanged: (text: String) -> Unit,
    onButtonClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val maxCharacterSize = 10

        var text by remember { mutableStateOf(content) }
        val keyboardController = LocalSoftwareKeyboardController.current

        Text(
            modifier = Modifier.padding(10.dp),
            text = title,
            color = MaterialTheme.colorScheme.onSecondary,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis
        )

        OutlinedTextField(
            modifier = Modifier.width(120.dp),
            value = text,
            onValueChange = {
                if(it.length <= maxCharacterSize) {
                    text = it
                    onTextChanged(text)
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
            label = {
                Text(
                    text = "Input",
                    color = MaterialTheme.colorScheme.onTertiary
                )
            },
            placeholder = {
                Text(
                    text = "this is placeholder",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                )
            },
            colors = outlinedTextFieldColor(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )

        OutlinedButton(
            modifier = Modifier
                .padding(horizontal = 10.dp),
            onClick = {
                keyboardController?.hide()
                onButtonClicked()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = UiText.StringResource(R.string.ok).asString(),
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}

@Preview
@ExperimentalComposeUiApi
@Composable
fun PreviewMyNavigationDrawer() {
    val viewModel = WikipediaViewModel(EmptyWikipediaUseCase())
    val state = MutableStateFlow("")
    PreviewTheme(false) {
        SearchView(
            title = "this is preview\n" + "PREVIEW",
            content = "",
            onTextChanged = {},
            onButtonClicked = {}
        )
    }
}
