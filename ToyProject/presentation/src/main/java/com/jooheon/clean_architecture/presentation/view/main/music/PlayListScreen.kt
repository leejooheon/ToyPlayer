package com.jooheon.clean_architecture.presentation.view.main.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import kotlinx.coroutines.Dispatchers

@Composable
fun PlayListScreen(
    viewModel: MusicPlayerViewModel = hiltViewModel(sharedViewModel()),
    isPreview: Boolean = false
) {
    val uiState by viewModel.musicState.collectAsState()

    Card(
        shape = RoundedCornerShape(0),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.zIndex(2f)
    ) {

        Column(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIXME: uiState.currentSongQueue로 변경하자
            if(uiState.songs.isEmpty()) {
                EmptySongItem()
            }

            LazyColumn {
                itemsIndexed(uiState.songs) { index, song ->
                    MusicItem(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .height(96.dp),
                        song = song,
                        onItemClick = viewModel::onPlayPauseButtonPressed
                    )
                }
            }

            if(isPreview) {
                DummyMusicItem()
            }
        }
    }
}

@Composable
private fun DummyMusicItem() {
    MusicItem(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(96.dp),
        song = Entity.Song.emptySong,
        onItemClick = {}
    )
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
            viewModel = musicPlayerViewModel,
            isPreview = true
        )
    }
}