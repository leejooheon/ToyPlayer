package com.jooheon.clean_architecture.presentation.view.temp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination


@Destination
@Composable
fun TestScreen() {
    val viewModel: TestViewModel = hiltViewModel()
    Column(modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally) {
        ClickableText(
            text = AnnotatedString(viewModel.text.value),
            onClick = {
                viewModel.onClicked()
            }
        )
    }
}