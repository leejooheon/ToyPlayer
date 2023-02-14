package com.jooheon.clean_architecture.presentation.view.main.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.layoutId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.base.extensions.albumArtUri
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlaylistUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicControllerUseCase
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.view.components.CoilImage
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel
import com.jooheon.clean_architecture.presentation.view.main.music.MusicControlButtons
import com.jooheon.clean_architecture.presentation.view.main.music.MusicProgress
import com.jooheon.clean_architecture.presentation.view.main.music.OtherButtons
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import com.jooheon.clean_architecture.presentation.view.temp.EmptySettingUseCase
import com.jooheon.clean_architecture.presentation.view.temp.EmptySubwayUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun MusicPlayerScreen(
    navigator: NavController,
    viewModel: MainViewModel = hiltViewModel(sharedViewModel()),
) {
    Card(
        shape = RoundedCornerShape(0),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.zIndex(2f)
    ) {
        AodPlayer(viewModel = viewModel.musicControllerUseCase)
    }
}

@Composable
private fun AodPlayer(viewModel: MusicControllerUseCase) {
    val uiState by viewModel.musicState.collectAsState()
    val timePassed by viewModel.timePassed.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CoilImage(
            url = uiState.currentPlayingMusic.albumArtUri.toString(),
            contentDescription = uiState.currentPlayingMusic.title,
            shape = RoundedCornerShape(10.dp),
            placeholderRes = R.drawable.ic_logo_github,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1.0f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .layoutId("column_title_artist")
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = uiState.currentPlayingMusic.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = uiState.currentPlayingMusic.artistName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.layoutId("column_music_control")
        ) {
            MusicProgress(
                modifier = Modifier.fillMaxWidth(0.8f),
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
                    val song = uiState.currentPlayingMusic
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
}


@Preview
@Composable
private fun PreviewAodPlayerPreview() {
    val context = LocalContext.current
    val scope = CoroutineScope(Dispatchers.Main)

    val musicPlaylistUseCase = MusicPlaylistUseCase(EmptyMusicUseCase())
    val musicControllerUseCase = MusicControllerUseCase(
        context = context,
        applicationScope = scope,
        musicController = MusicController(
            context = context, 
            applicationScope = scope,
            musicPlaylistUseCase = musicPlaylistUseCase,
            settingUseCase = EmptySettingUseCase(), 
            isPreview = true
        )
    )
    val viewModel = MainViewModel(EmptySubwayUseCase(), musicControllerUseCase)
    PreviewTheme(false) {
        MusicPlayerScreen(
            navigator = NavController(context),
            viewModel = viewModel
        )
    }
}