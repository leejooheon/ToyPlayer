package com.jooheon.toyplayer.features.musicplayer.presentation.common.controller

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import kotlin.math.min

@Composable
fun MediaBottomController(
    motionProgress: Float,
    song: Song,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onPlayPauseButtonClicked: (Song) -> Unit,
    onPlayingQueueButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(1f - min(motionProgress * 2, 1f))
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            PlayPauseButton(
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                onPlayPauseButtonPressed = { onPlayPauseButtonClicked(song)} ,
                modifier = Modifier
                    .wrapContentHeight()
                    .aspectRatio(1f)
                    .alpha(1f - min(motionProgress * 2, 1f)),
            )
            PlayListButton(
                onPlayingQueueClick = onPlayingQueueButtonPressed,
                modifier = Modifier
                    .wrapContentHeight()
                    .aspectRatio(1f)
                    .alpha(1f - min(motionProgress * 2, 1f)),
            )
        }
    }
}
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MediaBottomControllerPreview() {
    PreviewTheme {
        Surface(modifier = Modifier.width(400.dp)) {
            MediaBottomController(
                motionProgress = 0f,
                song = Song.default.copy(
                    title = "Song Title",
                    artist = "Song Artist"
                ),
                isPlaying = true,
                isBuffering = true,
                onPlayPauseButtonClicked = {},
                onPlayingQueueButtonPressed = {},
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )
        }
    }
}