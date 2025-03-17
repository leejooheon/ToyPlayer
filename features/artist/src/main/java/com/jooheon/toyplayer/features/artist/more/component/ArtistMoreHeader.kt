package com.jooheon.toyplayer.features.artist.more.component

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
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.common.compose.components.dropdown.MusicDropDownMenu
import com.jooheon.toyplayer.features.common.compose.components.dropdown.MusicDropDownMenuState

@Composable
internal fun ArtistHeader(
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
                        text = UiText.StringResource(Strings.option_sort_by).asString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = UiText.StringResource(Strings.option_sort_by).asString()
                    )
                }
                MusicDropDownMenu(
                    expanded = dropDownMenuExpanded,
                    dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.artistSortItems),
                    onDismissRequest = { dropDownMenuExpanded = false },
                    onClick = onDropDownMenuClick
                )
            }
        )
    }
}

@Preview
@Composable
private fun ArtistHeaderPreview() {
    ToyPlayerTheme {
        ArtistHeader(
            onDropDownMenuClick = {},
            modifier = Modifier
                .width(300.dp)
                .background(MaterialTheme.colorScheme.background)
        )
    }
}