package com.jooheon.clean_architecture.presentation.utils

import android.annotation.SuppressLint
import android.content.Context

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.common.AlertDialogResource
import com.jooheon.clean_architecture.presentation.theme.CustomTheme
import com.jooheon.clean_architecture.presentation.view.custom.CommonDialog

private const val TAG = "DialogUtils"

// FIXME: Compose function에 Base로 자동적용할수없을까?
@SuppressLint("UnrememberedMutableState")
@Composable
fun ObserveAlertDialogState(viewModel: BaseViewModel) {
    val resource = viewModel.alertDialogState.collectAsState(AlertDialogResource("")).value

    if(!resource.content.isEmpty()) {
        Log.d(TAG, "ObserveAlertDialogState execute")
        ShowAlertDialog(
            openDialog = mutableStateOf(true),
            content = resource.content
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
fun ShowAlertDialog(openDialog: MutableState<Boolean>, content: String) {
    CommonDialog(
        openDialog,
        content = content,
        onConfirmButtonClicked = {
            openDialog.value = false
            Log.d("BaseFragment", "onConfirmButtonClicked")
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
            color = CustomTheme.colors.textPrimary
        )
    }
}