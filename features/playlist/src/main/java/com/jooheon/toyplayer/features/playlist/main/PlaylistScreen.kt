package com.jooheon.toyplayer.features.playlist.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.jooheon.toyplayer.features.commonui.components.CustomTopAppBar
import com.jooheon.toyplayer.features.commonui.components.menu.DropDownMenu
import com.jooheon.toyplayer.features.playlist.main.component.PlaylistColumnItem
import com.jooheon.toyplayer.features.commonui.components.dialog.PlaylistDialog
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistEvent
import com.jooheon.toyplayer.features.playlist.main.model.PlaylistUiState

@Composable
fun PlaylistScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PlaylistScreenInternal(
        uiState = state,
        onBackClick = { navigateTo.invoke(ScreenNavigation.Back) },
        onEvent = {
            when(it) {
                is PlaylistEvent.OnNavigatePlaylist -> {
                    val destination = ScreenNavigation.Playlist.Details(it.id)
                    navigateTo.invoke(destination)
                }
                else -> viewModel.dispatch(it)
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
    var playlistDialogState by remember { mutableStateOf(false to Playlist.default) }

    BackHandler {
        onBackClick.invoke()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { playlistDialogState = true to Playlist.default }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = UiText.StringResource(Strings.option_add_playlist).asString()
                )
            }
        },
        topBar = {
            CustomTopAppBar(
                title = UiText.StringResource(Strings.title_playlist).asString(),
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
//                        item {
//                            PlaylistHeader(
//                                onAddPlaylistClick = {
//                                    playlistDialogState = true to Playlist.default
//                                }
//                            )
//                        }

                        items(
                            items = uiState.playlists,
                            key = { playlist: Playlist -> playlist.hashCode() }
                        ) { playlist ->
                            PlaylistColumnItem(
                                playlist = playlist,
                                showContextualMenu = playlist.id !in Playlist.defaultPlaylists.map { it.id },
                                onItemClick = { onEvent.invoke(PlaylistEvent.OnNavigatePlaylist(playlist.id)) },
                                onDropDownMenuClick = { menu ->
                                    when(menu) {
                                        DropDownMenu.PlaylistChangeName -> playlistDialogState = true to playlist
                                        DropDownMenu.PlaylistDelete -> onEvent.invoke(PlaylistEvent.OnDeletePlaylist(playlist.id))
                                        else -> throw IllegalArgumentException("")
                                    }
                                },
                            )
                        }
                    }
                )

                PlaylistDialog(
                    state = playlistDialogState,
                    onOkButtonClicked = {
                        val playlistId = playlistDialogState.second.id
                        playlistDialogState = false to Playlist.default
                        onEvent.invoke(PlaylistEvent.OnAddPlaylist(it, playlistId))
                    },
                    onDismissRequest = {
                        playlistDialogState = false to Playlist.default
                    }
                )
            }
        }
    )
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