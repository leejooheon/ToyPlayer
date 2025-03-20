package com.jooheon.toyplayer.features.player.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.features.commonui.components.OutlinedText
import com.jooheon.toyplayer.features.musicservice.data.MusicState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun LogoSection(
    musicState: MusicState,
    modifier: Modifier = Modifier,
) {
    val isLoading = musicState.isLoading()
    val isPlaying = musicState.isPlaying()
    val song = musicState.currentPlayingMusic

    if(song.useCache) return

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset("lottie_visualizer_long.json"),
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        reverseOnRepeat = true,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = Color.White.toArgb(), // 원하는 색상 지정
            keyPath = KeyPath("**", "Fill 1")
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(3.2f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(0.7f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedText(
                        text = song.displayName,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                        fillColor = Color.White,
                        outlineColor = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        minLines = 2,
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                if(isPlaying) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        dynamicProperties = dynamicProperties,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(1.6f)
                            .weight(1f),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .wrapContentSize()
                        .alpha(if(isLoading) 1f else 0f),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun PreviewLogoSection() {
    ToyPlayerTheme {
        LogoSection(
            musicState = MusicState.preview.copy(
                currentPlayingMusic = Song.preview.copy(
                    path = RadioData.default.serialize()
                )
            )
        )
    }
}