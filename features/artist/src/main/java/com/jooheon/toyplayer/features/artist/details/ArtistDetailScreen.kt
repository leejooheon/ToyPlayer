package com.jooheon.toyplayer.features.artist.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.features.artist.details.component.ArtistDetailAlbumItem
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailEvent
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailUiState
import com.jooheon.toyplayer.features.common.compose.components.CustomTopAppBar
import com.jooheon.toyplayer.features.common.compose.components.dialog.PlaylistDialog
import com.jooheon.toyplayer.features.common.compose.components.dialog.SelectPlaylistDialog
import com.jooheon.toyplayer.features.common.compose.components.dialog.SongDetailsDialog
import com.jooheon.toyplayer.features.common.compose.components.dropdown.MusicDropDownMenu
import com.jooheon.toyplayer.features.common.compose.components.media.MediaDetailHeader
import com.jooheon.toyplayer.features.common.compose.components.media.MediaItemSmallNoImage
import com.jooheon.toyplayer.features.common.utils.MusicUtil

@Composable
fun ArtistDetailScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    artistId: String,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData(context, artistId)
    }

    ArtistDetailScreenInternal(
        uiState = uiState,
        onEvent = {
            when(it) {
                is ArtistDetailEvent.OnAlbumClick -> navigateTo.invoke(ScreenNavigation.Album.Details(it.id))
                else -> viewModel.dispatch(it)
            }
        },
        onBackClick = {
            navigateTo.invoke(ScreenNavigation.Back)
        }
    )
}

@Composable
private fun ArtistDetailScreenInternal(
    uiState: ArtistDetailUiState,
    onEvent: (ArtistDetailEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val listState = rememberLazyListState()
    var dialogState by remember { mutableStateOf(ArtistDetailUiState.DialogState.default) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = uiState.artist.name,
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.artist.albums,
                        key = { album: Album -> album.hashCode() }
                    ) { album ->
                        ArtistDetailAlbumItem(
                            imageUrl = album.songs.firstOrNull()?.imageUrl.defaultEmpty(),
                            title = album.name,
                            subTitle = album.artist,
                            onItemClick = { onEvent.invoke(ArtistDetailEvent.OnAlbumClick(album.id)) },
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )

                        MediaDetailHeader(count = album.songs.size)

                        album.songs.forEach { song ->
                            MediaItemSmallNoImage(
                                trackNumber = song.trackNumber,
                                title = song.title,
                                subTitle = "${song.artist} • ${song.album}",
                                duration = MusicUtil.toReadableDurationString(song.duration),
                                dropDownMenus = MusicDropDownMenu.mediaMenuItems,
                                onItemClick = { onEvent.invoke(ArtistDetailEvent.OnSongClick(song)) },
                                onDropDownMenuClick = { menu ->
                                    when(menu) {
                                        MusicDropDownMenu.MediaItemAddToPlayingQueue -> onEvent.invoke(ArtistDetailEvent.OnAddPlayingQueue(song))
                                        MusicDropDownMenu.MediaItemAddToPlaylist -> {
                                            dialogState = ArtistDetailUiState.DialogState(
                                                type = ArtistDetailUiState.DialogState.Type.SelectPlaylist,
                                                song = song,
                                            )
                                        }
                                        MusicDropDownMenu.MediaItemDetails -> {
                                            dialogState = ArtistDetailUiState.DialogState(
                                                type = ArtistDetailUiState.DialogState.Type.SongInfo,
                                                song = song,
                                            )
                                        }
                                        else -> throw IllegalArgumentException("")
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                when(dialogState.type) {
                    ArtistDetailUiState.DialogState.Type.SongInfo -> {
                        SongDetailsDialog(
                            song = dialogState.song,
                            onDismissRequest = {
                                dialogState = ArtistDetailUiState.DialogState.default
                            }
                        )
                    }
                    ArtistDetailUiState.DialogState.Type.SelectPlaylist -> {
                        SelectPlaylistDialog(
                            playlists = uiState.playlists,
                            onPlaylistClick = {
                                if(it.id == Playlist.default.id) {
                                    dialogState = ArtistDetailUiState.DialogState(
                                        type = ArtistDetailUiState.DialogState.Type.NewPlaylist,
                                        song = dialogState.song,
                                    )
                                } else {
                                    onEvent.invoke(ArtistDetailEvent.OnAddPlaylist(it, dialogState.song))
                                    dialogState = ArtistDetailUiState.DialogState.default
                                }
                            },
                            onDismissRequest = {
                                dialogState = ArtistDetailUiState.DialogState.default
                            }
                        )
                    }
                    ArtistDetailUiState.DialogState.Type.NewPlaylist -> {
                        PlaylistDialog(
                            state = true to Playlist.default,
                            onOkButtonClicked = {
                                onEvent.invoke(ArtistDetailEvent.OnAddPlaylist(Playlist.default.copy(name = it), dialogState.song))
                                dialogState = ArtistDetailUiState.DialogState.default
                            },
                            onDismissRequest = {
                                dialogState = ArtistDetailUiState.DialogState.default
                            }
                        )
                    }
                    ArtistDetailUiState.DialogState.Type.None -> { /** nothing **/ }
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewArtistDetailScreen() {
    ToyPlayerTheme {
        ArtistDetailScreenInternal(
            uiState = ArtistDetailUiState.default,
            onEvent = {},
            onBackClick = {},
        )
    }
}