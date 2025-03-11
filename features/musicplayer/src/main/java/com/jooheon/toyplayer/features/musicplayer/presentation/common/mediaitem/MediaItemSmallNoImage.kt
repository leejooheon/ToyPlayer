package com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown.MusicDropDownMenu
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.toyplayer.core.resources.UiText

@Composable
fun MediaItemSmallNoImage(
    trackNumber: Int,
    title: String,
    subTitle: String,
    duration: String,
    dropDownMenuState: MusicDropDownMenuState,
    onItemClick: () -> Unit,
    onDropDownMenuClick: (index: Int) -> Unit,
) {
    Card(
        onClick = { onItemClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier.padding(
            top = 12.dp,
            start = 12.dp,
            end = 12.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = trackNumber.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(0.1f),
                textAlign = TextAlign.Center
            )

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(0.65f).padding(4.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = subTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.7f
                    ),
                )
            }

            Text(
                text = duration,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                ),
                modifier = Modifier.weight(0.15f),
                textAlign = TextAlign.Center
            )

            var dropDownMenuExpanded by remember { mutableStateOf(false) }

            IconButton(
                onClick = { dropDownMenuExpanded = true },
                modifier = Modifier.weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "more" // TODO
                )
                MusicDropDownMenu(
                    expanded = dropDownMenuExpanded,
                    dropDownMenuState = MusicDropDownMenuState(dropDownMenuState.items),
                    onDismissRequest = { dropDownMenuExpanded = false },
                    onClick = onDropDownMenuClick
                )
            }
        }
    }
}
@Preview
@Composable
private fun MediaItemSmallWithoutImagePreview() {
    ToyPlayerTheme {
        MediaItemSmallNoImage(
            trackNumber = 1,
            title = UiText.StringResource(R.string.placeholder_long).asString(),
            subTitle = UiText.StringResource(R.string.placeholder_medium).asString(),
            duration = "00:12",
            dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.mediaItems),
            onItemClick = {},
            onDropDownMenuClick = {},
        )
    }
}
