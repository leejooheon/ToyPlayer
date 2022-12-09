package com.jooheon.clean_architecture.presentation.view.main.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicItem(
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
fun EmptySongItem(modifier: Modifier = Modifier) {
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
private fun PreviewEmptySong() {
    ApplicationTheme(false) {
        EmptySongItem(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
