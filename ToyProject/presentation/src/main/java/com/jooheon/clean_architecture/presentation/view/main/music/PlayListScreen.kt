package com.jooheon.clean_architecture.presentation.view.main.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.Dispatchers


@Destination
@Composable
fun PlayListScreen(
    navigator: DestinationsNavigator,
    viewModel: MusicPlayerViewModel = hiltViewModel(sharedViewModel()),
) {
    Box(
        modifier = Modifier.padding(top = 50.dp).fillMaxSize()
    ) {
        val uiState by viewModel.musicState.collectAsState()

        LazyColumn {
            itemsIndexed(uiState.currentSongQueue) { index, song ->
                MusicItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .height(76.dp),
                    song = song,
                    surfaceColor = MaterialTheme.colorScheme.surface,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    onItemClick = {
//                        viewModel.playFromMediaId(it.id.toString())
                    }
                )
            }
        }

        if(uiState.currentSongQueue.isEmpty()) {
            EmptySongItem()
        }
    }
}

@Preview
@Composable
private fun PreviewPlayListScreen() {
    val context = LocalContext.current
    val musicPlayerUseCase = MusicPlayerUseCase(EmptyMusicUseCase())
    val musicPlayerViewModel = MusicPlayerViewModel(
        context = context,
        dispatcher= Dispatchers.IO,
        musicController = MusicController(context, musicPlayerUseCase, true)
    )
    PreviewTheme(false) {
        PlayListScreen(
            navigator = EmptyDestinationsNavigator,
            viewModel = musicPlayerViewModel,
        )
    }
}