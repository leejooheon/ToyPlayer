package com.jooheon.toyplayer.features.playlist.details.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Drawables
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.common.R
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogButton

@Composable
internal fun PermissionRequestItem(
    modifier: Modifier = Modifier,
    launchPermissionRequest: () -> Unit,
) {
    val description = UiText.StringResource(R.string.description_permission_read_storage).asString()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            alignment = Alignment.BottomCenter,
            painter = painterResource(id = Drawables.img_storage_permission),
            contentDescription = description
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = description,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.TopCenter
        ) {
            DialogButton(
                text = UiText.StringResource(R.string.action_request_permission).asString(),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = launchPermissionRequest,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewPermission() {
    ToyPlayerTheme {
        PermissionRequestItem(
            launchPermissionRequest = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}