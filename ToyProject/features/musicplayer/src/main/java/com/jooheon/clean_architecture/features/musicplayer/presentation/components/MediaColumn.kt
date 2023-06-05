package com.jooheon.clean_architecture.features.musicplayer.presentation.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.components.ShowAlertDialog
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.common.utils.MusicUtil
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events.MediaDropDownMenuEvent
import org.jaudiotagger.audio.AudioFileIO
import java.io.File

@Composable
fun MediaColumn(
    onItemClick: (song: Song) -> Unit,
    onDropDownMenuClick: (Int, Song) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    playlist: List<Song>,
    viewType: Boolean,
    modifier: Modifier = Modifier,
) {
    var detailsDialogState by remember { mutableStateOf(Song.default) }

    val onDropDownMenuClickEvent: (Int, Song) -> Unit = { index, song ->
        if(MediaDropDownMenuEvent.fromIndex(index, song) is MediaDropDownMenuEvent.OnDetailsClick) {
            detailsDialogState = song
        } else {
            onDropDownMenuClick(index, song)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(playlist) { song ->
            if(viewType) {
                MediaItemLarge(
                    title = song.title,
                    subTitle = song.artist,
                    imageUrl = song.imageUrl,
                    onItemClick = { onItemClick(song) },
                    onDropDownMenuClick = { onDropDownMenuClickEvent(it, song) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                MediaItemSmall(
                    imageUrl = song.imageUrl,
                    title = song.title,
                    subTitle = song.artist,
                    showContextualMenu = true,
                    onItemClick = { onItemClick(song) },
                    onDropDownMenuClick = { onDropDownMenuClickEvent(it, song) },
                    modifier = Modifier,
                )
            }
        }
    }

    DetailsDialog(
        dialogState = detailsDialogState,
        onInteraction = {
            detailsDialogState = Song.default
        }
    )
}


@Composable
private fun DetailsDialog(
    dialogState: Song,
    onInteraction: (Unit?) -> Unit
) {
    val song = dialogState
    if(dialogState == Song.default) {
        return
    }

    val content = parseSongInfo(song) ?: return
    ShowAlertDialog(
        content = content,
        onOkButtonClicked = { onInteraction(Unit) },
        onDismiss = { onInteraction(null) }
    )
}

@Composable
private fun parseSongInfo(song: Song): AnnotatedString? {
    val data = song.data ?: return null
    val songFile = File(data)

    if(!songFile.exists()) return null
    val context = LocalContext.current

    val audioFile = AudioFileIO.read(songFile)
    val audioHeader = audioFile.audioHeader

    val annotatedString = buildAnnotatedString {
        val titleTextStyle = SpanStyle(fontWeight = FontWeight.Bold)

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, R.string.label_file_name)) }
        append(stringResourceWithLineBreak(songFile.name))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, R.string.label_file_path)) }
        append(stringResourceWithLineBreak(songFile.absolutePath))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, R.string.label_last_modified)) }
        append(stringResourceWithLineBreak(MusicUtil.getDateModifiedString(songFile.lastModified())))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, R.string.label_file_size)) }
        append(stringResourceWithLineBreak(songFile.length().toString()))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, R.string.label_file_format)) }
        append(stringResourceWithLineBreak(audioHeader.format))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, R.string.label_track_length)) }
        append(stringResourceWithLineBreak(MusicUtil.toReadableDurationString((audioHeader.trackLength * 1000).toLong())))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, R.string.label_bit_rate)) }
        append(stringResourceWithLineBreak(audioHeader.bitRate + " kb/s"))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, R.string.label_sampling_rate)) }
        append(stringResourceWithLineBreak(audioHeader.sampleRate + " Hz"))
    }
    return annotatedString
}

private fun stringResourceWithColon(context: Context, resId: Int): String {
    return UiText.StringResource(resId).asString(context) + ": "
}
private fun stringResourceWithLineBreak(content: String): String {
    return content + "\n"
}

@Preview
@Composable
fun VideoColumnPreview() {
    PreviewTheme(false) {
        Surface(modifier = Modifier.width(400.dp)) {
            Column {
                MediaColumn(
                    viewType = true,
                    onItemClick = {},
                    onDropDownMenuClick = { _, _ -> },
                    playlist = listOf(Song.default),
                )
                MediaColumn(
                    viewType = false,
                    onItemClick = {},
                    onDropDownMenuClick = { _, _ -> },
                    playlist = listOf(Song.default),
                )
            }
        }
    }
}