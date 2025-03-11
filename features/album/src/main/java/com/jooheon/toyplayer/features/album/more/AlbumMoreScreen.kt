package com.jooheon.toyplayer.features.album.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
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
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.features.album.more.component.AlbumMoreHeader
import com.jooheon.toyplayer.features.album.more.component.AlbumMoreItem
import com.jooheon.toyplayer.features.album.more.model.AlbumMoreEvent
import com.jooheon.toyplayer.features.album.more.model.AlbumMoreUiState
import com.jooheon.toyplayer.features.common.compose.components.TopAppBarBox

@Composable
fun AlbumMoreScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: AlbumMoreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData(context)
    }

    AlbumMoreScreenInternal(
        uiState = uiState,
        onEvent = {},
        onBackClick = { navigateTo.invoke(ScreenNavigation.Back) }
    )
}

@Composable
private fun AlbumMoreScreenInternal(
    uiState: AlbumMoreUiState,
    onEvent: (AlbumMoreEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val listState = rememberLazyGridState()

    TopAppBarBox(
        title = UiText.StringResource(Strings.title_album).asString(),
        onClick = onBackClick,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
        ) {
            AlbumMoreHeader(
                onDropDownMenuClick = {
//                    val sortType = MusicAlbumScreenViewModel.AlbumSortType.entries[it]
//                    onMusicAlbumEvent(MusicAlbumScreenEvent.OnSortTypeChanged(sortType))
                },
                modifier = Modifier
            )

            LazyVerticalGrid(
                state = listState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 16.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                content = {
                    items(
                        items = uiState.albums,
                        key = { album: Album -> album.hashCode() }
                    ) { album ->
                        AlbumMoreItem(
                            album = album,
                            onClick = {

                            }
                        )
                    }
                }
            )
        }
    }
}


@Preview
@Composable
private fun MusicAlbumScreenPreview() {
    ToyPlayerTheme {
        AlbumMoreScreenInternal(
            uiState = AlbumMoreUiState.default,
            onEvent = {},
            onBackClick = {},
        )
    }
}