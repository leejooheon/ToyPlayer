package com.jooheon.toyplayer.features.library.main.component.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.features.common.compose.components.CoilImage
import com.jooheon.toyplayer.features.common.compose.components.OutlinedText

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ArtistLibraryItem(
    artist: Artist,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f, true)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick.invoke() }
    ) {
        val albumOrNull = artist.albums.firstOrNull()
        CoilImage(
            url = albumOrNull?.imageUrl.defaultEmpty(),
            contentDescription = albumOrNull?.name.defaultEmpty(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            OutlinedText(
                text = artist.name,
                outlineColor = MaterialTheme.colorScheme.onPrimary,
                fillColor = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium.copy(
                    shadow = Shadow(Color.Black, Offset(1f,1f), 1f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxSize(0.85f)
                    .wrapContentHeight(Alignment.Bottom),
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}