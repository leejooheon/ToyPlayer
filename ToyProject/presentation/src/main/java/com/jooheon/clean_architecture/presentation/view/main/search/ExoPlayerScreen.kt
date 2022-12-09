package com.jooheon.clean_architecture.presentation.view.main.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.view.main.music.EmptySongItem
import com.jooheon.clean_architecture.presentation.view.main.music.MusicItem
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import kotlinx.coroutines.Dispatchers

private const val TAG = "PlayerScreen"
@ExperimentalPermissionsApi
@Composable
fun ExoPlayerScreen(
    viewModel: MusicPlayerViewModel = hiltViewModel(sharedViewModel()),
    isPreview: Boolean = false
) {
    // ExoPlayer 정리글
    // https://jungwoon.github.io/android/library/2020/11/06/ExoPlayer.html

    val uiState by viewModel.musicState.collectAsState()

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

        if(uiState.songs.isNotEmpty()) {
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
            itemsIndexed(uiState.songs) { index, song ->
                MusicItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .height(96.dp),
                    song = song,
                    onItemClick = viewModel::onPlayPauseButtonPressed
                )
            }
        }
    }

    ObserveLifecycleEvent()
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

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun PreviewSearchScreen() {
    val context = LocalContext.current
    val musicPlayerUseCase = MusicPlayerUseCase(EmptyMusicUseCase())
    val musicPlayerViewModel = MusicPlayerViewModel(
        context = context,
        dispatcher= Dispatchers.IO,
        musicController = MusicController(context, musicPlayerUseCase, true)
    )

    ApplicationTheme(false) {
        ExoPlayerScreen(musicPlayerViewModel, true)
    }
}