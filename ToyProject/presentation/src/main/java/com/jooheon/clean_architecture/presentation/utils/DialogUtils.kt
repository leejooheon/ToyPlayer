package com.jooheon.clean_architecture.presentation.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.presentation.view.custom.MyAlertDialog

private const val TAG = "DialogUtils"

// FIXME: Compose function에 Base로 자동적용할수없을까?
@Composable
fun ObserveAlertDialogState(
    viewModel: BaseViewModel,
    onOkButtonClicked: (() -> Unit)? = null,
) {
    val state = viewModel.alertDialogFlow.collectAsState(initial = null).value
    state?.let {
        ShowAlertDialog(
            openDialog = mutableStateOf(true),
            content = it.content,
            viewModel = viewModel,
            onOkButtonClicked
        )
    }
}

// FIXME: Compose function에 Base로 자동적용할수없을까?
@Composable
fun ObserveLoadingState(viewModel: BaseViewModel) {
    val isShow = viewModel.loadingState.collectAsState(false).value

    if(isShow) {
        ShowLoading()
    }
}

fun showToastMessage(context: Context, message:String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun ShowAlertDialog(
    openDialog: MutableState<Boolean>,
    content: UiText,
    viewModel: BaseViewModel,
    onOkButtonClicked: (() -> Unit)?
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
fun ShowLoading() {
    // Column is a composable that places its children in a vertical sequence. You
    // can think of it similar to a LinearLayout with the vertical orientation.
    // In addition we also pass a few modifiers to it.

    // You can think of Modifiers as implementations of the decorators pattern that are
    // used to modify the composable that its applied to. In this example, we configure the
    // Column composable to occupy the entire available width and height using
    // Modifier.fillMaxSize().
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // A pre-defined composable that's capable of rendering a circular progress indicator. It
        // honors the Material Design specification.
        CircularProgressIndicator(
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary
        )
    }
}