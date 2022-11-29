package com.jooheon.clean_architecture.presentation.view.main.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.extensions.albumArtUri
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.view.components.CoilImage
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel
import com.jooheon.clean_architecture.presentation.view.main.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase

private const val TAG = "PlayerScreen"
@ExperimentalPermissionsApi
@Composable
fun ExoPlayerScreen(
    viewModel: MusicPlayerViewModel = hiltViewModel(sharedViewModel()),
    isPreview: Boolean = false
) {
    // ExoPlayer 정리글
    // https://jungwoon.github.io/android/library/2020/11/06/ExoPlayer.html

    val uiState = viewModel.uiState.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 10.dp, bottom = 64.dp), //  64dp is music bottom bar size
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = "This is Music Tab",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            modifier = Modifier,
            onClick = { Log.d(TAG, "onClick") },
            content = {
                Text(
                    text = "GET local song list",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
        )

        if(uiState.songList.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Local Song List",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            EmptySongItem()
        }

        LazyColumn {
            itemsIndexed(uiState.songList) { index, song ->
                MusicItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .height(96.dp),
                    song = song,
                    onItemClick = {
                        viewModel.playFromMediaId(it.id.toString())
                    }
                )
            }
        }
    }

    ObserveLifecycleEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MusicItem(
    modifier: Modifier,
    song: Entity.Song,
    onItemClick: (Entity.Song) -> Unit
) {
    Surface(
        onClick = { onItemClick(song) },
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            CoilImage(
                url = song.albumArtUri.toString(),
                contentDescription = song.title + "_Image",
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = song.artistName,
                    style = MaterialTheme.typography.labelSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EmptySongItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\uD83D\uDE31",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "no songs!",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@ExperimentalPermissionsApi
@Composable
private fun ObserveLifecycleEvent(
//    exoPlayer: ExoPlayer
) {
//    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
//    DisposableEffect(
//        key1 = lifecycleOwner,
//        effect = {
//            val observer = LifecycleEventObserver { lifecycleOwner, event ->
//                if(event == Lifecycle.Event.ON_STOP) {
//                    exoPlayer.release()
//                }
//            }
//
//            lifecycleOwner.lifecycle.addObserver(observer)
//
//            onDispose {
//                lifecycleOwner.lifecycle.removeObserver(observer)
//            }
//        }
//    )
}

@Preview
@Composable
private fun PreviewEmptySong() {
    ApplicationTheme(false) {
        EmptySongItem(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun PreviewSearchScreen() {
    val context = LocalContext.current
    val viewModel = MusicPlayerViewModel(MusicPlayerRemote(context))
    ApplicationTheme(false) {
        ExoPlayerScreen(viewModel, true)
    }
}