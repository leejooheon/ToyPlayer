package com.jooheon.clean_architecture.presentation.view.main.bottom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.view.main.music.*
import com.jooheon.clean_architecture.presentation.view.main.music.AlbumImage
import com.jooheon.clean_architecture.presentation.view.main.music.PlayListButton
import com.jooheon.clean_architecture.presentation.view.main.music.PlayPauseButton

private const val TAG = "MusicBottomBar"
private val MOTION_CONTENT_HEIGHT = 64.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomMusicPlayer(
    song: Song,
    isPlaying: Boolean,
    onItemClick: () -> Unit,
    onPlayPauseButtonPressed: (Song) -> Unit,
    onPlayListButtonPressed: () -> Unit,
) {
    Surface(
        onClick = onItemClick,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,) {
            AlbumImage(
                isPlaying = isPlaying,
                song = song,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(MOTION_CONTENT_HEIGHT)
                    .aspectRatio(1.0f)
            )
            Column(modifier = Modifier.weight(1f)) {
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
                    overflow = TextOverflow.Ellipsis
                )
            }
            PlayPauseButton(
                song = song,
                isPlaying = isPlaying,
                onPlayPauseButtonPressed = onPlayPauseButtonPressed,
                modifier = Modifier
                    .height(MOTION_CONTENT_HEIGHT)
                    .aspectRatio(1f)
            )
            PlayListButton(
                onPlayListButtonPressed = onPlayListButtonPressed,
                modifier = Modifier
                    .height(MOTION_CONTENT_HEIGHT)
                    .aspectRatio(1f)
            )
        }
    }
}
@Preview
@Composable
private fun MusicBottomBarPreview() {
    PreviewTheme(false) {
        BottomMusicPlayer(
            song = Song.default,
            isPlaying = true,
            onItemClick = { },
            onPlayPauseButtonPressed = { },
            onPlayListButtonPressed = { },
        )
    }
}