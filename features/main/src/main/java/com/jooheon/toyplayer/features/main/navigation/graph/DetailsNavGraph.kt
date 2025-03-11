package com.jooheon.toyplayer.features.main.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.MusicAlbumDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.detail.MusicArtistDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.MusicListDetailScreen
import com.jooheon.toyplayer.features.playlist.details.PlaylistDetailScreen

fun NavGraphBuilder.detailsNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
) {
    composable<ScreenNavigation.Music.PlaylistDetail> {
        val args = it.toRoute<ScreenNavigation.Music.PlaylistDetail>()
        PlaylistDetailScreen(
            navigateTo = navigateTo,
            playlistId = args.playlistId
        )
    }
    composable<ScreenNavigation.Music.ArtistDetail> {
        val args = it.toRoute<ScreenNavigation.Music.ArtistDetail>()
        MusicArtistDetailScreen(
            onBackClick = {},
            navigate = navigateTo,
            artistId = args.artistId
        )
    }
    composable<ScreenNavigation.Music.AlbumDetail> {
        val args = it.toRoute<ScreenNavigation.Music.AlbumDetail>()
        MusicAlbumDetailScreen(
            onBackClick = {},
            navigate = navigateTo,
            albumId = args.albumId
        )
    }
    composable<ScreenNavigation.Music.MusicListDetail> {
        val args = it.toRoute<ScreenNavigation.Music.MusicListDetail>()
        MusicListDetailScreen(
            onBackClick = { },
            navigate = navigateTo,
        )
    }
}