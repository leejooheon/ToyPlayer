package com.jooheon.toyplayer.features.settings.presentation.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.features.settings.presentation.language.LanguageType


@Composable
internal fun LanguageItem(
    type: LanguageType,
    currentType: LanguageType,
    onCheckedClick: (LanguageType) -> Unit,
) {
    val isChecked = currentType == type

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if(isChecked) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(onClick = { onCheckedClick.invoke(type) })
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp)
        ) {
            Text(
                text = stringResource(type.stringResource()),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if(isChecked) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
                modifier = Modifier.padding(
                    vertical = 10.dp
                )
            )
            if(isChecked) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                )
            }
        }
    }
}