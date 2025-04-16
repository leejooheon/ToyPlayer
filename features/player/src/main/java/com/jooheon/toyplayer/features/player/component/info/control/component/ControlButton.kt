package com.jooheon.toyplayer.features.player.component.info.control.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
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
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
            contentDescription = stringResource(Strings.action_previous),
            tint = Color.White,
            modifier = Modifier.size(32.dp).bounceClick { onPreviousClick.invoke() }
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier,
        ) {
            if(isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    painter = if(isPlaying) painterResource(Drawables.ic_pause)
                    else painterResource(Drawables.ic_play),
                    contentDescription = stringResource(Strings.action_play_pause),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(64.dp).bounceClick { onPlayPauseClick.invoke() }
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            contentDescription = stringResource(Strings.action_next),
            tint = Color.White,
            modifier = Modifier.size(32.dp).bounceClick { onNextClick.invoke() }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewControlButton() {
    ToyPlayerTheme {
        ControlButton(
            isPlaying = false,
            isLoading = false,
            onPlayPauseClick = {},
            onNextClick = {},
            onPreviousClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}