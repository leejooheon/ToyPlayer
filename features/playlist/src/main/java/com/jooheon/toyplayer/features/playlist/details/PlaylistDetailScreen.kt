package com.jooheon.toyplayer.features.playlist.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.components.CustomTopAppBar
import com.jooheon.toyplayer.features.common.compose.components.dropdown.MusicDropDownMenu
import com.jooheon.toyplayer.features.playlist.details.component.PlaylistDetailMediaColumn
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailEvent
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailUiState
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistEvent

@Composable
fun PlaylistDetailScreen(
    playlistId: Int,
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData(playlistId)
    }

    PlaylistDetailScreenInternal(
        uiState = uiState,
        onBackClick = { navigateTo.invoke(ScreenNavigation.Back) },
        onEvent = { viewModel.dispatch(context, it) },
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
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = uiState.playlist.name,
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                PlaylistDetailMediaColumn(
                    listState = rememberLazyListState(),
                    playlist = uiState.playlist,
                    onPlayClick = {
                        onEvent.invoke(PlaylistDetailEvent.OnPlayAllClick(false))
                    },
                    onPlayAllClick = {
                        onEvent.invoke(PlaylistDetailEvent.OnPlayAllClick(true))
                    },
                    onDropDownEvent = { menu, song ->
                        when(menu) {
                            MusicDropDownMenu.PlaylistMediaItemDelete -> {}
                            MusicDropDownMenu.PlaylistMediaItemDetails -> {}
                            else -> throw IllegalArgumentException("")
                        }
                    },
                )
            }
        }
    )
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
