package com.jooheon.clean_architecture.presentation.view.main.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "SearchScreen")
    }
}

@Preview
@Composable
fun PreviewSearchScreen() {
    SearchScreen()
}