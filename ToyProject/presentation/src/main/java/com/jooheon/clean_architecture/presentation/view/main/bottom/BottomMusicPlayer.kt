package com.jooheon.clean_architecture.presentation.view.main.bottom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.view.main.music.*
import com.jooheon.clean_architecture.presentation.view.main.music.AlbumImage
import com.jooheon.clean_architecture.presentation.view.main.music.MusicProgress
import com.jooheon.clean_architecture.presentation.view.main.music.PlayListButton
import com.jooheon.clean_architecture.presentation.view.main.music.PlayPauseButton
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import kotlinx.coroutines.Dispatchers

private const val TAG = "MusicBottomBar"
private val MOTION_CONTENT_HEIGHT = 64.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomMusicPlayer(
    song: Entity.Song,
    isPlaying: Boolean,
    onItemClick: () -> Unit,
    onPlayPauseButtonPressed: (Entity.Song) -> Unit,
    onPlayListButtonPressed: () -> Unit,
) {
    Surface(
        onClick = onItemClick,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,) {
            AlbumImage(
                isPlaying = isPlaying,
                song = song,
                modifier = Modifier
                    .height(MOTION_CONTENT_HEIGHT)
                    .aspectRatio(1.0f)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = song.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    overflow = TextOverflow.Ellipsis
                )
            }
            PlayPauseButton(
                song = song,
                isPlaying = isPlaying,
                onPlayPauseButtonPressed = onPlayPauseButtonPressed,
                modifier = Modifier
                    .height(MOTION_CONTENT_HEIGHT)
                    .aspectRatio(1f)
            )
            PlayListButton(
                onPlayListButtonPressed = onPlayListButtonPressed,
                modifier = Modifier
                    .height(MOTION_CONTENT_HEIGHT)
                    .aspectRatio(1f)
            )
        }
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
private fun MotionLayoutTest(
    viewModel: MusicPlayerViewModel
) {
    val fraction = viewModel.motionFraction.collectAsState()
    val context = LocalContext.current
    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.motion_scene_music_bar)
            .readBytes()
            .decodeToString()
    }
    Box {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(MOTION_CONTENT_HEIGHT)
                .align(Alignment.TopCenter),
            motionScene = MotionScene(content = motionScene),
            progress = fraction.value,
            content = { MusicBottomBarForMotion(viewModel = viewModel) },
        )
    }
}
@Composable
private fun MusicBottomBarForMotion(
    viewModel: MusicPlayerViewModel,
) {
    val uiState by viewModel.musicState.collectAsState()
    val timePassed by viewModel.timePassed.collectAsState()
    val song = uiState.currentPlayingMusic

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .layoutId("mb_row_container")
    ){}

    AlbumImage(
        isPlaying = uiState.isPlaying,
        song = song,
        modifier = Modifier
            .aspectRatio(1.0f)
            .layoutId("mb_album_image")
    )

    Column(modifier = Modifier.layoutId("mb_column_title_artist")) {
        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artistName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            overflow = TextOverflow.Ellipsis
        )
    }
    PlayPauseButton(
        song = song,
        isPlaying = uiState.isPlaying,
        onPlayPauseButtonPressed = {}, //onPlayPauseButtonPressed,
        modifier = Modifier.layoutId("mb_play_pause_button"),
    )
    PlayListButton(
        onPlayListButtonPressed = {}, //onPlayListButtonPressed,
        modifier = Modifier.layoutId("mb_play_list_button")
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.layoutId("column_music_control")
    ) {
        MusicProgress(
            modifier = Modifier.fillMaxWidth(0.9f),
            maxDuration = uiState.currentPlayingMusic.duration,
            currentDuration = timePassed,
            onChanged = { progress ->
                val duration = progress * uiState.currentPlayingMusic.duration
                viewModel.snapTo(duration.toLong())
            }
        )

        MusicControlButtons(
            isPlaying = uiState.isPlaying,
            onNext = viewModel::onNext,
            onPrevious = viewModel::onPrevious,
            onPlayPauseButtonPressed = {
                viewModel.onPlayPauseButtonPressed(song)
            }
        )

        OtherButtons(
            repeatMode = uiState.repeatMode,
            shuffleMode = uiState.shuffleMode,
            onShuffleModePressed = viewModel::onShuffleButtonPressed,
            onRepeatModePressed = viewModel::onRepeatButtonPressed,
        )
    }
}

@Preview
@Composable
private fun MotionLayoutTestPreview() {
    val context = LocalContext.current
    val musicPlayerUseCase = MusicPlayerUseCase(EmptyMusicUseCase())
    val musicPlayerViewModel = MusicPlayerViewModel(
        context = context,
        dispatcher= Dispatchers.IO,
        musicController = MusicController(context, musicPlayerUseCase, true)
    )
    PreviewTheme(false) {
        MotionLayoutTest(viewModel = musicPlayerViewModel)
    }
}

@Preview
@Composable
private fun MusicBottomBarPreview() {
    PreviewTheme(false) {
        BottomMusicPlayer(
            song = Entity.Song.emptySong,
            isPlaying = true,
            onItemClick = { },
            onPlayPauseButtonPressed = { },
            onPlayListButtonPressed = { },
        )
    }
}