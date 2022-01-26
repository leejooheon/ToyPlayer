package com.jooheon.clean_architecture.presentation.view.custom

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.jooheon.clean_architecture.presentation.theme.CustomTheme


@Composable
fun CommonDialog(
    openDialog: MutableState<Boolean>,
    title: String, content: String,
    onConfirmButtonClicked: () -> Unit,
    onDismissButtonClicked: () -> Unit) {

    if(!openDialog.value) { return }

    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        title = { Text(text = title)},
        text = { Text(text = content)},

        confirmButton = {
            TextButton(onClick = {
                openDialog.value = false
                onConfirmButtonClicked()
            }) {
                Text("확인")

            }
        },
        dismissButton = {
            TextButton(onClick = {
                openDialog.value = false
                onDismissButtonClicked()
            }) {
                Text("취소")
            }
        }
    )
}

@Composable
fun CommonDialog(
    openDialog: MutableState<Boolean>,
    title: String, content: String,
    onConfirmButtonClicked: (() -> Unit)? = null) {

    if(!openDialog.value) { return }

    AlertDialog(
        backgroundColor = CustomTheme.colors.uiBackground,
        onDismissRequest = { openDialog.value = false },
        title = { Text(text = title)},
        text = { Text(text = content)},

        confirmButton = {
            TextButton(onClick = {
                openDialog.value = false
                onConfirmButtonClicked?.let { it() }
            }) {
                Text("확인")
            }
        }
    )
}