package com.jooheon.toyplayer.features.playlist.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.features.common.compose.components.DialogButton
import com.jooheon.toyplayer.features.common.compose.components.DialogColumn
import com.jooheon.toyplayer.features.common.compose.components.outlinedTextFieldColor

@Composable
internal fun PlaylistDialog(
    state: Pair<Boolean, Playlist>,
    onOkButtonClicked: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val (openDialog, playlist) = state
    if(!openDialog) return

    var playlistName by remember { mutableStateOf(playlist.name) }
    val title = if(playlist.id == Playlist.default.id) {
        UiText.StringResource(Strings.dialog_new_playlist).asString()
    } else {
        UiText.StringResource(Strings.dialog_edit_playlist_name).asString()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    DialogColumn(
        fraction = 0.7f,
        padding = 16.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = playlistName,
            onValueChange = {
                playlistName = it
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
            label = {
                Text(
                    text = stringResource(Strings.dialog_new_playlist_label),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                )
            },
            placeholder = {
                Text(
                    text = stringResource(Strings.dialog_new_playlist_placeholder),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                )
            },
            colors = outlinedTextFieldColor(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DialogButton(
                text = stringResource(Strings.ok),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = {
                    onOkButtonClicked.invoke(playlistName)
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            DialogButton(
                text = stringResource(Strings.cancel),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = onDismissRequest
            )
        }
    }
}

@Preview
@Composable
private fun PlaylistDialogContentPreview() {
    ToyPlayerTheme {
        PlaylistDialog(
            state = true to Playlist.default,
            onOkButtonClicked = {},
            onDismissRequest = {},
        )
    }
}