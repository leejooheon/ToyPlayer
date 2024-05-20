package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.components.MusicSongOptionDialog
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.toyplayer.features.essential.base.UiText
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.common.compose.components.PermissionRequestItem
import com.jooheon.toyplayer.features.common.compose.components.appDetailSettings
import com.jooheon.toyplayer.features.common.compose.components.isPermissionRequestBlocked
import com.jooheon.toyplayer.features.common.compose.components.savePermissionRequested
import com.jooheon.toyplayer.features.common.utils.VersionUtil
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.components.MusicComponent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.components.MusicSongCommonHeader
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.components.SongComponent
import java.lang.Float
import kotlin.math.max

@Composable
fun MusicSongScreen(
    navController: NavController,
    viewModel: MusicSongScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    viewModel.navigateTo.observeWithLifecycle {
        navController.navigate(it)
    }
    viewModel.musicListType.observeWithLifecycle {
        val event = MusicSongScreenEvent.OnRefresh(context, it)
        viewModel.dispatch(event)
    }

    val screenState by viewModel.musicPlayerScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicSongScreen(
        musicSongState = screenState,
        onMusicSongScreenEvent = viewModel::dispatch,
        onMusicMediaItemEvent = viewModel::onSongItemEvent,

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
private fun MusicSongScreen(
    musicSongState: MusicSongScreenState,
    onMusicSongScreenEvent: (MusicSongScreenEvent) -> Unit,
    onMusicMediaItemEvent: (SongItemEvent) -> Unit,

    musicPlayerState: MusicPlayerState,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    var openDialog by remember { mutableStateOf(false) }

    val permission = if (VersionUtil.hasTiramisu()) Manifest.permission.READ_MEDIA_AUDIO
                     else Manifest.permission.READ_EXTERNAL_STORAGE

    val permissionState = rememberPermissionState(permission)
    val rememberLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val activity = LocalContext.current as Activity

    var isPermissionRequestBlockedState by remember {
        mutableStateOf(isPermissionRequestBlocked(activity, permission))
    }

    LaunchedEffect(permissionState.status) {
        if(permissionState.status.isGranted) {
            val event = MusicSongScreenEvent.OnRefresh(context, musicSongState.musicListType)
            onMusicSongScreenEvent(event)
        }
        isPermissionRequestBlockedState = isPermissionRequestBlocked(activity, permission)
    }

    if(!permissionState.status.isGranted) {
        PermissionRequestItem(
            resId = R.drawable.folder_search_base_256_blu_glass,
            description = UiText.StringResource(R.string.description_permission_read_storage),
            isPermissionRequestBlocked = isPermissionRequestBlockedState,
            launchPermissionRequest = {
                if (permissionState.status.isGranted) {
                    return@PermissionRequestItem
                }
                if (isPermissionRequestBlocked(activity, permission)) {
                    rememberLauncher.launch(appDetailSettings(activity))
                    return@PermissionRequestItem
                }
                permissionState.launchPermissionRequest()
                savePermissionRequested(activity, permission)
            }
        )
        return
    }

    MediaSwipeableLayout(
        musicPlayerState = musicPlayerState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerEvent,
        content = {

            MusicSongCommonHeader(
                title = UiText.StringResource(R.string.title_song),
                resId = R.drawable.default_album_art
            )
            SongComponent(
                dataSet = musicSongState.songList,
                onMusicSongScreenEvent = onMusicSongScreenEvent,
                onMusicPlayerEvent = onMusicPlayerEvent,
            )

            Spacer(modifier = Modifier.height(16.dp))

            MusicSongCommonHeader(
                title = UiText.DynamicString("Device Library"),
                resId = R.drawable.default_album_art
            )
            MusicComponent(
                state = musicSongState,
                onMusicSongScreenEvent = onMusicSongScreenEvent,
            )

//            MusicSongMediaHeader(
//                viewType = viewType,
//                onSeeMoreButtonClick = { openDialog = true },
//                onViewTypeClick = { viewType = it} ,
//                modifier = Modifier
//                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
//                    .fillMaxWidth()
//            )
//            MusicSongMediaColumn(
//                musicSongScreenState = musicSongState,
//                songMediaColumnItemType = viewType,
//                listState = listState,
//                onSongClick = {
//                    if (swipeableState.currentValue == 0) {
//                        if (musicState.currentPlayingMusic != it) {
//                            onMusicPlayerEvent(MusicPlayerEvent.OnSongClick(it))
//                        } else {
//                            scope.launch {
//                                swipeableState.animateTo(1)
//                            }
//                        }
//                    }
//                },
//                onMediaItemEvent = onMusicMediaItemEvent,
//                modifier = Modifier
//                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
//                    .fillMaxWidth()
//                    .scrollEnabled(motionProgress == 0f),
//            )
        }
    )

    MusicSongOptionDialog(
        musicListType = musicSongState.musicListType,
        openDialog = openDialog,
        onDismiss = { openDialog = false },
        onOkButtonClicked = {
            openDialog = false
            onMusicSongScreenEvent(MusicSongScreenEvent.OnMusicListTypeChanged(it))
        }
    )
}

@Preview
@Composable
private fun MusicScreenPreview() {
    PreviewTheme(false) {
        MusicSongScreen(
            musicSongState = MusicSongScreenState.default,
            onMusicSongScreenEvent = { _, -> },
            onMusicMediaItemEvent = { },

            musicPlayerState = MusicPlayerState.default.copy(
                musicState = MusicState(
                    currentPlayingMusic = Song.default.copy(
                        title = Resource.longStringPlaceholder,
                        artist = Resource.mediumStringPlaceholder,
                    ),
                )
            ),
            onMusicPlayerEvent = {},
        )
    }
}