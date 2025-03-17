package com.jooheon.toyplayer.features.playlist.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.components.TopAppBarBox
import com.jooheon.toyplayer.features.playlist.details.component.PlaylistDetailMediaColumn
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailEvent
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailUiState

@Composable
fun PlaylistDetailScreen(
    playlistId: Int,
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
//    viewModel.navigateTo.observeWithLifecycle { route ->
//        if(route is ScreenNavigation.Back) {
//            onBackClick.invoke()
//        } else {
//            (route as? ScreenNavigation.Music)?.let {
//                navigate.invoke(it)
//            }
//        }
//    }
    LaunchedEffect(Unit) {
        viewModel.loadData(playlistId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlaylistDetailScreenInternal(
        uiState = uiState,
        onBackClick = {
            navigateTo.invoke(ScreenNavigation.Back)
        },
        onEvent = {
            viewModel.dispatch(it)
        },
//        onMediaDropDownMenuEvent = viewModel::onSongItemEvent,
    )
}

@Composable
private fun PlaylistDetailScreenInternal(
    uiState: PlaylistDetailUiState,
    onBackClick: () -> Unit,
    onEvent: (PlaylistDetailEvent) -> Unit,
//    onMediaDropDownMenuEvent: (SongItemEvent) -> Unit,
) {
    TopAppBarBox(
        title = uiState.playlist.name,
        onClick = onBackClick,
        modifier = Modifier.fillMaxSize()
    ) {
        PlaylistDetailMediaColumn(
            listState = rememberLazyListState(),
            playlist = uiState.playlist,
            onPlayClick = {
//                    onMusicPlayerEvent(MusicPlayerEvent.OnSongClick(it))
            },
            onPlayAllClick = {
//                    onMusicPlayerEvent(
//                        MusicPlayerEvent.OnEnqueue(
//                            songs = musicPlayingQueueScreenState.playlist.songs,
//                            shuffle = it,
//                            playWhenReady = true
//                        )
//                    )
            },
            onDropDownEvent = { index, song ->
//                    when(index) {
//                        0 -> onMusicPlayerEvent(MusicPlayerEvent.OnDeleteClick(song))
//                        1 -> onMediaDropDownMenuEvent(MusicDropDownMenuState.indexToEvent(index, song))
//                        else -> {
//                            /** Nothing **/
//                            /** Nothing **/
//                            /** Nothing **/
//                            /** Nothing **/
//                            /** Nothing **/
//                            /** Nothing **/
//                            /** Nothing **/    /** Nothing **/ }
//                    }
            },
        )
    }
}

@Preview
@Composable
private fun MusicPlaylistDetailScreenPreview() {
    ToyPlayerTheme {
        PlaylistDetailScreenInternal(
            uiState = PlaylistDetailUiState.preview,
            onBackClick = {},
            onEvent = {},
        )
    }
}
