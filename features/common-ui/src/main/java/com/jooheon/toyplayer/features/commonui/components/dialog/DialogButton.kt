package com.jooheon.toyplayer.features.commonui.components.dialog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick

@Composable
fun DialogButton(
    text: String,
    style: TextStyle,
    enable: Boolean = true,
    alpha: Float = 1f,
    onClick: () -> Unit,
) {
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    Button (
        onClick = onClick,
        enabled = enable,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = color,
            disabledContentColor = color,
            disabledContainerColor = color,
        ),
        contentPadding = PaddingValues(
            horizontal = 29.dp,
            vertical = 10.dp
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.bounceClick(onClick = onClick),
    ) {
        Text(
            text = text,
            style = style,
            modifier = Modifier.alpha(alpha)
        )
    }
}