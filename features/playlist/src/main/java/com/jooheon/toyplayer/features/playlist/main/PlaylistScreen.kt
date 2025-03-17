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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.jooheon.toyplayer.features.common.compose.components.dropdown.DropDownMenuEvent
import com.jooheon.toyplayer.features.common.compose.components.dropdown.MusicDropDownMenuState
import com.jooheon.toyplayer.features.playlist.main.component.PlaylistColumnItem
import com.jooheon.toyplayer.features.playlist.main.component.PlaylistDialog
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
                    val destination = ScreenNavigation.Playlist.Details(it.id)
                    navigateTo.invoke(destination)
                }
                else -> viewModel.dispatch(it)
            }
        },
        onDropDownMenuEvent = {},
    )
}

@Composable
private fun PlaylistScreenInternal(
    uiState: PlaylistUiState,
    onBackClick: () -> Unit,
    onEvent: (PlaylistEvent) -> Unit,
    onDropDownMenuEvent: (DropDownMenuEvent) -> Unit,
) {
    val listState = rememberLazyListState()
    var playlistDialogState by remember { mutableStateOf(false to Playlist.default) }

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
                            playlistDialogState = true to Playlist.default
                        }
                    )
                }

                items(
                    items = uiState.playlists,
                    key = { playlist: Playlist -> playlist.hashCode() }
                ) { playlist ->
                    PlaylistColumnItem(
                        playlist = playlist,
                        showContextualMenu = playlist.id !in Playlist.defaultPlaylistIds.map { (id, _) -> id },
                        onItemClick = { onEvent.invoke(PlaylistEvent.OnPlaylistClick(playlist.id)) },
                        onDropDownMenuClick = {
                            val event = MusicDropDownMenuState.indexToEvent(it, playlist)

                            when(event) {
                                is DropDownMenuEvent.OnChangeName -> playlistDialogState = true to playlist
                                else -> onDropDownMenuEvent.invoke(event)
                            }
                        },
                    )
                }
            }
        )

        PlaylistDialog(
            state = playlistDialogState,
            onOkButtonClicked = {
                playlistDialogState = false to Playlist.default
                onEvent.invoke(PlaylistEvent.OnAddPlaylist(it))
            },
            onDismissRequest = {
                playlistDialogState = false to Playlist.default
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
            onDropDownMenuEvent = {},
        )
    }
}