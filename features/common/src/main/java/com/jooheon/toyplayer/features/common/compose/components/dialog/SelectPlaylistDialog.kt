package com.jooheon.toyplayer.features.common.compose.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist

@Composable
fun SelectPlaylistDialog(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val models = playlists.ifEmpty {
        listOf(
            Playlist.default.copy(
                name = UiText.StringResource(Strings.dialog_new_playlist).asString()
            )
        )
    }

    DialogColumn(
        fraction = 0.7f,
        padding = 16.dp,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.heightIn(min = 0.dp, max = 300.dp)
    ) {
        Text(
            text = UiText.StringResource(Strings.dialog_select_playlist).asString(),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        models.forEachIndexed { index, playlist ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onPlaylistClick.invoke(playlist) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = playlist.name,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if(models.lastIndex != index) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Preview
@Composable
private fun PlaylistDialogContentPreview() {
    ToyPlayerTheme {
        SelectPlaylistDialog(
            playlists = emptyList(),//listOf(Playlist.default),
            onPlaylistClick = {},
            onDismissRequest = {},
        )
    }
}