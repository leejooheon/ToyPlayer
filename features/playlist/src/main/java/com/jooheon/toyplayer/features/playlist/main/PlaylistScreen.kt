package com.jooheon.toyplayer.features.playlist.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.common.compose.components.TopAppBarBox
import com.jooheon.toyplayer.features.playlist.main.component.PlaylistMediaColumn
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistEvent
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistUiState

@Composable
fun PlaylistScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    PlaylistScreenInternal(
        uiState = state,
        onBackClick = { navigateTo.invoke(ScreenNavigation.Back) },
        onEvent = {
            when(it) {
                is PlaylistEvent.OnPlaylistClick -> {
                    val destination = ScreenNavigation.Music.PlaylistDetail(it.playlist.id)
                    navigateTo.invoke(destination)
                }
                else -> {}
            }
        },
    )
}

@Composable
private fun PlaylistScreenInternal(
    uiState: PlaylistUiState,
    onBackClick: () -> Unit,
    onEvent: (PlaylistEvent) -> Unit,
) {
    TopAppBarBox(
        title = UiText.StringResource(Strings.title_playlist).asString(),
        onClick = onBackClick,
        modifier = Modifier.fillMaxSize()
    ) {
        PlaylistMediaColumn(
            listState = rememberLazyListState(),
            playlists = uiState.playlists ,
            onItemClick = { onEvent(PlaylistEvent.OnPlaylistClick(it)) },
            onAddPlaylistClick = {
//                onMusicPlaylistScreenEvent(MusicPlaylistScreenEvent.OnAddPlaylist(it))
                                 },
            onDropDownMenuClick = { index, playlist ->
//                val event = MusicDropDownMenuState.indexToEvent(index, playlist)
//                onMusicPlaylistItemEvent(event)
            }
        )
    }
}

@Preview
@Composable
private fun MusicPlaylistScreenPreview() {
    ToyPlayerTheme {
        PlaylistScreenInternal(
            uiState = PlaylistUiState.preview,
            onBackClick = {},
            onEvent = {},
        )
    }
}