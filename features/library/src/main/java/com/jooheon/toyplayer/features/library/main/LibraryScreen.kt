package com.jooheon.toyplayer.features.library.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.jooheon.toyplayer.core.resources.Drawables
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.common.compose.components.TopAppBarBox
import com.jooheon.toyplayer.features.library.main.component.LibraryHeaderItem
import com.jooheon.toyplayer.features.library.main.component.artist.ArtistLibrarySection
import com.jooheon.toyplayer.features.library.main.component.playlist.PlaylistLibrarySection
import com.jooheon.toyplayer.features.library.main.model.LibraryEvent
import com.jooheon.toyplayer.features.library.main.model.LibraryUiState

@Composable
fun LibraryScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData(context)
    }

    LibraryScreenInternal(
        uiState = uiState,
        onBackClick = {
            navigateTo.invoke(ScreenNavigation.Back)
        },
        onEvent = {
            val event = when(it) {
                is LibraryEvent.OnPlaylistClick -> ScreenNavigation.Playlist.Details(it.id)
                is LibraryEvent.OnPlaylistMainClick -> ScreenNavigation.Playlist.Main
                is LibraryEvent.OnArtistClick -> ScreenNavigation.Artist.Details(it.id)
                is LibraryEvent.OnArtistMoreClick -> ScreenNavigation.Artist.More
            }
            navigateTo.invoke(event)
        }
    )
}

@Composable
private fun LibraryScreenInternal(
    uiState: LibraryUiState,
    onBackClick: () -> Unit,
    onEvent: (LibraryEvent) -> Unit,
) {
    TopAppBarBox(
        title = UiText.StringResource(Strings.title_library).asString(),
        onClick = onBackClick,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            LibraryHeaderItem(
                title = UiText.StringResource(Strings.title_playlist),
                resId = Drawables.default_album_art
            )

            PlaylistLibrarySection(
                models = uiState.defaultPlaylists,
                onClick = { onEvent.invoke(LibraryEvent.OnPlaylistClick(it)) },
                onMoreClick = { onEvent.invoke(LibraryEvent.OnPlaylistMainClick) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LibraryHeaderItem(
                title = UiText.StringResource(Strings.title_artist),
                resId = Drawables.default_album_art
            )

            ArtistLibrarySection(
                models = uiState.artists,
                onClick = { onEvent.invoke(LibraryEvent.OnArtistClick(it)) },
                onMoreClick = { onEvent.invoke(LibraryEvent.OnArtistMoreClick) },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLibraryScreen() {
    ToyPlayerTheme {
        LibraryScreenInternal(
            uiState = LibraryUiState.preview,
            onBackClick = {},
            onEvent = {},
        )
    }
}
