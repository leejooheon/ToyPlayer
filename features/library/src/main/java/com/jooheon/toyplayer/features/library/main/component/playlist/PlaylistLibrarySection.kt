package com.jooheon.toyplayer.features.library.main.component.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Stream
import androidx.compose.material.icons.outlined.WebAsset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.features.common.extension.toDp

@Composable
internal fun PlaylistLibrarySection(
    models: List<Playlist>,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
    onMoreClick: () -> Unit,
) {
    val icons = listOf(
        Icons.Outlined.LocalActivity,
        Icons.Outlined.Stream,
        Icons.Outlined.WebAsset,
    )

    BoxWithConstraints(modifier = modifier) {
        val childItemSize = 3
        val maxWidth = constraints.maxWidth
        val childItemContentSize = (maxWidth - 48 * 2 - 16 * 2) / childItemSize
        val chunkedItems = models.chunked(childItemSize).take(2)

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(chunkedItems.size) { index ->
                val items = chunkedItems[index]

                Row(
                    modifier = Modifier
                        .width(maxWidth.toDp())
                        .height(childItemContentSize.toDp()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items.forEachIndexed { playlistIndex, playlist ->
                        if(index + 1 == chunkedItems.size && playlistIndex + 1 == items.size) { // last item
                            PlaylistLibraryItem(
                                name = UiText.StringResource(Strings.action_see_more).asString(),
                                icon = icons[playlistIndex],
                                onClick = onMoreClick
                            )
                        } else {
                            PlaylistLibraryItem(
                                name = playlist.name,
                                icon = icons[playlistIndex],
                                onClick = { onClick.invoke(playlist.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
@Preview
private fun PreviewPlaylistLibrarySection() {
    ToyPlayerTheme {
        PlaylistLibrarySection(
            models = listOf(Playlist.preview),
            onClick = {},
            onMoreClick = {},
        )
    }
}