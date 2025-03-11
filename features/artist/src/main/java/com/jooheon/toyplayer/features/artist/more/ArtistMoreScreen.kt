package com.jooheon.toyplayer.features.artist.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.features.artist.more.component.ArtistHeader
import com.jooheon.toyplayer.features.artist.more.component.ArtistMoreItem
import com.jooheon.toyplayer.features.artist.more.model.ArtistMoreEvent
import com.jooheon.toyplayer.features.artist.more.model.ArtistMoreUiState
import com.jooheon.toyplayer.features.common.compose.components.TopAppBarBox

@Composable
fun ArtistMoreScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: ArtistMoreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData(context)
    }

    ArtistMoreScreenInternal(
        uiState = uiState,
        onEvent = {
            when(it) {
                is ArtistMoreEvent.OnArtistClick -> {
                    navigateTo.invoke(ScreenNavigation.Artist.Details(it.id))
                }
            }
        },
        onBackClick = {
            navigateTo.invoke(ScreenNavigation.Back)
        }
    )
}

@Composable
private fun ArtistMoreScreenInternal(
    uiState: ArtistMoreUiState,
    onEvent: (ArtistMoreEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val state = rememberLazyGridState()

    TopAppBarBox(
        title = UiText.StringResource(Strings.title_artist).asString(),
        onClick = onBackClick,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ArtistHeader(
                onDropDownMenuClick = {
//                    val type = MusicArtistScreenViewModel.ArtistSortType.entries[it]
//                    onMusicArtistScreenEvent(MusicArtistScreenEvent.OnSortTypeChanged(type))
                },
                modifier = Modifier,
            )
            LazyVerticalGrid(
                state = state,
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 16.dp
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    items = uiState.artists,
                    key = { artist: Artist -> artist.hashCode() }
                ) { artist ->
                    ArtistMoreItem(
                        artist = artist,
                        onItemClick = {
                            val event = ArtistMoreEvent.OnArtistClick(artist.id)
                            onEvent.invoke(event)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewArtistMoreScreen() {
    ToyPlayerTheme {
        ArtistMoreScreenInternal(
            uiState = ArtistMoreUiState.preview,
            onEvent = {},
            onBackClick = {},
        )
    }
}
