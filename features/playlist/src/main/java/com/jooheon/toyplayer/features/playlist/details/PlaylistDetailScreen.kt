package com.jooheon.toyplayer.features.playlist.details

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
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
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.commonui.components.CustomTopAppBar
import com.jooheon.toyplayer.features.commonui.components.dialog.SongDetailsDialog
import com.jooheon.toyplayer.features.commonui.components.media.MediaDetailHeader
import com.jooheon.toyplayer.features.commonui.components.media.MediaItemSmallNoImage
import com.jooheon.toyplayer.features.commonui.components.menu.DropDownMenu
import com.jooheon.toyplayer.features.commonui.components.menu.MenuDialogState
import com.jooheon.toyplayer.features.playlist.details.component.PermissionRequestItem
import com.jooheon.toyplayer.features.playlist.details.component.PlaylistDetailHeader
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailEvent
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailUiState
import timber.log.Timber

@Composable
fun PlaylistDetailScreen(
    playlistId: Int,
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData(playlistId)
    }

    PlaylistDetailScreenInternal(
        uiState = uiState,
        onBackClick = { navigateTo.invoke(ScreenNavigation.Back) },
        onEvent = { viewModel.dispatch(it) },
    )
}

@Composable
private fun PlaylistDetailScreenInternal(
    uiState: PlaylistDetailUiState,
    onBackClick: () -> Unit,
    onEvent: (PlaylistDetailEvent) -> Unit,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var dialogState by remember { mutableStateOf(MenuDialogState.default) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            Timber.d("selected Uri: $uri")
            uri?.let {
                val event = PlaylistDetailEvent.OnThumbnailImageSelected(context, it)
                onEvent.invoke(event)
            }
        }
    )

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
                if(uiState.requirePermission) {
                    PermissionRequestItem(
                        launchPermissionRequest = {
                            onEvent.invoke(PlaylistDetailEvent.OnPermissionRequest(context))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        content = {
                            item {
                                PlaylistDetailHeader(
                                    playlist = uiState.playlist,
                                    onPlayAllClick = { onEvent.invoke(PlaylistDetailEvent.OnPlayAll(true)) },
                                    onThumbnailImageClick = {
                                        singlePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                )
                            }

                            item {
                                MediaDetailHeader(
                                    count = uiState.playlist.songs.size
                                )
                            }

                            itemsIndexed(
                                items = uiState.playlist.songs,
                            ) { index, song ->
                                MediaItemSmallNoImage(
                                    index = index + 1,
                                    title = song.title,
                                    subTitle = "${song.artist} • ${song.album}",
                                    duration = MusicUtil.toReadableDurationString(song.duration),
                                    dropDownMenus = DropDownMenu.playlistMediaItemMenuItems,
                                    onItemClick = { onEvent.invoke(PlaylistDetailEvent.OnPlay(index)) },
                                    onDropDownMenuClick = { menu ->
                                        when (menu) {
                                            DropDownMenu.PlaylistMediaItemDelete -> {
                                                onEvent.invoke(PlaylistDetailEvent.OnDelete(song))
                                            }

                                            DropDownMenu.MediaItemDetails -> {
                                                dialogState = MenuDialogState(
                                                    type = MenuDialogState.Type.SongInfo,
                                                    song = song,
                                                )
                                            }

                                            else -> throw IllegalArgumentException("")
                                        }
                                    }
                                )
                            }

                            item {
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    )
                }

                when(dialogState.type) {
                    MenuDialogState.Type.SongInfo -> {
                        SongDetailsDialog(
                            song = dialogState.song,
                            onDismissRequest = {
                                dialogState = MenuDialogState.default
                            }
                        )
                    }
                    else -> { /** nothing **/ }
                }
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
