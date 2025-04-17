package com.jooheon.toyplayer.features.player.component.info.control.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.player.common.contentHeight

@Composable
internal fun ControlBottomInfo(
    title: String,
    artist: String,
    duration: Long,
    currentPosition: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = androidx.compose.ui.graphics.Color.White,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = artist,
            style = MaterialTheme.typography.bodySmall.copy(
                color = androidx.compose.ui.graphics.Color.White.copy(
                    alpha = 0.5f
                ),
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (contentHeight() * 0.6f)),
        ) {
            if(duration > 0) {
                ControlSlider(
                    duration = duration,
                    currentPosition = currentPosition,
                    onSeek = onSeek,
                    modifier = Modifier,
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(44.dp)
                    .bounceClick { },
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = android.graphics.Color.GRAY.toLong(),
)
@Composable
private fun PreviewControlBottomInfoSection() {
    ToyPlayerTheme {
        ControlBottomInfo(
            title = Song.preview.title,
            artist = Song.preview.artist,
            duration = 10000L,
            currentPosition = 5000L,
            onSeek = {},
        )
    }
}