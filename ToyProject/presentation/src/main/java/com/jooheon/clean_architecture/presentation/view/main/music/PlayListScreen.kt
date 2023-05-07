package com.jooheon.clean_architecture.presentation.view.main.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptySubwayUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun PlayListScreen(
    viewModel: MainViewModel = hiltViewModel(sharedViewModel()),
    isPreview: Boolean = false
) {
//    val musicPlayerViewModel = viewModel.musicControllerUseCase
//    val uiState by musicPlayerViewModel.musicState.collectAsState()

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
//            if(uiState.songs.isEmpty()) {
//                EmptySongItem()
//            }
//
//            LazyColumn {
//                itemsIndexed(uiState.songs) { index, song ->
//                    MusicItem(
//                        modifier = Modifier
//                            .padding(horizontal = 8.dp, vertical = 4.dp)
//                            .height(96.dp),
//                        song = song,
//                        onItemClick = musicPlayerViewModel::onPlayPauseButtonPressed
//                    )
//                }
//            }

            DummyMusicItem()
        }
    }
}

@Composable
private fun DummyMusicItem() {
    MusicItem(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(96.dp),
        song = Song.default,
        onItemClick = {}
    )
}

@Preview
@Composable
private fun PreviewPlayListScreen() {
    val context = LocalContext.current
    val scope = CoroutineScope(Dispatchers.Main)

    val viewModel = MainViewModel(EmptySubwayUseCase())
    PreviewTheme(false) {
        PlayListScreen(
            viewModel = viewModel,
            isPreview = true
        )
    }
}