package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.essential.base.UiText
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache.components.CachedSongItem
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache.model.MusicCacheScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache.model.MusicCacheScreenState
import com.jooheon.toyplayer.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.toyplayer.features.musicplayer.R
import java.lang.Float
import kotlin.math.max

@Composable
fun MusicCacheScreen(
    navController: NavController,
    viewModel: MusicCacheScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val screenState by viewModel.musicCacheScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    viewModel.refreshChannel.observeWithLifecycle {
        viewModel.dispatch(MusicCacheScreenEvent.OnRefresh(context))
    }

    MusicCacheScreen(
        musicCacheScreenState = screenState,
        musicPlayerState = musicPlayerState,
        onMusicCacheScreenEvent = viewModel::dispatch,
        onMusicPlayerEvent = viewModel::onMusicPlayerEvent
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MusicCacheScreen(
    musicCacheScreenState: MusicCacheScreenState,
    musicPlayerState: MusicPlayerState,
    onMusicCacheScreenEvent: (MusicCacheScreenEvent) -> Unit,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    val items = musicCacheScreenState.songs
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    MediaSwipeableLayout(
        musicPlayerState = musicPlayerState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerEvent,
        content = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = UiText.StringResource(R.string.cached_song).asString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
                )
                LazyRow(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                    items(
                        items = items,
                        key = { song: Song -> song.key() }
                    ) {
                        CachedSongItem(
                            song = it,
                            onItemClick = { onMusicPlayerEvent(MusicPlayerEvent.OnPlayPauseClick(it)) }
                        )
                    }
                }
            }
        }
    )
}
