package com.jooheon.clean_architecture.presentation.view.custom

import android.content.Context
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme

fun DialogTest(context: Context) {
    MaterialAlertDialogBuilder(context)
        .setTitle("title")
        .setMessage("message - 123")
        .setPositiveButton("Ok") { a, b ->
            Log.d("asdasd", "asd")
        }
        .show()
}

@Composable
fun MyAlertDialog(
    openDialog: MutableState<Boolean>,
    content: String,
    onDismiss: (() -> Unit)? = null,
    onConfirmButtonClicked: (() -> Unit)? = null
) {
    if(!openDialog.value) { return }

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
                    text = "확인",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
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

    PreviewTheme {
//        CommonDialog()
    }
}