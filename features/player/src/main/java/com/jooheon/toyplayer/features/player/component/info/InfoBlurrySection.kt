package com.jooheon.toyplayer.features.player.component.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jooheon.toyplayer.core.designsystem.component.CustomGlideImage
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.player.model.PlayerUiState

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun InfoBlurrySection(
    imageUrl: String,
    contentDescription: String,
    currentPageIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(if(currentPageIndex == 0) 16.dp else 128.dp)
        ) {
            CustomGlideImage(
                model = imageUrl,
                contentDescription = contentDescription,
                transition = null,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                    )
                )
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun PreviewInfoBlurrySection() {
    val uiState = PlayerUiState.preview
    val song = uiState.musicState.currentPlayingMusic
    ToyPlayerTheme {
        InfoBlurrySection(
            imageUrl = song.imageUrl,
            contentDescription = song.title,
            currentPageIndex = 0
        )
    }
}
