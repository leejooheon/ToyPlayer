package com.jooheon.clean_architecture.presentation.view.main.search

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase

private const val TAG = "PlayerScreen"
@ExperimentalPermissionsApi
@Composable
fun ExoPlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    // ExoPlayer 정리글
    // https://jungwoon.github.io/android/library/2020/11/06/ExoPlayer.html

    val context = LocalContext.current
//    val exoPlayer = remember { createExoPlayer(context) }
    val contentsFlag by remember { mutableStateOf(false) }

//    setMediaContent(context, exoPlayer, contentsFlag)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "This is ExoPlayer",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            modifier = Modifier,
            onClick = {
//                viewModel.fetchSongs()
//                setMediaContent(context, exoPlayer, contentsFlag)
//                contentsFlag = !contentsFlag
            },
            content = {
                Text(
                    text = "Toggle to " + if(contentsFlag) { "mp3" } else { "mp4" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
        )

        Text(
            modifier = Modifier.padding(10.dp),
            text = "Local Song List",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

//        LazyColumn {
//            viewModel.songList.value?.let { songs ->
//                itemsIndexed(songs) { index, song ->
//                    Text(
//                        text = "${index + 1}: ${song.title}",
//                        style = MaterialTheme.typography.labelLarge,
//                        color = MaterialTheme.colorScheme.onBackground
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }
//        }

//        AndroidView(
//            modifier = Modifier
//                .fillMaxWidth()
//                .heightIn(
//                    min = 100.dp,
//                    max = 400.dp
//                ),
//            factory = {
//                StyledPlayerView(it).apply {
//                    player = exoPlayer
//                }
//            }
//        )
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
    val viewModel = PlayerViewModel(EmptyMusicUseCase())
    PreviewTheme(false) {
        ExoPlayerScreen(viewModel)
    }
}