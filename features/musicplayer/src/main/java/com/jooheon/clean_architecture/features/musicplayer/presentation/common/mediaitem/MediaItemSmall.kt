package com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.toyproject.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.toyproject.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenu
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaItemSmall(
    title: String,
    imageUrl: String,
    subTitle: String,
    showContextualMenu: Boolean,
    onItemClick: () -> Unit,
    onDropDownMenuClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onItemClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoilImage(
                url = imageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = title,
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .width(48.dp)
                    .height(48.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small)
            )

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(0.65f)
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

            if(!showContextualMenu) {
                Spacer(modifier = Modifier.width(16.dp))
                return@Row
            }

            var dropDownMenuExpanded by remember { mutableStateOf(false) }

            IconButton(
                onClick = { dropDownMenuExpanded = true },
                modifier = Modifier.weight(0.15f).padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "more" // TODO
                )
                MusicDropDownMenu(
                    expanded = dropDownMenuExpanded,
                    dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.mediaItems),
                    onDismissRequest = { dropDownMenuExpanded = false },
                    onClick = onDropDownMenuClick
                )
            }
        }
    }
}

@Preview
@Composable
private fun MediaItemSmallPreview() {
    PreviewTheme(false) {
        MediaItemSmall(
            imageUrl = "",
            title = UiText.StringResource(R.string.placeholder_long).asString(),
            subTitle = UiText.StringResource(R.string.placeholder_medium).asString(),
            showContextualMenu = true,
            onItemClick = {},
            onDropDownMenuClick = {},
            modifier = Modifier.width(400.dp),
        )
    }
}