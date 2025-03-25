package com.jooheon.toyplayer.features.player.component.info.control.component

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FeaturedPlayList
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.ext.fadingEdge
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.commonui.components.CustomGlideImage
import timber.log.Timber

@Composable
internal fun ControlTopInfo(
    title: String,
    imageUrl: String,
    isPlaying: Boolean,
    onLibraryClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Timber.d("ControlTopInfo: $title")

    var titleAreaSize by remember { mutableStateOf(IntSize(0, 0)) }
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        constraints = Constraints.fixedWidth(titleAreaSize.width)
    )

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.height(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(52.dp)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    ),
            ) {
                CustomGlideImage(
                    url = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .onGloballyPositioned { titleAreaSize = it.size }
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = androidx.compose.ui.graphics.Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .then(
                            if(textLayoutResult.hasVisualOverflow) Modifier.fadingEdge(16.dp)
                            else Modifier
                        )
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            spacing = MarqueeSpacing(16.dp),
                        )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = onLibraryClick,
                modifier = Modifier
                    .size(36.dp)
                    .bounceClick { onLibraryClick.invoke() },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Album,
                    contentDescription = stringResource(Strings.title_playlist),
                    tint = androidx.compose.ui.graphics.Color.White,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onPlaylistClick,
                modifier = Modifier
                    .size(36.dp)
                    .bounceClick { onPlaylistClick.invoke() },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.FeaturedPlayList,
                    contentDescription = stringResource(Strings.title_playlist),
                    tint = androidx.compose.ui.graphics.Color.White,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSettingClick,
                modifier = Modifier
                    .size(36.dp)
                    .bounceClick { onSettingClick.invoke() },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(Strings.title_settings),
                    tint = androidx.compose.ui.graphics.Color.White,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = android.graphics.Color.GRAY.toLong()
)
@Composable
private fun PreviewControlTopInfoSection() {
    ToyPlayerTheme {
        ControlTopInfo(
            isPlaying = true,
            title = Song.preview.title,
            imageUrl = "",
            onLibraryClick = {},
            onPlaylistClick = {},
            onSettingClick = {},
        )
    }
}