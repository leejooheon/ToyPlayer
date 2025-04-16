package com.jooheon.toyplayer.features.player.component

import android.content.res.Configuration
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
    val song = musicState.currentPlayingMusic
    val (visualizerWidth, visualizerHeight) = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 0.3f to 0.1f
        else -> 0.35f to 0.1f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(!song.useCache) {
                OutlinedText(
                    text = song.displayName,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                    ),
                    fillColor = Color.White,
                    outlineColor = Color.Black,
                    modifier = Modifier
                )
            }
            Box(
                contentAlignment = Alignment.Center,
            ) {
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
                        .fillMaxWidth(visualizerWidth)
                        .fillMaxHeight(visualizerHeight)
                )

                if(isLoading || LocalInspectionMode.current) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier
                            .wrapContentSize()
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
)
@Composable
private fun PreviewLogoSection() {
    ToyPlayerTheme {
        LogoSection(
            musicState = MusicState.preview.copy(
                currentPlayingMusic = Song.preview.copy(
                    path = RadioData.default.serialize(),
                    isFavorite = false,
                )
            ),
            visualizerData = VisualizerData.default
        )
    }
}