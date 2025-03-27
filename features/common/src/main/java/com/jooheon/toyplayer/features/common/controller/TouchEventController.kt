package com.jooheon.toyplayer.features.common.controller

import android.view.MotionEvent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

object TouchEventController {
    private val _event = Channel<MotionEvent?>()
    val event = _event.receiveAsFlow()

    @OptIn(FlowPreview::class)
    val debouncedEvent: Flow<Unit> = event
        .filterNotNull()
        .debounce(100L)
        .map { }
        .flowOn(Dispatchers.IO)

    suspend fun sendEvent(event: MotionEvent?) {
        _event.send(event)
    }
}