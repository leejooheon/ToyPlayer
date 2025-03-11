package com.jooheon.toyplayer.features.library.main.component.artist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.features.common.extension.toDp

@Composable
internal fun ArtistLibrarySection(
    models: List<Artist>,
    onClick: (String) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when(models.size) {
        0 -> return // show no local song available
        1, 2, 3 -> Unit // maybe something else
    }

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
                    items.forEachIndexed { artistIndex, artist ->
                        if(index + 1 == chunkedItems.size && artistIndex + 1 == items.size) { // last item
                            ArtistLibraryItem(
                                artist = Artist.default.copy(
                                    name = UiText.StringResource(Strings.action_see_more).asString() + " ${models.size}"
                                ),
                                onClick = onMoreClick
                            )
                        } else ArtistLibraryItem(
                            artist = artist,
                            onClick = { onClick.invoke(artist.id) },
                        )
                    }
                }
            }
        }
    }
}


@Composable
@Preview
private fun PreviewArtistLibrarySection() {
    ToyPlayerTheme {
        ArtistLibrarySection(
            models = listOf(Artist.default, Artist.default, Artist.default, Artist.default, Artist.default, Artist.default),
            onClick = {},
            onMoreClick = {},
        )
    }
}