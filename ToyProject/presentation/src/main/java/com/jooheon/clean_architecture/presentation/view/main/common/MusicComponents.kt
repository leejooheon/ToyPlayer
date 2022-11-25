package com.jooheon.clean_architecture.presentation.view.main.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import com.jooheon.clean_architecture.presentation.view.components.CoilImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicBottomBar(
    song: Entity.Song,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (Entity.Song) -> Unit,
    onPlayPauseButtonPressed: (Entity.Song) -> Unit
) {
    Surface(
        onClick = { onItemClick(song) },
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            CoilImage(
                url = parseSongImageUrl(song),
                contentDescription = song.albumName,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1.0f)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Column(
                modifier = Modifier.weight(1f)
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
                    text = song.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    overflow = TextOverflow.Ellipsis
                )
            }
            PlayPauseButton(
                song = song,
                isPlaying = isPlaying,
                onPlayPauseButtonPressed = onPlayPauseButtonPressed
            )
        }
    }
}

@Composable
fun PlayPauseButton(
    song: Entity.Song,
    isPlaying: Boolean,
    iconRelativeSize: Float = 0.4f,
    onPlayPauseButtonPressed: (Entity.Song) -> Unit
) {
    RoundImageButton(
        image = getPlayPauseIcon(isPlaying),
        iconTint = MaterialTheme.colorScheme.tertiary,
        iconRelativeSize = iconRelativeSize,
        backgroundColor = Color.Transparent,
        contentDescription = if (isPlaying) PAUSE_MUSIC_CD else PLAY_MUSIC_CD,
        onClick = {
            onPlayPauseButtonPressed(song)
        },
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f),
        iconOffset = if (isPlaying) 0.dp else 4.dp,
    )
}

@Composable
fun RoundImageButton(
    image: Int,
    iconTint: Color,
    iconRelativeSize: Float,
    backgroundColor: Color,
    contentDescription: String,
    modifier: Modifier = Modifier,
    iconOffset: Dp = 0.dp,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(color = backgroundColor),
        enabled = isEnabled
    ) {
        Icon(
            painter = painterResource(id = image),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize(iconRelativeSize)
                .offset(x = iconOffset),
            tint = iconTint.copy(alpha = if (isEnabled) 1f else 0.5f)
        )
    }
}

private fun parseSongImageUrl(song: Entity.Song) = if(song.albumId == -1L) {
    Entity.tempImages.first().imageUrl
} else {
    MusicUtil.getMediaStoreAlbumCoverUri(song.albumId).toString()
}

private fun getPlayPauseIcon(isPlaying: Boolean) = if (isPlaying) {
    R.drawable.ic_pause
} else {
    R.drawable.ic_play_arrow
}

const val PLAY_MUSIC_CD = "Play music"
const val PAUSE_MUSIC_CD = "Pause music"

@Preview
@Composable
private fun MusicBottomBarPreviewLight() {
    PreviewTheme(false) {
        MusicBottomBar(
            song = Entity.Song.emptySong,
            isPlaying = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            onItemClick = { },
            onPlayPauseButtonPressed = { }
        )
    }
}

@Preview
@Composable
private fun MusicBottomBarPreviewDark() {
    PreviewTheme(true) {
        MusicBottomBar(
            song = Entity.Song.emptySong,
            isPlaying = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            onItemClick = { },
            onPlayPauseButtonPressed = { }
        )
    }
}
@Preview
@Composable
fun PlayButtonPreviewLight() {
    PreviewTheme(false) {
        RoundImageButton(
            image = R.drawable.ic_pause_white_48dp,
            iconTint = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentDescription = "Play Music",
            iconRelativeSize = 0.4f,
            modifier = Modifier.size(72.dp),
        ) {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlayButtonPreviewDark() {
    PreviewTheme(true) {
        RoundImageButton(
            image = R.drawable.ic_play_arrow_white_48dp,
            iconTint = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentDescription = "Play Music",
            iconRelativeSize = 0.4f,
            modifier = Modifier.size(72.dp),
        ) {}
    }
}