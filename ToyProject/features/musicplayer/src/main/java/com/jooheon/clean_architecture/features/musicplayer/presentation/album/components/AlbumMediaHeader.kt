package com.jooheon.clean_architecture.features.musicplayer.presentation.album.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenu
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.toyproject.features.musicplayer.R

@Composable
internal fun AlbumMediaHeader(
    onDropDownMenuClick: (index: Int) -> Unit,
    modifier: Modifier,
) {
    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        TextButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = { dropDownMenuExpanded = true },
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = UiText.StringResource(R.string.option_sort_by).asString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = UiText.StringResource(R.string.option_sort_by).asString()
                    )
                }
                MusicDropDownMenu(
                    expanded = dropDownMenuExpanded,
                    dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.albumSortItems),
                    onDismissRequest = { dropDownMenuExpanded = false },
                    onClick = onDropDownMenuClick
                )
            }
        )
    }
}
@Preview
@Composable
private fun AlbumMediaHeaderPreview() {
    PreviewTheme(true) {
        AlbumMediaHeader(
            onDropDownMenuClick = {},
            modifier = Modifier
                .width(300.dp)
                .background(MaterialTheme.colorScheme.background)
        )
    }
}