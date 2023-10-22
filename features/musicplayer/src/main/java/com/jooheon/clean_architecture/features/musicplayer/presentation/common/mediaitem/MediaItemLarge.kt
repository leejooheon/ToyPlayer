package com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.toyproject.features.musicplayer.R
import com.jooheon.clean_architecture.toyproject.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenu
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState

@Composable
fun MediaItemLarge(
    title: String,
    imageUrl: String,
    subTitle: String,
    onItemClick: () -> Unit,
    onDropDownMenuClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { onItemClick() },
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onItemClick() },
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            CoilImage(
                url = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                placeholderRes = R.drawable.ic_placeholder,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                CoilImage(
                    url = imageUrl,
                    contentDescription = subTitle,
                    modifier = Modifier
                        .weight(0.2f)
                        .aspectRatio(1f)
                        .clip(CircleShape),
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(0.7f)
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.weight(0.1f))
                IconButton(
                    onClick = { dropDownMenuExpanded = true },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "more" // TODO
                    )
                    MusicDropDownMenu(
                        expanded = dropDownMenuExpanded,
                        dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.mediaItems),
                        onDismissRequest = { dropDownMenuExpanded = false },
                        onClick = {
                            onDropDownMenuClick(it)
                            dropDownMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun MediaItemLargePreview() {
    PreviewTheme(false) {
        MediaItemLarge(
            title = UiText.StringResource(R.string.placeholder_long).asString(),
            subTitle = UiText.StringResource(R.string.placeholder_medium).asString(),
            imageUrl = "image",
            onItemClick = {},
            onDropDownMenuClick = {},
            modifier = Modifier.width(400.dp),
        )
    }
}