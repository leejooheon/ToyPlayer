package com.jooheon.toyplayer.features.musicplayer.presentation.song.components

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
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.core.strings.UiText
import com.jooheon.toyplayer.domain.model.music.MusicListType

@Composable
internal fun MusicSongOptionDialog(
    musicListType: MusicListType,
    openDialog: Boolean,
    onDismiss: (() -> Unit)? = null,
    onOkButtonClicked: ((MusicListType) -> Unit)? = null
) {
    if(!openDialog) { return }

    var playlistTypeState by remember { mutableStateOf(musicListType) }
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.secondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        onDismissRequest = { onDismiss?.invoke() },
        text = {
            Column {
                TextButton(
                    onClick = { playlistTypeState = MusicListType.Local },
                    content = {

                        RadioButton(
                            selected = playlistTypeState == MusicListType.Local,
                            onClick = { playlistTypeState = MusicListType.Local },
                        )
                        Text(
                            text = UiText.StringResource(R.string.option_only_local).asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                )
                TextButton(
                    onClick = { playlistTypeState = MusicListType.Asset },
                    content = {

                        RadioButton(
                            selected = playlistTypeState == MusicListType.Asset,
                            onClick = { playlistTypeState = MusicListType.Asset },
                        )
                        Text(
                            text = UiText.StringResource(R.string.option_only_asset).asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                )
                TextButton(
                    onClick = { playlistTypeState = MusicListType.Streaming },
                    content = {
                        RadioButton(
                            selected = playlistTypeState == MusicListType.Streaming,
                            onClick = { playlistTypeState = MusicListType.Streaming },
                        )
                        Text(
                            text = UiText.StringResource(R.string.option_only_streaming).asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                )
                TextButton(
                    onClick = { playlistTypeState = MusicListType.All },
                    content = {
                        RadioButton(
                            selected = playlistTypeState == MusicListType.All,
                            onClick = { playlistTypeState = MusicListType.All },
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
    ToyPlayerTheme {
        MusicSongOptionDialog(
            musicListType = MusicListType.All,
            openDialog = true
        )
    }
}