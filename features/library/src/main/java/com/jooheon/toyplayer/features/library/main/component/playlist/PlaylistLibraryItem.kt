package com.jooheon.toyplayer.features.library.main.component.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.features.commonui.components.OutlinedText

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun PlaylistLibraryItem(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f, true)
            .clip(RoundedCornerShape(4.dp))
            .composed {
                val color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                val alpha = 0.8f
                drawBehind { drawRect(color, alpha = alpha) }
            }
            .clickable { onClick.invoke() }
            .padding(horizontal = 8.dp),
        propagateMinConstraints = true
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                imageVector = icon,
                contentDescription = null,
            )

            OutlinedText(
                text = name,
                outlineColor = MaterialTheme.colorScheme.onPrimary,
                fillColor = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
