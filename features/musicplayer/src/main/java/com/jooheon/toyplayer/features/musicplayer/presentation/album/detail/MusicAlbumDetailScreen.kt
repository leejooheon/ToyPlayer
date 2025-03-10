package com.jooheon.toyplayer.features.musicplayer.presentation.album.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.common.compose.components.CoilImage
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.components.MusicAlbumDetailMediaColumn
import com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.model.MusicAlbumDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.model.MusicAlbumDetailScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import java.lang.Float
import kotlin.OptIn
import kotlin.String
import kotlin.Unit
import kotlin.let
import kotlin.math.max
import kotlin.with

@Composable
fun MusicAlbumDetailScreen(
    onBackClick: () -> Unit,
    navigate: (ScreenNavigation.Music) -> Unit,
    albumId: String,
    viewModel: MusicAlbumDetailScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    viewModel.initialize(context, albumId)
    viewModel.navigateTo.observeWithLifecycle { route ->
        if(route is ScreenNavigation.Back) {
            onBackClick.invoke()
        } else {
            (route as? ScreenNavigation.Music)?.let {
                navigate.invoke(route)
            }
        }
    }
    val state by viewModel.musicAlbumDetailScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicAlbumDetailScreen(
        musicAlbumDetailScreenState = state,
        onMusicAlbumDetailScreenEvent = viewModel::dispatch,
        onMusicMediaItemEvent = viewModel::onSongItemEvent,

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MusicAlbumDetailScreen(
    musicAlbumDetailScreenState: MusicAlbumDetailScreenState,
    musicPlayerState: MusicPlayerState,
    onMusicAlbumDetailScreenEvent: (MusicAlbumDetailScreenEvent) -> Unit,
    onMusicMediaItemEvent: (SongItemEvent) -> Unit,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val listState = rememberLazyListState()

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    var songItemEventState by remember {
        mutableStateOf<SongItemEvent>(SongItemEvent.Placeholder)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = musicAlbumDetailScreenState.album.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { onMusicAlbumDetailScreenEvent(MusicAlbumDetailScreenEvent.OnBackClick) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = UiText.StringResource(Strings.back).asString(),
                    )
                }
            }
        )
        MediaSwipeableLayout(
            musicPlayerState = musicPlayerState,
            swipeableState = swipeableState,
            swipeAreaHeight = swipeAreaHeight,
            motionProgress = motionProgress,
            onEvent = onMusicPlayerEvent,
            content = {
                MusicAlbumDetailMediaColumn(
                    musicAlbumDetailScreenState = musicAlbumDetailScreenState,
                    listState = listState,
                    onEvent = onMusicAlbumDetailScreenEvent,
                    onMusicPlayerEvent = onMusicPlayerEvent,
                    onMediaItemEvent = onMusicMediaItemEvent,
                )
            }
        )
    }

    BackHandler {
        onMusicAlbumDetailScreenEvent(MusicAlbumDetailScreenEvent.OnBackClick)
    }
}

@Composable
private fun MediaAlbumHeader(album: com.jooheon.toyplayer.domain.model.music.Album) {
    val albumDuration = album.songs.fastSumBy { it.duration.toInt() }.toLong()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        CoilImage(
            url = album.imageUrl,
            contentDescription = album.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(15))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.default_album_art),
                contentDescription = "Default Album Art",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = album.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${album.artist} • ${MusicUtil.toReadableDurationString(albumDuration)}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            OutlinedButton(
                onClick = { /** TODO **/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = UiText.StringResource(R.string.action_play_all).asString(),
                    color = MaterialTheme.colorScheme.onTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedButton(
                onClick = { /** TODO **/},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = UiText.StringResource(R.string.action_play_all_shuffle).asString(),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Composable
private fun MusicAlbumDetailScreenPreview() {
    ToyPlayerTheme {
        MusicAlbumDetailScreen(
            musicAlbumDetailScreenState = MusicAlbumDetailScreenState.default.copy(
                album = com.jooheon.toyplayer.domain.model.music.Album.default.copy(
                    name = UiText.StringResource(R.string.placeholder_long).asString(),
                    artist = UiText.StringResource(R.string.placeholder_medium).asString(),
                )
            ),
            onMusicAlbumDetailScreenEvent = {},
            onMusicMediaItemEvent = {},
            musicPlayerState = MusicPlayerState.default,
            onMusicPlayerEvent = {},
        )
    }
}