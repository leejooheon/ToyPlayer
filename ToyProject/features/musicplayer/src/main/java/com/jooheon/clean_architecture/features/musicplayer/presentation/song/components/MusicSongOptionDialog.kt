package com.jooheon.clean_architecture.features.musicplayer.presentation.song.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.RadioButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.clean_architecture.domain.entity.music.PlaylistType
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R

@Composable
internal fun MusicSongOptionDialog(
    playlistType: PlaylistType,
    openDialog: Boolean,
    onDismiss: (() -> Unit)? = null,
    onOkButtonClicked: ((PlaylistType) -> Unit)? = null
) {
    if(!openDialog) { return }

    var playlistTypeState by remember { mutableStateOf(playlistType) }
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.secondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        onDismissRequest = { onDismiss?.invoke() },
        text = {
            Column {
                TextButton(
                    onClick = { playlistTypeState = PlaylistType.Local },
                    content = {

                        RadioButton(
                            selected = playlistTypeState == PlaylistType.Local,
                            onClick = { playlistTypeState = PlaylistType.Local },
                        )
                        Text(
                            text = UiText.StringResource(R.string.option_only_local).asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                )
                TextButton(
                    onClick = { playlistTypeState = PlaylistType.Streaming },
                    content = {
                        RadioButton(
                            selected = playlistTypeState == PlaylistType.Streaming,
                            onClick = { playlistTypeState = PlaylistType.Streaming },
                        )
                        Text(
                            text = UiText.StringResource(R.string.option_only_streaming).asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                )
                TextButton(
                    onClick = { playlistTypeState = PlaylistType.All },
                    content = {
                        RadioButton(
                            selected = playlistTypeState == PlaylistType.All,
                            onClick = { playlistTypeState = PlaylistType.All },
                        )
                        Text(
                            text = UiText.StringResource(R.string.option_include_all).asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss?.invoke()
                onOkButtonClicked?.invoke(playlistTypeState)
            }) {
                Text(
                    text = UiText.StringResource(R.string.ok).asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    )
}
@Preview
@Composable
private fun MusicSongOptionDialogPreview() {
    PreviewTheme(true) {
        MusicSongOptionDialog(
            playlistType = PlaylistType.All,
            openDialog = true
        )
    }
}