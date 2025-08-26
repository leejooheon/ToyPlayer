package com.jooheon.toyplayer.features.player.component.info.content

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.commonui.components.CustomGlideImage
import com.jooheon.toyplayer.features.commonui.ext.toDp
import com.jooheon.toyplayer.features.player.common.contentWidth
import com.jooheon.toyplayer.features.player.model.PlayerUiState

@Composable
internal fun ContentCardItem(
    title: String,
    imageUrl: String,
    isPlaying: Boolean,
    isSelectedItem: Boolean,
    isFavorite: Boolean,
    showFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current

    Column(
        modifier = modifier
            .bounceClick { onClick.invoke() }
    ) {
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
            CustomGlideImage(
                url = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
            )

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
                val dynamicProperties = rememberLottieDynamicProperties(
                    LottieDynamicProperty(
                        property = LottieProperty.COLOR,
                        value = androidx.compose.ui.graphics.Color.White.toArgb(),
                        keyPath = KeyPath("**", "Ð\u0097Ð°Ð»Ð¸Ð²ÐºÐ° 1")
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                )
                LottieAnimation(
                    composition = composition,
                    progress = { if (isPlaying) progress else 0f },
                    dynamicProperties = dynamicProperties,
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                )
            }
            if(showFavorite) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = UiText.StringResource(Strings.action_toggle_favorite).asString(),
                        modifier = Modifier.size(16.dp),
                        tint = androidx.compose.ui.graphics.Color.Red.copy(alpha = if (isFavorite) 1f else 0.5f)
                    )
                }
            }
        }

        // when dp changed, change cardBottomPreviewHeight together
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = androidx.compose.ui.graphics.Color.White,
            ),
            maxLines = if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 1 else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    height = textHeight(
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 1 else 2,
                    )
                )
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
            isFavorite = false,
            showFavorite = true,
            onClick = {},
            onFavoriteClick = {},
            modifier = Modifier.width(contentWidth()),
        )
    }
}