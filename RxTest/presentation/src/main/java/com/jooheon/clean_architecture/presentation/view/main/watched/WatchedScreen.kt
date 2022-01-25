package com.jooheon.clean_architecture.presentation.view.main.watched

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun WatchedScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "WatchedScreen")
    }
}