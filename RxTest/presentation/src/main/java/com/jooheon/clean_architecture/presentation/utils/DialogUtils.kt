package com.jooheon.clean_architecture.presentation.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.view.custom.CommonDialog
import kotlinx.coroutines.*

fun showToastMessage(context: Context, message: String?) {
    Toast.makeText(context, message ?: context.resources.getString(R.string.some_error), Toast.LENGTH_SHORT)
        .show()
}

@Composable
fun HandleApiFailure(response: Resource.Failure) {
    val openDialog = remember { mutableStateOf(true) }

    CommonDialog(
        openDialog = openDialog,
        title = "Api Failure",
        content = "message: ${ response.message}\nstatus: ${response.failureStatus}\ncode: ${response.code}",
        onConfirmButtonClicked = {
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
        CircularProgressIndicator(modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
    }
}