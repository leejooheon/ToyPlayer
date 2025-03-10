package com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.features.common.compose.components.CoilImage
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.core.resources.UiText


@Composable
internal fun MusicAlbumDetailHeader(
    album: Album,
    onPlayAllClick: (shuffle: Boolean) -> Unit,
) {
    val albumDuration = album.songs.fastSumBy { it.duration.toInt() }.toLong()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        CoilImage(
            url = album.imageUrl,
            contentDescription = album.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(15))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.default_album_art),
                contentDescription = "Default Album Art",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = album.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${album.artist} • ${MusicUtil.toReadableDurationString(albumDuration)}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            OutlinedButton(
                onClick = { onPlayAllClick(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = UiText.StringResource(R.string.action_play_all).asString(),
                    color = MaterialTheme.colorScheme.onTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            OutlinedButton(
                onClick = { onPlayAllClick(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = UiText.StringResource(R.string.action_play_all_shuffle).asString(),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Composable
private fun MusicAlbumDetailHeaderPreview() {
    ToyPlayerTheme {
        MusicAlbumDetailHeader(
            album = Album.default.copy(
                name = "name",
                artist = "artist"
            ),
            onPlayAllClick = {},
        )
    }
}