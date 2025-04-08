package com.jooheon.toyplayer.features.player.component

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.audio.VisualizerData
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.features.commonui.components.OutlinedText
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.player.component.legacy.ExoVisualizer

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun LogoSection(
    musicState: MusicState,
    visualizerData: VisualizerData,
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
            value = Color.White.toArgb(),
            keyPath = KeyPath("**", "Fill 1")
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1.6f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxSize(0.7f),
            ) {
                val (text, lottie, loading) = createRefs()

                OutlinedText(
                    text = song.displayName,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                    ),
                    fillColor = Color.White,
                    outlineColor = Color.Black,
                    modifier = Modifier
                        .constrainAs(text) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

                AndroidView(
                    factory = {
                        ExoVisualizer(it).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = {
                        if(visualizerData == VisualizerData.default) return@AndroidView
                        it.bandView.onFFT(
                            fft = visualizerData.fft.toFloatArray(),
                            sampleRate = visualizerData.sampleRateHz
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .fillMaxHeight(0.1f)
                        .constrainAs(lottie) {
                            top.linkTo(text.bottom)
                            start.linkTo(text.start)
                            end.linkTo(text.end)
                        },
                )
                Spacer(modifier = Modifier.height(20.dp))

                if(isLoading || LocalInspectionMode.current) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(y = 32.dp)
                            .constrainAs(loading) {
                                top.linkTo(text.bottom)
                                start.linkTo(text.start)
                                end.linkTo(text.end)
                            },
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 640,
    heightDp = 320,
)
@Composable
private fun PreviewLogoSection() {
    ToyPlayerTheme {
        LogoSection(
            musicState = MusicState.preview.copy(
                currentPlayingMusic = Song.preview.copy(
//                    displayName = "123",
                    path = RadioData.default.serialize(),
                    isFavorite = false,
                )
            ),
            visualizerData = VisualizerData.default
        )
    }
}