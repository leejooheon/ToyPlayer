package com.jooheon.toyplayer.features.common.compose.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import com.jooheon.toyplayer.features.essential.base.UiText
import com.jooheon.toyplayer.features.common.R

@Composable
fun ShowAlertDialog(
    content: UiText,
    onOkButtonClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
    var openDialog by remember { mutableStateOf(true) }

    MyAlertDialog(
        openDialog = openDialog,
        content = content.asString(),
        onDismiss = {
            openDialog = false
            onDismiss()
        },
        onConfirmButtonClicked = {
            openDialog = false
            onOkButtonClicked()
        }
    )
}
@Composable
fun ShowAlertDialog(
    content: AnnotatedString,
    onOkButtonClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
    var openDialog by remember { mutableStateOf(true) }

    MyAlertDialog(
        openDialog = openDialog,
        content = content,
        onDismiss = {
            openDialog = false
            onDismiss()
        },
        onConfirmButtonClicked = {
            openDialog = false
            onOkButtonClicked()
        }
    )
}


@Composable
private fun MyAlertDialog(
    openDialog: Boolean,
    content: AnnotatedString,
    onDismiss: (() -> Unit)? = null,
    onConfirmButtonClicked: (() -> Unit)? = null
) {
    if(!openDialog) { return }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.secondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        onDismissRequest = {
            onDismiss?.let { it() }
        },
        text = {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss?.let { it() }
                onConfirmButtonClicked?.let { it() }
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
@Composable
private fun MyAlertDialog(
    openDialog: Boolean,
    content: String,
    onDismiss: (() -> Unit)? = null,
    onConfirmButtonClicked: (() -> Unit)? = null
) {
    if(!openDialog) { return }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.secondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        onDismissRequest = {
            onDismiss?.let { it() }
        },
        text = {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss?.let { it() }
                onConfirmButtonClicked?.let { it() }
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