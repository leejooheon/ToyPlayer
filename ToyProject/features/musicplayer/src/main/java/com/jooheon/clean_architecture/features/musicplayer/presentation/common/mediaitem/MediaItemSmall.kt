package com.jooheon.clean_architecture.features.musicplayer.presentation.mediaitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.mediaitem.dropdown.MusicDropDownMenu
import com.jooheon.clean_architecture.features.musicplayer.presentation.mediaitem.dropdown.MusicDropDownMenuState

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
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.padding(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(72.dp)
        ) {
            CoilImage(
                url = imageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = title,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.2f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(0.7f)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )

                Text(
                    text = subTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            if(showContextualMenu) {
                var dropDownMenuExpanded by remember { mutableStateOf(false) }

                IconButton(
                    onClick = { dropDownMenuExpanded = true },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
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
}

@Preview
@Composable
private fun MediaItemSmallPreview() {
    PreviewTheme(false) {
        MediaItemSmall(
            imageUrl = "",
            title = UiText.StringResource(R.string.lorem).asString(),
            subTitle = UiText.StringResource(R.string.dessert).asString(),
            showContextualMenu = true,
            onItemClick = {},
            onDropDownMenuClick = {},
            modifier = Modifier.width(400.dp),
        )
    }
}