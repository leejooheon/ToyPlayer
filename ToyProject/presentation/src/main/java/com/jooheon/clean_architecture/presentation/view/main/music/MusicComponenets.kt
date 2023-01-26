package com.jooheon.clean_architecture.presentation.view.main.music

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
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.extensions.albumArtUri
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import com.jooheon.clean_architecture.presentation.view.components.CoilImage
import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.layoutId
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.domain.entity.Entity.RepeatMode
import com.jooheon.clean_architecture.domain.entity.Entity.ShuffleMode
import com.jooheon.clean_architecture.presentation.utils.UiText


@Composable
internal fun MusicProgress(
    modifier: Modifier,
    maxDuration: Long,
    currentDuration: Long,
    onChanged: (progress: Float) -> Unit
) {
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
    onPlayPauseButtonPressed: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .layoutId("music_control_buttons")
            .fillMaxWidth(0.8f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                painter = painterResource(R.drawable.ic_skip_previous),
                contentDescription = UiText.StringResource(R.string.action_previous).asString()
            )
        }
        IconButton(onClick = onPlayPauseButtonPressed) {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                painter = painterResource(
                    id = if(isPlaying) R.drawable.ic_pause
                    else R.drawable.ic_play_arrow,
                ),
                contentDescription = UiText.StringResource(R.string.action_play_pause).asString()
            )
        }

        IconButton(onClick = onNext) {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                painter = painterResource(R.drawable.ic_skip_next),
                contentDescription = UiText.StringResource(R.string.action_next).asString()
            )
        }
    }
}

@Composable
internal fun OtherButtons(
    modifier: Modifier = Modifier,
    shuffleMode: ShuffleMode,
    repeatMode: RepeatMode,
    onShuffleModePressed: () -> Unit,
    onRepeatModePressed: () -> Unit,
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
        IconButton(onClick = onRepeatModePressed) {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                painter = painterResource(repeatIconResId),
                contentDescription = UiText.StringResource(R.string.action_cycle_repeat).asString()
            )
        }

        IconButton(onClick = onShuffleModePressed) {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                painter = painterResource(shuffleIconResId),
                contentDescription = UiText.StringResource(R.string.action_toggle_shuffle).asString()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MusicItem(
    modifier: Modifier,
    song: Entity.Song,
    surfaceColor: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
    onItemClick: (Entity.Song) -> Unit
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
                    text = song.artistName,
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
    song: Entity.Song,
    modifier: Modifier = Modifier
) {
    val borderThickness = (0.5).dp
    val borderColor = MaterialTheme.colorScheme.surface
    val circle = RoundedCornerShape(100)
    val bottomMusicPlayerInfiniteTransition = rememberInfiniteTransition()
    val angle by bottomMusicPlayerInfiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3500,
                easing = FastOutSlowInEasing
            )
        )
    )
    var currentAngle by remember { mutableStateOf(0f) }

    LaunchedEffect(angle, isPlaying) {
        if (isPlaying) {
            currentAngle = angle
        }
    }
    Card(
        shape = circle,
        border = BorderStroke(
            width = borderThickness,
            color = borderColor,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = modifier.rotate(currentAngle)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(circle)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(
                        width = borderThickness,
                        color = borderColor,
                        shape = circle
                    )
                    .align(Alignment.Center)
                    .zIndex(2f)
            )

            CoilImage(
                url = song.albumArtUri.toString(),
                contentDescription = song.albumName,
                shape = RoundedCornerShape(10.dp),
                placeholderRes = R.drawable.ic_logo_github,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1.0f)
            )
        }
    }
}

@Composable
internal fun PlayPauseButton(
    song: Entity.Song,
    isPlaying: Boolean,
    iconRelativeSize: Float = 0.4f,
    onPlayPauseButtonPressed: (Entity.Song) -> Unit,
    modifier: Modifier = Modifier,
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

private fun parseSongImageUrl(song: Entity.Song) = if(song.albumId == -1L) {
    Entity.tempImages.first().imageUrl
} else {
    song.albumArtUri.toString()
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
            song = Entity.Song.emptySong,
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
            song = Entity.Song.emptySong,
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
            song = Entity.Song.emptySong,
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
