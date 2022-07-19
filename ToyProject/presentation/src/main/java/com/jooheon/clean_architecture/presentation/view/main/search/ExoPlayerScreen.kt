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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.UiText

private const val TAG = "PlayerScreen"
@ExperimentalPermissionsApi
@Composable
fun ExoPlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    // ExoPlayer 정리글
    // https://jungwoon.github.io/android/library/2020/11/06/ExoPlayer.html

    val context = LocalContext.current
    val exoPlayer = remember { createExoPlayer(context) }
    var contentsFlag by remember { mutableStateOf(false) }

    setMediaContent(context, exoPlayer, contentsFlag)

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
                viewModel.fetchSongs()
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

        LazyColumn {
            viewModel.songList.value?.let { songs ->
                itemsIndexed(songs) { index, song ->
                    Text(
                        text = "${index + 1}: ${song.title}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    min = 100.dp,
                    max = 400.dp
                ),
            factory = {
                StyledPlayerView(it).apply {
                    player = exoPlayer
                }
            }
        )
    }

    ObserveLifecycleEvent(exoPlayer)
}

private fun setMediaContent(context: Context, exoPlayer: ExoPlayer, flag: Boolean) {
    val mediaContent: String
    if(flag) {
        mediaContent = UiText.StringResource(R.string.media_url_mp4).asString(context)
    } else {
        mediaContent = UiText.StringResource(R.string.test_mp3).asString(context)
    }

    exoPlayer.apply {
        val dataSourceFactory = DefaultHttpDataSource.Factory().apply {
            setUserAgent(Util.getUserAgent(context, context.packageName))
        }
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            MediaItem.fromUri(mediaContent)
        )

        clearMediaItems()
        addMediaSource(mediaSource)
        prepare()
    }
}

private fun getMediaItem(context: Context, resourceId: Int) {
    val resource = UiText.StringResource(resourceId).asString(context)
    val mediaItem = MediaItem.fromUri(resource)

}

private fun createExoPlayer(context: Context) = ExoPlayer.Builder(context).build()
    .apply { addListener(listener) }

@ExperimentalPermissionsApi
@Composable
private fun ObserveLifecycleEvent(
    exoPlayer: ExoPlayer
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->
                if(event == Lifecycle.Event.ON_STOP) {
                    exoPlayer.release()
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
}

private val listener = object : Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            Player.STATE_IDLE -> {
                Log.d(TAG, "onPlaybackStateChanged: Player.STATE_IDLE")
            }
            Player.STATE_BUFFERING -> {
                Log.d(TAG, "onPlaybackStateChanged: Player.STATE_BUFFERING")
            }
            Player.STATE_READY -> {
                Log.d(TAG, "onPlaybackStateChanged: Player.STATE_READY")
            }
            Player.STATE_ENDED -> {
                Log.d(TAG, "onPlaybackStateChanged: Player.STATE_ENDED")
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.d(TAG, "onIsPlayingChanged: $isPlaying")
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        Log.d(TAG, "onMediaMetadataChanged title: ${mediaMetadata.title}, displayTtle: ${mediaMetadata.displayTitle}")

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun PreviewSearchScreen() {
    PreviewTheme(false) {
        ExoPlayerScreen()
    }
}