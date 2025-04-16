package com.jooheon.toyplayer.core.designsystem.ext

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp


private enum class ButtonState { Pressed, Idle }
fun Modifier.bounceClick(
    scale : Float = 0.9f,
    onClick: () -> Unit,
) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }

    val scalingValue by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) scale else 1f,
        label = "Button Scale Animation"
    )

    this
        .graphicsLayer {
            scaleX = scalingValue
            scaleY = scalingValue
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                buttonState = ButtonState.Pressed
                onClick.invoke()
                buttonState = ButtonState.Idle
            }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Modifier.fadingEdge(edgeWidth: Dp): Modifier {
    return this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            fun ContentDrawScope.drawFadedEdge(direction: Boolean, edgeWidthPx: Float) {
                drawRect(
                    topLeft = Offset(if (direction) size.width - edgeWidthPx else 0f, 0f),
                    size = Size(edgeWidthPx, size.height),
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startX = if (direction) size.width else 0f,
                        endX = if (direction) size.width - edgeWidthPx else edgeWidthPx
                    ),
                    blendMode = BlendMode.DstIn
                )
            }

            val edgeWidthPx = edgeWidth.toPx()

            drawContent()
            drawFadedEdge(true, edgeWidthPx)
            drawFadedEdge(false, edgeWidthPx)
        }
}