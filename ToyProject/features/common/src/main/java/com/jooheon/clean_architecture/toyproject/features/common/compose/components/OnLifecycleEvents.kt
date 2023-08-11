package com.jooheon.clean_architecture.toyproject.features.common.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
private fun OnLifecycleEvents(
    onStart: (() -> Unit)? = null,
    onResume: (() -> Unit)? = null,
    onPause: (() -> Unit)? = null,
    onStop: (() -> Unit)? = null,
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->
                when(event) {
                    Lifecycle.Event.ON_START -> onStart?.invoke()
                    Lifecycle.Event.ON_RESUME -> onResume?.invoke()
                    Lifecycle.Event.ON_PAUSE -> onPause?.invoke()
                    Lifecycle.Event.ON_STOP -> onStop?.invoke()
                    else -> { /** Nothing**/ }
                }
                if(event == Lifecycle.Event.ON_START) {
                    onStart?.invoke()
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
}