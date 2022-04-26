package com.jooheon.clean_architecture.presentation.view.custom

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.clean_architecture.presentation.theme.CustomTheme
import com.jooheon.clean_architecture.presentation.theme.ProvideCustomColors
import com.jooheon.clean_architecture.presentation.view.temp.PreviewPallete


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
    content: String,
    onDismiss: (() -> Unit)? = null,
    onConfirmButtonClicked: (() -> Unit)? = null) {

    if(!openDialog.value) { return }

    AlertDialog(
        backgroundColor = CustomTheme.colors.uiFloated,
        onDismissRequest = {
            onDismiss?.let { it() }
        },
        text = {
            Text(
                text = content,
                color = CustomTheme.colors.textHelp
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss?.let { it() }
                onConfirmButtonClicked?.let { it() }
            }) {
                Text(
                    text = "확인",
                    color = CustomTheme.colors.textSecondary
                )
            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun CommonDialogPreview() {
    val openDialog = remember { mutableStateOf(true) }
    // https://issuetracker.google.com/issues/186502047
    // 현재 AlertDialog preview가 안되는 현상이있다고함.
    // ChipMunk Canary5 버전부터 수정됨.

    ProvideCustomColors(PreviewPallete) {
//        CommonDialog()
    }
}