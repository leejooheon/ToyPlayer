package com.jooheon.clean_architecture.presentation.view.main.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CollectEvent(
    event: SharedFlow<Boolean>,
    navigateTo: () -> Unit
) {
    LaunchedEffect(Unit) {
        event.collectLatest {
            if (it) navigateTo()
        }
    }
}
