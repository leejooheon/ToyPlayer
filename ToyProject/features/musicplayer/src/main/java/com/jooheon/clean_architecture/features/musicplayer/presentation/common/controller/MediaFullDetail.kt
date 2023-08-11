package com.jooheon.clean_architecture.features.musicplayer.presentation.common.controller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.toyproject.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicservice.data.albumArtUri

@Composable
fun MediaFullDetails(
    song: Song,
    modifier: Modifier = Modifier,
) {

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .padding(all = 10.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
        ) {
            Text(
                text = song.title,
                color = colorScheme.onBackground,
                style = typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "ThumbUp",
                        tint = colorScheme.onBackground
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = colorScheme.onBackground
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 10.dp)
        ) {

            CoilImage(
                url = song.albumArtUri.toString(),
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = song.album,
                color = colorScheme.onBackground,
                style = typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = song.artist,
                color = colorScheme.onBackground.copy(alpha = 0.6f),
                style = typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

    }
}

@Preview
@Composable
private fun MediaFullDetailsPreviewDark() {
    PreviewTheme(true) {
        Surface(modifier = Modifier.width(400.dp)) {
            MediaFullDetails(
                song = Song.default.copy(
                    title = "Song Title Song Title Song Title Song Title",
                    artist = "Song Artist Song Artist Song Artist Song Artist",
                    album = "Song Album Song Album Song Album Song Album"
                )
            )
        }
    }
}
@Preview
@Composable
private fun MediaFullDetailsPrevieLight() {
    PreviewTheme(false) {
        Surface(modifier = Modifier.width(400.dp)) {
            MediaFullDetails(
                song = Song.default.copy(
                    title = "Song Title Song Title Song Title Song Title",
                    artist = "Song Artist Song Artist Song Artist Song Artist",
                    album = "Song Album Song Album Song Album Song Album"
                )
            )
        }
    }
}