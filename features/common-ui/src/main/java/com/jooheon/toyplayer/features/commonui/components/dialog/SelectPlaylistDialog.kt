package com.jooheon.toyplayer.features.commonui.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
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
        modifier = Modifier
            .heightIn(min = 0.dp, max = 300.dp)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = UiText.StringResource(Strings.dialog_select_playlist).asString(),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        models.forEach { playlist ->
            PlaylistOutlinedButton(
                onClick = { onPlaylistClick.invoke(playlist) },
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                        contentDescription = UiText.StringResource(Strings.option_add_playlist).asString()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        ),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistOutlinedButton(
    modifier: Modifier = Modifier,
    scale: Float = 0.95f,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(all = 0.dp),
        modifier = modifier
            .bounceClick(
                scale = scale,
                onClick = onClick,
            )
            .semantics(
                mergeDescendants = true,
                properties = {
                    role = Role.Button
                }
            ),
        content = content,
    )
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