package com.jooheon.clean_architecture.features.musicplayer.presentation.common.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.features.common.compose.components.outlinedTextFieldColor
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R

@Composable
fun MusicPlaylistDialog(
    openDialog: Boolean,
    title: String,
    name: String,
    onConfirmButtonClicked: (String) -> Unit,
    onDismiss: (() -> Unit),
) {
    if(!openDialog) { return }

    var playlistName by remember { mutableStateOf(name) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        onDismissRequest = { onDismiss() },
        text = {
            PlaylistDialogContent(
                title = title,
                playlistName = playlistName,
                onTextChanged = { playlistName = it }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onConfirmButtonClicked(playlistName)
                }
            ) {
                Text(
                    text = UiText.StringResource(R.string.ok).asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(
                    text = UiText.StringResource(R.string.cancel).asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistDialogContent(
    title: String,
    playlistName: String,
    onTextChanged: (String) -> Unit,
) {
    val maxCharacterSize = 10

    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = playlistName,
            onValueChange = {
                if(it.length <= maxCharacterSize) {
                    onTextChanged(it)
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
            label = {
                Text(
                    text = UiText.StringResource(R.string.dialog_new_playlist_label).asString(),
                    color = MaterialTheme.colorScheme.onTertiary
                )
            },
            placeholder = {
                Text(
                    text = UiText.StringResource(R.string.dialog_new_playlist_placeholder).asString(),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                )
            },
            colors = outlinedTextFieldColor(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )
    }
}

@Preview
@Composable
private fun PlaylistDialogContentPreview() {
    PreviewTheme(true) {
        MusicPlaylistDialog(
            openDialog = true,
            title = UiText.StringResource(R.string.dialog_new_playlist).asString(),
            name = "",
            onDismiss = {},
            onConfirmButtonClicked = {}
        )
    }
}