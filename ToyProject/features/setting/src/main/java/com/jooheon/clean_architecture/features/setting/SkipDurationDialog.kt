package com.jooheon.clean_architecture.features.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jooheon.clean_architecture.domain.entity.music.SkipForwardBackward
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText


@Composable
internal fun SkipDurationDialog(
    currentState: SkipForwardBackward,
    onChanged: (SkipForwardBackward) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Card(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = true,
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onDismiss
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    SkipForwardBackward.values().forEach {
                        SkipForwardBackwardItem(
                            selected = currentState == it,
                            skipForwardBackward = it,
                            onClicked = onChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(
                            text = UiText.StringResource(R.string.close).asString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SkipForwardBackwardItem(
    selected: Boolean,
    skipForwardBackward: SkipForwardBackward,
    modifier: Modifier = Modifier,
    onClicked: (SkipForwardBackward) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.surface.copy(
                    alpha = 0.24f
                )
            )
            .clickable {
                onClicked(skipForwardBackward)
            }
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onClicked(skipForwardBackward)
            }
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = UiText.StringResource(
                resId = R.string.n_second,
                args = arrayOf(skipForwardBackward.toInteger())
            ).asString(),
        )
    }
}

@Composable
@Preview
private fun PreviewSkipForwardPopup() {
    PreviewTheme(false) {
        SkipDurationDialog(
            currentState = SkipForwardBackward.FIFTEEN_SECOND,
            onChanged = {},
            onDismiss = {},
        )
    }
}