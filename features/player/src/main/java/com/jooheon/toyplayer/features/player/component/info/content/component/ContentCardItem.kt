package com.jooheon.toyplayer.features.player.component.info.content.component

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.common.compose.components.CustomGlideImage
import com.jooheon.toyplayer.features.common.extension.toDp
import com.jooheon.toyplayer.features.player.model.PlayerUiState

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun ContentCardItem(
    title: String,
    imageUrl: String,
    isPlaying: Boolean,
    isSelectedItem: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = UiText.StringResource(Strings.placeholder_long).asString(),
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        constraints = Constraints.fixedWidth(1)
    )
    val textHeight = textLayoutResult.size.height

    Column(
        modifier = modifier.bounceClick { onClick.invoke() }
    ) {

        // when dp changed, change cardTopPreviewHeight together
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 0.63f)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Box(modifier = modifier) {
                CustomGlideImage(
                    url = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if(isSelectedItem) {
                val composition by rememberLottieComposition(
                    spec = LottieCompositionSpec.Asset("lottie_visualizer.json"),
                )

                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    isPlaying = isPlaying,
                    reverseOnRepeat = true,
                    clipSpec = LottieClipSpec.Frame(min = 4, max = 21),
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.5f
                            )
                        )
                )
                LottieAnimation(
                    composition = composition,
                    progress = { if (isPlaying) progress else 0f },
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                )
            }
        }

        // when dp changed, change cardBottomPreviewHeight together
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSecondary,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .height(textHeight.toDp())
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = Color.GRAY.toLong()
)
@Composable
private fun PreviewContentCardItem() {
    val uiState = PlayerUiState.preview
    val song = uiState.musicState.currentPlayingMusic
    ToyPlayerTheme {
        ContentCardItem(
            title = song.title,
            imageUrl = song.imageUrl,
            isPlaying = true,
            isSelectedItem = true,
            onClick = {},
            modifier = Modifier,
        )
    }
}