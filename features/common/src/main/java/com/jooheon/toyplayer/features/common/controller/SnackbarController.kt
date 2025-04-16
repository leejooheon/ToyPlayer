package com.jooheon.toyplayer.features.common.controller

import com.jooheon.toyplayer.core.resources.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


data class SnackbarEvent(
    val uiText: UiText,
    val action: SnackbarAction? = null
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit
)

object SnackbarController {
    private val _event = Channel<SnackbarEvent>()
    val event = _event.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _event.send(event)
    }
}