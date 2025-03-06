package com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.features.common.compose.components.CoilImage
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.core.strings.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist

@Composable
internal fun MusicPlaylistDetailHeader(
    playlist: Playlist,
    onPlayAllClick: (shuffle: Boolean) -> Unit,
) {
    val allDuration = MusicUtil.toReadableDurationString(playlist.songs.fastSumBy { it.duration.toInt() }.toLong())
    val songCount = UiText.StringResource(R.string.n_song, playlist.songs.size).asString()

    val thumbnailUrl = playlist.thumbnailUrl.ifBlank {
        playlist.songs.firstOrNull()?.imageUrl
    }.default("empty")

    Row(modifier = Modifier.fillMaxSize()) {
        CoilImage(
            url = thumbnailUrl,
            contentDescription = playlist.name,
            placeholderRes = R.drawable.default_album_art,
            modifier = Modifier
                .padding(12.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(15))
                .weight(0.3f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = playlist.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge .copy(
                    fontWeight = FontWeight.Bold
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$songCount • $allDuration",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                TextButton(
                    onClick = { onPlayAllClick(false) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(25))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .weight(0.75f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = null,
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))
                TextButton(
                    onClick = { onPlayAllClick(true) },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .weight(0.25f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MusicPlaylistDetailHeaderPreview() {
    ToyPlayerTheme {
        MusicPlaylistDetailHeader(
            playlist = Playlist.default,
            onPlayAllClick = {},
        )
    }
}