package com.jooheon.clean_architecture.presentation.view.main.watched

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.clean_architecture.presentation.theme.themes.CustomTheme


@Composable
fun WatchedScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.material3Colors.primary)
    ) {
        Text(
            text = "WatchedScreen",
            color = CustomTheme.colors.material3Colors.onPrimary
        )
    }
}

@Preview
@Composable
fun test() {
    WatchedScreen()
}