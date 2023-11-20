package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.library.playlist.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import com.jooheon.clean_architecture.domain.common.extension.default
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.toyproject.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.toyproject.features.common.utils.MusicUtil
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.toyproject.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dialog.MusicPlaylistDialog
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenu
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MusicPlaylistColumnItem(
    playlist: Playlist,
    showContextualMenu: Boolean,
    onItemClick: () -> Unit,
    onDropDownMenuClick: ((index: Int, playlist: Playlist) -> Unit)? = null,
) {
    val playlistNameEditIndex = 1
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    var playlistDialogState by remember { mutableStateOf(false) }

    val thumbnailUrl = playlist.thumbnailUrl.ifBlank {
        playlist.songs.firstOrNull()?.imageUrl
    }.default("empty")

    Card(
        onClick = onItemClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.PlaylistPlay,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null,
                modifier = Modifier.weight(0.1f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            CoilImage(
                url = thumbnailUrl,
                contentDescription = playlist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(25))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(0.7f)
            ) {
                val subTitle = UiText.StringResource(R.string.n_song, playlist.songs.size).asString() + " • " +
                        MusicUtil.toReadableDurationString(playlist.songs.fastSumBy { it.duration.toInt() }.toLong())
                Text(
                    text = playlist.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = subTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.7f
                    ),
                )
            }

            if(showContextualMenu) {
                IconButton(
                    onClick = { dropDownMenuExpanded = true },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "more" // TODO
                    )
                    MusicDropDownMenu(
                        expanded = dropDownMenuExpanded,
                        dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.playlistItems),
                        onDismissRequest = { dropDownMenuExpanded = false },
                        onClick = {
                            if (it == playlistNameEditIndex) {
                                playlistDialogState = true
                            } else {
                                onDropDownMenuClick?.invoke(it, playlist)
                            }
                        }
                    )
                }
            }
        }
    }

    MusicPlaylistDialog(
        openDialog = playlistDialogState,
        title = UiText.StringResource(R.string.dialog_edit_playlist_name).asString(),
        name = playlist.name,
        onConfirmButtonClicked = {
            onDropDownMenuClick?.invoke(playlistNameEditIndex, playlist.copy(name = it))
        },
        onDismiss = { playlistDialogState = false }
    )
}

@Preview
@Composable
private fun MusicPlaylistColumnItemPreview() {
    PreviewTheme(true) {
        MusicPlaylistColumnItem(
            playlist = Playlist.default,
            showContextualMenu = true,
            onItemClick = {},
            onDropDownMenuClick = { _, _ -> }
        )
    }
}