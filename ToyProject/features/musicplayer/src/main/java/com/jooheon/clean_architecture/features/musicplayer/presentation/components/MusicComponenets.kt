package com.jooheon.clean_architecture.features.musicplayer.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.features.common.utils.MusicUtil
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicservice.data.albumArtUri
import com.jooheon.clean_architecture.features.common.R


@Composable
internal fun MusicProgress(
    modifier: Modifier,
    maxDuration: Long,
    currentDuration: Long,
    onChanged: (progress: Float) -> Unit
) {
    Log.d("Jooheon", "currentDuration: ${currentDuration}, max: ${maxDuration}")
    val progress = remember(maxDuration, currentDuration) {
        currentDuration.toFloat() / maxDuration.toFloat()
    }

    Column(modifier = modifier) {
        Slider(
            value = progress,
            onValueChange = onChanged
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = MusicUtil.toReadableDurationString(currentDuration),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
            Text(
                text = MusicUtil.toReadableDurationString(maxDuration),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
        }
    }
}

@Composable
internal fun MusicControlButtons(
    isPlaying: Boolean,
    shuffleMode: ShuffleMode,
    repeatMode: RepeatMode,
    onShuffleModePressed: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPauseButtonPressed: () -> Unit,
    onNext: () -> Unit,
    onRepeatModePressed: () -> Unit,
    iconRelativeSize: Float = 0.4f,
    modifier: Modifier = Modifier
) {
    val shuffleIconResId = when(shuffleMode) {
        ShuffleMode.SHUFFLE -> R.drawable.ic_shuffle_on_circled
        ShuffleMode.NONE -> R.drawable.ic_shuffle_off_circled
    }
    val repeatIconResId = when(repeatMode) {
        RepeatMode.REPEAT_ALL -> R.drawable.ic_repeat_white_circle
        RepeatMode.REPEAT_ONE -> R.drawable.ic_repeat_one
        RepeatMode.REPEAT_OFF -> R.drawable.ic_repeat
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        RoundImageButton(
            image = repeatIconResId,
            iconTint = MaterialTheme.colorScheme.onBackground,
            iconRelativeSize = iconRelativeSize,
            backgroundColor = MaterialTheme.colorScheme.background,
            contentDescription = UiText.StringResource(R.string.action_cycle_repeat).asString(),
            onClick = onRepeatModePressed,
            modifier = Modifier.size(92.dp),
        )

        RoundImageButton(
            image = R.drawable.ic_skip_previous,
            iconTint = MaterialTheme.colorScheme.onTertiary,
            iconRelativeSize = iconRelativeSize,
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentDescription = UiText.StringResource(R.string.action_previous).asString(),
            onClick = onPrevious,
        )
        RoundImageButton(
            image = getPlayPauseIcon(isPlaying),
            iconTint = MaterialTheme.colorScheme.onTertiary,
            iconRelativeSize = iconRelativeSize,
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentDescription = if (isPlaying) PAUSE_MUSIC_CD else PLAY_MUSIC_CD,
            onClick = onPlayPauseButtonPressed,
        )
        RoundImageButton(
            image = R.drawable.ic_skip_next,
            iconTint = MaterialTheme.colorScheme.onTertiary,
            iconRelativeSize = iconRelativeSize,
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentDescription = UiText.StringResource(R.string.action_next).asString(),
            onClick = onNext,
        )

        RoundImageButton(
            image = shuffleIconResId,
            iconTint = MaterialTheme.colorScheme.onBackground,
            iconRelativeSize = iconRelativeSize,
            backgroundColor = MaterialTheme.colorScheme.background,
            contentDescription = UiText.StringResource(R.string.action_toggle_shuffle).asString(),
            onClick = onShuffleModePressed,
            modifier = Modifier.size(92.dp),
        )
    }
}

@Composable
internal fun OtherButtons(
    modifier: Modifier = Modifier,
    shuffleMode: ShuffleMode,
    repeatMode: RepeatMode,
    onShuffleModePressed: () -> Unit,
    onRepeatModePressed: () -> Unit,
    iconRelativeSize: Float = 0.4f,
) {
    val shuffleIconResId = when(shuffleMode) {
        ShuffleMode.SHUFFLE -> R.drawable.ic_shuffle_on_circled
        ShuffleMode.NONE -> R.drawable.ic_shuffle_off_circled
    }
    val repeatIconResId = when(repeatMode) {
        RepeatMode.REPEAT_ALL -> R.drawable.ic_repeat_white_circle
        RepeatMode.REPEAT_ONE -> R.drawable.ic_repeat_one
        RepeatMode.REPEAT_OFF -> R.drawable.ic_repeat
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        RoundImageButton(
            image = repeatIconResId,
            iconTint = MaterialTheme.colorScheme.onBackground,
            iconRelativeSize = iconRelativeSize,
            backgroundColor = Color.Transparent,
            contentDescription = UiText.StringResource(R.string.action_cycle_repeat).asString(),
            onClick = onRepeatModePressed,
            modifier = Modifier.size(92.dp),
        )

        RoundImageButton(
            image = shuffleIconResId,
            iconTint = MaterialTheme.colorScheme.onBackground,
            iconRelativeSize = iconRelativeSize,
            backgroundColor = Color.Transparent,
            contentDescription = UiText.StringResource(R.string.action_toggle_shuffle).asString(),
            onClick = onShuffleModePressed,
            modifier = Modifier.size(92.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MusicItem(
    modifier: Modifier,
    song: Song,
    surfaceColor: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
    onItemClick: (Song) -> Unit
) {
    Surface(
        onClick = { onItemClick(song) },
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
        color = surfaceColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            CoilImage(
                url = song.albumArtUri.toString(),
                contentDescription = song.title + "_Image",
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.labelSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
            Text(
                text = MusicUtil.toReadableDurationString(song.duration),
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = textColor.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
internal fun EmptySongItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\uD83D\uDE31",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "no songs!",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
internal fun AlbumImage(
    isPlaying: Boolean,
    song: Song,
    modifier: Modifier = Modifier
) {
    val borderThickness = (0.5).dp
    val borderColor = MaterialTheme.colorScheme.surface

    Card(
        border = BorderStroke(
            width = borderThickness,
            color = borderColor,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = modifier
            .padding(12.dp)
            .clip(CircleShape)
            .aspectRatio(1.0f)
            .background(MaterialTheme.colorScheme.background)
    ) {
        CoilImage(
            url = song.albumArtUri.toString(),
            contentDescription = song.album,
            placeholderRes = R.drawable.ic_placeholder,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1.0f),
        )
    }
}

@Composable
internal fun PlayPauseButton(
    isPlaying: Boolean,
    iconRelativeSize: Float = 0.4f,
    onPlayPauseButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RoundImageButton(
        image = getPlayPauseIcon(isPlaying),
        iconTint = MaterialTheme.colorScheme.tertiary,
        iconRelativeSize = iconRelativeSize,
        backgroundColor = Color.Transparent,
        contentDescription = if (isPlaying) PAUSE_MUSIC_CD else PLAY_MUSIC_CD,
        onClick = {
            onPlayPauseButtonPressed()
        },
        modifier = modifier
    )
}

@Composable
internal fun PlayListButton(
    iconRelativeSize: Float = 0.4f,
    onPlayListButtonPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoundImageButton(
        image = R.drawable.ic_round_playlist_play_24,
        iconTint = MaterialTheme.colorScheme.tertiary,
        iconRelativeSize = iconRelativeSize,
        backgroundColor = Color.Transparent,
        contentDescription = PLAY_LIST_CD,
        onClick = onPlayListButtonPressed,
        modifier = modifier,
    )
}

@Composable
internal fun RoundImageButton(
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

private fun getPlayPauseIcon(isPlaying: Boolean) = if (isPlaying) {
    R.drawable.ic_pause
} else {
    R.drawable.ic_play_arrow
}

const val PLAY_MUSIC_CD = "Play music"
const val PAUSE_MUSIC_CD = "Pause music"
const val PLAY_LIST_CD = "Play list"


@Preview
@Composable
private fun MusicItemPreviewLight() {
    PreviewTheme(false) {
        MusicItem(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .height(96.dp),
            song = Song.default,
            onItemClick = { }
        )
    }
}

@Preview
@Composable
private fun MusicItemPreviewDark() {
    PreviewTheme(true) {
        MusicItem(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .height(96.dp),
            song = Song.default,
            onItemClick = { }
        )
    }
}

@Preview
@Composable
private fun AlbumImagePreview() {
    PreviewTheme(false) {
        AlbumImage(
            isPlaying = true,
            song = Song.default,
            modifier = Modifier.size(96.dp)
        )

//            .fillMaxHeight()
//            .aspectRatio(1.0f)
//            .rotate(currentAngle)
    }
}

@Preview
@Composable
fun PlayButtonPreviewLight() {
    PreviewTheme(false) {
        RoundImageButton(
            image = R.drawable.ic_pause,
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
            image = R.drawable.ic_play_arrow,
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
fun PlayListButtonPreviewLight() {
    PreviewTheme(false) {
        RoundImageButton(
            image = R.drawable.ic_round_playlist_play_24,
            iconTint = MaterialTheme.colorScheme.tertiary,
            iconRelativeSize = 0.4f,
            backgroundColor = Color.Transparent,
            contentDescription = PLAY_LIST_CD,
            onClick = {},
            modifier = Modifier.size(72.dp),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlayListButtonPreviewDark() {
    PreviewTheme(true) {
        RoundImageButton(
            image = R.drawable.ic_round_playlist_play_24,
            iconTint = MaterialTheme.colorScheme.tertiary,
            iconRelativeSize = 0.4f,
            backgroundColor = Color.Transparent,
            contentDescription = PLAY_LIST_CD,
            onClick = {},
            modifier = Modifier.size(72.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewEmptySong() {
    PreviewTheme(false) {
        EmptySongItem(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
