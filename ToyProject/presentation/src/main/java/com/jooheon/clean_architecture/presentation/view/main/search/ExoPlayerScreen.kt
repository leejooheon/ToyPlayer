package com.jooheon.clean_architecture.presentation.view.main.search

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase

private const val TAG = "PlayerScreen"
@ExperimentalPermissionsApi
@Composable
fun ExoPlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    isPreview: Boolean = false
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
            onClick = { viewModel.fetchSongs() },
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

        LazyColumn {
            val songList = if(isPreview) {
                EmptyMusicUseCase.dummyData()
            } else {
                viewModel.songList.value
            } ?: return@LazyColumn

            itemsIndexed(songList) { index, song ->
                MusicItem(modifier = Modifier, song = song) { song, isPlaying ->
                    if(isPlaying) {
                        MusicPlayerRemote.pauseSong()
                    } else {
                        MusicPlayerRemote.openQueue(listOf(song))
                    }
                }
            }
        }
    }

    ObserveLifecycleEvent()
}

@Composable
private fun MusicItem(
    modifier: Modifier,
    song: Entity.Song,
    onClick: (Entity.Song, Boolean) -> Unit
) {
    val isPlaying = remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .heightIn(min = 50.dp)
            .clickable {
                onClick.invoke(song, isPlaying.value)

                isPlaying.value = !isPlaying.value
            }
    ) {
        Row {
            val imageUrl = MusicUtil.getMediaStoreAlbumCoverUri(song.albumId)
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                        placeholder(drawableResId = R.drawable.ic_logo_github)
                    }).build()
                ),
                contentDescription = "music name is " + song.title,
                modifier = Modifier.width(50.dp),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.heightIn(4.dp))
                Text(
                    text = song.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Image(
                painter = painterResource(id = if(isPlaying.value) {
                    R.drawable.ic_play_arrow_white_48dp
                } else {
                    R.drawable.ic_pause_white_48dp
                }),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
            )
        }
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
fun PreviewSearchScreen() {
    val viewModel = PlayerViewModel(EmptyMusicUseCase())
    PreviewTheme(false) {
//        ExoPlayerScreen(viewModel)
        MusicItem(Modifier, EmptyMusicUseCase.dummyData().first()) { song, isPlaying ->

        }
    }
}