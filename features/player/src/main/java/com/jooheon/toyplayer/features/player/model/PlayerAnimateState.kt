package com.jooheon.toyplayer.features.player.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

data class PlayerAnimateState(
    var start: Boolean,
    val imageReady: Boolean,
    val containerAnimation: Animatable<Float, AnimationVector1D>,
    val swipeAnimState: MutableState<Pair<Boolean, Int>>,
) {
    val shouldRun: Boolean get() { return start && imageReady }

    suspend fun cancel() {
        val shortTime = 250.milliseconds.inWholeMilliseconds.toInt()
        showAnimation(shortTime)
        swipeAnimState.value = false to 0
    }

    suspend fun start(showSwipe: Boolean) {
        val shortTime = 250.milliseconds.inWholeMilliseconds.toInt()
        val longTime = 1400.milliseconds.inWholeMilliseconds.toInt()

        start = false
        hideAnimation(shortTime)
        showAnimation(longTime)

        if(showSwipe) {
            swipeAnimation()
        }
    }

    private suspend fun swipeAnimation() {
        val time = 750
        swipeAnimState.value = true to time
        withContext(Dispatchers.IO) { delay(time.toLong()) }
        swipeAnimState.value = false to time
        withContext(Dispatchers.IO) { delay(time * 2L) }
    }

    private suspend fun showAnimation(time: Int) {
        containerAnimation.animateTo(1f, animationSpec = tween(durationMillis = time))
    }

    private suspend fun hideAnimation(time: Int) = withContext(Dispatchers.Main) {
        launch {
            containerAnimation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = time)
            )
        }
    }

    companion object {
        val default = PlayerAnimateState(
            start = false,
            imageReady = false,
            containerAnimation = Animatable(1f),
            swipeAnimState = mutableStateOf(false to 0)
        )
    }
}