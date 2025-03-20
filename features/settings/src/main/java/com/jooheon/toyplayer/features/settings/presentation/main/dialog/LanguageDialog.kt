package com.jooheon.toyplayer.features.settings.presentation.main.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogButton
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogColumn
import com.jooheon.toyplayer.features.settings.presentation.language.LanguageType
import com.jooheon.toyplayer.features.settings.presentation.main.component.LanguageItem

@Composable
internal fun LanguageDialog(
    fraction: Float,
    currentLanguageType: LanguageType,
    onDismissRequest: () -> Unit,
    onSelected: (LanguageType) -> Unit,
) {
    var selectedLanguageType by remember { mutableStateOf(currentLanguageType) }

    DialogColumn(
        fraction = fraction,
        padding = 16.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = UiText.StringResource(Strings.dialog_language_selection_title).asString(),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = UiText.StringResource(Strings.dialog_language_selection_description).asString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        )

        LanguageType.entries.forEach {
            Spacer(modifier = Modifier.height(8.dp))
            LanguageItem(
                type = it,
                currentType = selectedLanguageType,
                onCheckedClick = { selectedLanguageType = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DialogButton(
                text = stringResource(Strings.ok),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = { onSelected.invoke(selectedLanguageType) }
            )

            Spacer(modifier = Modifier.width(12.dp))

            DialogButton(
                text = stringResource(Strings.cancel),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = onDismissRequest
            )
        }
    }
}


@Preview
@Composable
private fun PreviewLanguageDialog() {
    ToyPlayerTheme {
        LanguageDialog(
            fraction = 0.7f,
            currentLanguageType = LanguageType.default,
            onDismissRequest = {},
            onSelected = {},
        )
    }
}