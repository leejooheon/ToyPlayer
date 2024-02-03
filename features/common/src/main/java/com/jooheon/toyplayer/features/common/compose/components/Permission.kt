package com.jooheon.toyplayer.features.common.compose.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.jooheon.toyplayer.features.common.R
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.essential.base.UiText


@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun PermissionRequestItem(
    resId: Int,
    description: UiText,
    isPermissionRequestBlocked: Boolean,
    noinline launchPermissionRequest: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(),
            alignment = Alignment.BottomCenter,
            painter = painterResource(id = resId),
            contentDescription = "Image"
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text =  if(isPermissionRequestBlocked) {
                UiText.StringResource(R.string.description_permission_blocked).asString()
            } else description.asString(),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Button(
                onClick = launchPermissionRequest,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                val style = MaterialTheme.typography.titleMedium
                Text(
                    text = if(isPermissionRequestBlocked) UiText.StringResource(R.string.action_navigate_app_settings).asString()
                    else UiText.StringResource(R.string.action_request_permission).asString(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = style.fontSize,
                    fontStyle = style.fontStyle
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewPermission() {
    PreviewTheme(false) {
        PermissionRequestItem(
            resId = R.drawable.folder_search_base_256_blu_glass,
            description = UiText.StringResource(R.string.description_permission_read_storage),
            isPermissionRequestBlocked = false,
            launchPermissionRequest = {}
        )
    }
}

@Composable
fun isPermissionGranted(
    permissionString: String,
    context: Context = LocalContext.current
): Boolean {
    return ContextCompat.checkSelfPermission(context, permissionString) == PackageManager.PERMISSION_GRANTED
}

fun savePermissionRequested(activity: Activity, permissionString: String) {
    val sharedPref = activity.getSharedPreferences("perm", Context.MODE_PRIVATE)
    sharedPref.edit { putBoolean(permissionString, true) }
}

fun isPermissionRequestBlocked(activity: Activity, permission: String): Boolean {
    val sharedPref = activity.getSharedPreferences("perm", Context.MODE_PRIVATE)
    return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
            && !activity.shouldShowRequestPermissionRationale(permission)
            && sharedPref.getBoolean(permission, false)
}


fun appDetailSettings(context: Context): Intent {
    return Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.parse("package:${context.packageName}")
    }
}