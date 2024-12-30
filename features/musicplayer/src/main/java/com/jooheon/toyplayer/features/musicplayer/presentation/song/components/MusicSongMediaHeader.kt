package com.jooheon.toyplayer.features.musicplayer.presentation.song.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.core.strings.UiText

@Composable
internal fun MusicSongMediaHeader(
    viewType: Boolean,
    onSeeMoreButtonClick: () -> Unit,
    onViewTypeClick: (Boolean) -> Unit,
    onPlayAllClick: () -> Unit,
    modifier: Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        TextButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onSeeMoreButtonClick,
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = UiText.StringResource(R.string.option_see_more).asString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = UiText.StringResource(R.string.option_see_more).asString()
                    )
                }
            }
        )


        IconButton(
            modifier = Modifier,
            onClick = { onPlayAllClick() },
            content = {
                Icon(
                    imageVector = if(viewType) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircle,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = UiText.StringResource(R.string.option_see_more).asString()
                )
            }
        )
        IconButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = { onViewTypeClick(!viewType) },
            content = {
                Icon(
                    imageVector = if(viewType) Icons.Filled.List else Icons.Outlined.Image,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = UiText.StringResource(R.string.option_see_more).asString()
                )
            }
        )
    }
}
@Preview
@Composable
private fun MusicSongMediaHeaderPreview() {
    ToyPlayerTheme {
        MusicSongMediaHeader(
            viewType = true,
            onSeeMoreButtonClick = {},
            onViewTypeClick = {},
            onPlayAllClick = {},
            modifier = Modifier.width(300.dp).background(MaterialTheme.colorScheme.background)
        )
    }
}