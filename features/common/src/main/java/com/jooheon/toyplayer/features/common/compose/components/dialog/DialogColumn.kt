package com.jooheon.toyplayer.features.common.compose.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogColumn(
    modifier: Modifier = Modifier,
    fraction: Float,
    padding: Dp,
    cancelable: Boolean = true,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    BasicAlertDialog(
        modifier = modifier.fillMaxWidth(fraction),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = cancelable,
            dismissOnClickOutside = cancelable
        )
    ) {
        LazyColumn {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = Color.Transparent,
                    content = {
                        Column(
                            content = content,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceDim)
                                .padding(padding)
                                .fillMaxWidth(1f)
                                .wrapContentHeight()
                        )
                    }
                )
            }
        }
    }
}
