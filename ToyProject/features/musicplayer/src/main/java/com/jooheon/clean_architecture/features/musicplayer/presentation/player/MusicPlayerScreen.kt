package com.jooheon.clean_architecture.features.musicplayer.presentation.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.features.common.compose.extensions.scrollEnabled
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaColumn
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MusicOptionDialog
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events.MediaDropDownMenuEvent
import kotlinx.coroutines.launch
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicPlayerScreen(
    musicPlayerScreenState: MusicPlayerScreenState,

    onEvent: (MusicPlayerScreenEvent) -> Unit,
    onDropDownMenuEvent: (MediaDropDownMenuEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val musicState = musicPlayerScreenState.musicState

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    var openDialog by remember { mutableStateOf(false) }
    var viewType by rememberSaveable { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    MediaSwipeableLayout(
        musicPlayerScreenState = musicPlayerScreenState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onEvent,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()

            ) {
                TextButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { openDialog = true },
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = UiText.StringResource(R.string.option_see_more).asString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            )
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = UiText.StringResource(R.string.option_see_more).asString()
                            )
                        }
                    }
                )

                IconButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { viewType = !viewType },
                    content = {
                        Icon(
                            imageVector = if(viewType) Icons.Filled.List else Icons.Outlined.Image,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = UiText.StringResource(R.string.option_see_more).asString()
                        )
                    }
                )
            }
            MediaColumn(
                playlist = musicState.playlist,
                listState = listState,
                viewType = viewType,
                onItemClick = {
                    if (swipeableState.currentValue == 0) {
                        if (musicState.currentPlayingMusic != it) {
                            onEvent(MusicPlayerScreenEvent.OnPlayClick(it))
                        }
                        scope.launch {
                            swipeableState.animateTo(1)
                        }
                    }
                },
                onDropDownMenuClick = { index, song ->
                    val event = MediaDropDownMenuEvent.fromIndex(index, song)
                    onDropDownMenuEvent(event)
                },
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
                    .scrollEnabled(motionProgress == 0f),
            )
        }

    )
    MusicOptionDialog(
        playlistType = musicPlayerScreenState.musicState.playlistType,
        openDialog = openDialog,
        onDismiss = {
            openDialog = false
        },
        onOkButtonClicked = {
            openDialog = false
            onEvent(MusicPlayerScreenEvent.OnPlaylistTypeChanged(it))
        }
    )
}
@Preview
@Composable
private fun MusicScreenPreview() {
    PreviewTheme(false) {
        MusicPlayerScreen(
            musicPlayerScreenState = MusicPlayerScreenState.default,
            onEvent = { _, -> },
            onDropDownMenuEvent = { }
        )
    }
}