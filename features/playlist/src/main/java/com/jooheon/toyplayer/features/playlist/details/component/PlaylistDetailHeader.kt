package com.jooheon.toyplayer.features.playlist.details.component

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.commonui.components.CustomGlideImage

@Composable
internal fun PlaylistDetailHeader(
    playlist: Playlist,
    onPlayAllClick: (shuffle: Boolean) -> Unit,
    onThumbnailImageClick: () -> Unit,
) {
    val allDuration = MusicUtil.toReadableDurationString(playlist.songs.fastSumBy { it.duration.toInt() }.toLong())
    val songCount = UiText.StringResource(Strings.n_song, playlist.songs.size).asString()

    val thumbnailUrl = playlist.thumbnailUrl
        .ifBlank { playlist.songs.firstOrNull()?.imageUrl.defaultEmpty() }

    Row(modifier = Modifier.fillMaxSize()) {
        CustomGlideImage(
            url = thumbnailUrl,
            contentDescription = playlist.name,
            modifier = Modifier
                .padding(12.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(15))
                .weight(0.3f)
                .clickable { onThumbnailImageClick.invoke() }
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onPlayAllClick(false) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier
                        .weight(0.75f)
                        .bounceClick { onPlayAllClick(false) },
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = UiText.StringResource(Strings.action_play_all).asString(),
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))

                Button(
                    onClick = { onPlayAllClick(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .aspectRatio(1f)
                        .weight(0.25f)
                        .clip(CircleShape)
                        .bounceClick { onPlayAllClick(true) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = UiText.StringResource(Strings.action_play_all_shuffle).asString(),
                        modifier = Modifier.scale(2f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
private fun MusicPlaylistDetailHeaderPreview() {
    ToyPlayerTheme {
        PlaylistDetailHeader(
            playlist = Playlist.preview,
            onPlayAllClick = {},
            onThumbnailImageClick = {},
        )
    }
}