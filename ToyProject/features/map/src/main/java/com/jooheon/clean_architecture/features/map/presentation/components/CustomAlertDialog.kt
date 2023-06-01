package com.jooheon.clean_architecture.features.map.presentation.components

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.map.R


@Composable
fun ShowAlertDialog(
    openDialog: MutableState<Boolean>,
    content: UiText,
    viewModel: BaseViewModel,
    onOkButtonClicked: (() -> Unit)?,
) {
    MyAlertDialog(
        openDialog,
        content = content.asString(),
        onDismiss = {
            viewModel.dismissAlertDialog()
            Log.d("BaseFragment", "onDismiss")
        },
        onConfirmButtonClicked = {
            openDialog.value = false
            Log.d("BaseFragment", "onConfirmButtonClicked")
            onOkButtonClicked?.invoke()
        }
    )
}
@Composable
fun ShowAlertDialog(
    openDialog: MutableState<Boolean>,
    content: UiText,
    onOkButtonClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
    MyAlertDialog(
        openDialog = openDialog,
        content = content.asString(),
        onDismiss = {
            openDialog.value = false
            onDismiss()
        },
        onConfirmButtonClicked = {
            openDialog.value = false
            onOkButtonClicked()
        }
    )
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
                    text = UiText.StringResource(R.string.ok).asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    )
}