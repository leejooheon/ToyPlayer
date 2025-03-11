package com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.core.resources.UiText

@Composable
fun MediaDetailHeader(
    count: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
        )
        Text(
            text = UiText.StringResource(R.string.song_list).asString(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = UiText.StringResource(R.string.n_song, count).asString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.7f
            )
        )
    }
    Divider(
        color = MaterialTheme.colorScheme.onBackground.copy(
            alpha = 0.7f
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    )
}

@Preview
@Composable
private fun MediaDetailHeaderPreview() {
    ToyPlayerTheme {
        MediaDetailHeader(100)
    }
}