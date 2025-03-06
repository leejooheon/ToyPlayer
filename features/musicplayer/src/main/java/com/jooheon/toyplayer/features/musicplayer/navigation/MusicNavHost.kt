package com.jooheon.toyplayer.features.musicplayer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.model.music.MusicListType
import com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.MusicAlbumDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.detail.MusicArtistDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.library.playingqueue.MusicPlayingQueueScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail.MusicPlaylistDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.MusicListDetailScreen

fun NavGraphBuilder.musicNavGraph(
    onBackClick: () -> Unit,
    navigate: (ScreenNavigation.Music) -> Unit,
) {
    composable<ScreenNavigation.Music.PlayingQueue> {
        MusicPlayingQueueScreen(
            onBackClick = onBackClick,
            navigate = navigate,
        )
    }
    composable<ScreenNavigation.Music.ArtistDetail> {
        val args = it.toRoute<ScreenNavigation.Music.ArtistDetail>()
        MusicArtistDetailScreen(
            onBackClick = onBackClick,
            navigate = navigate,
            artistId = args.artistId
        )

    }
    composable<ScreenNavigation.Music.AlbumDetail> {
        val args = it.toRoute<ScreenNavigation.Music.AlbumDetail>()
        MusicAlbumDetailScreen(
            onBackClick = onBackClick,
            navigate = navigate,
            albumId = args.albumId
        )

    }
    composable<ScreenNavigation.Music.PlaylistDetail> {
        val args = it.toRoute<ScreenNavigation.Music.PlaylistDetail>()
        MusicPlaylistDetailScreen(
            onBackClick = onBackClick,
            navigate = navigate,
            playlistId = args.playlistId
        )

    }
    composable<ScreenNavigation.Music.MusicListDetail> {
        val args = it.toRoute<ScreenNavigation.Music.MusicListDetail>()
        MusicListDetailScreen(
            onBackClick = onBackClick,
            navigate = navigate,
        )
    }
}