package com.jooheon.toyplayer.features.player.component.info.control.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Drawables
import com.jooheon.toyplayer.core.resources.Strings

@Composable
internal fun ControlButton(
    isLoading: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        if(isLoading) {

            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                painter = if(isPlaying) painterResource(Drawables.ic_pause)
                else painterResource(Drawables.ic_play),
                contentDescription = stringResource(Strings.action_play_pause),
                tint = Color.Unspecified,
                modifier = Modifier.bounceClick { onClick.invoke() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewControlButton() {
    ToyPlayerTheme {
        ControlButton(
            isPlaying = false,
            isLoading = false,
            onClick = {},
            modifier = Modifier.wrapContentSize(),
        )
    }
}