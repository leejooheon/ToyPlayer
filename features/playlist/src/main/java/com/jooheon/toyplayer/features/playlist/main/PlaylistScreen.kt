package com.jooheon.toyplayer.features.playlist.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.features.common.compose.components.TopAppBarBox
import com.jooheon.toyplayer.features.playlist.main.component.PlaylistColumnItem
import com.jooheon.toyplayer.features.playlist.main.component.PlaylistHeader
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
                    val destination = ScreenNavigation.Playlist.Details(it.playlist.id)
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
    val listState = rememberLazyListState()
    TopAppBarBox(
        title = UiText.StringResource(Strings.title_playlist).asString(),
        onClick = onBackClick,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 16.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            content = {
                item {
                    PlaylistHeader(
                        onAddPlaylistClick = {

                        }
                    )
                }

                items(
                    items = uiState.playlists,
                    key = { playlist: Playlist -> playlist.hashCode() }
                ) { playlist ->
                    PlaylistColumnItem(
                        playlist = playlist,
                        showContextualMenu = playlist.id !in Playlist.defaultPlaylistIds.map { it.hashCode() },
                        onItemClick = {
//                            onItemClick(playlist)
                                      },
                        onDropDownMenuClick = { _ ,_ ->
//                                              onDropDownMenuClick,
                        },
                    )
                }
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