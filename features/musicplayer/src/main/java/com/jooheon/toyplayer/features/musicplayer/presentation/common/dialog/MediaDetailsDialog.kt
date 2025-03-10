package com.jooheon.toyplayer.features.musicplayer.presentation.common.dialog

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.jooheon.toyplayer.features.common.compose.components.ShowAlertDialog
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Song
import org.jaudiotagger.audio.AudioFileIO
import java.io.File


@Composable
fun MediaDetailsDialog(
    song: Song,
    onDismiss: () -> Unit
) {
    if(song == Song.default) {
        return
    }

    val content = parseSongInfo(song) ?: return
    ShowAlertDialog(
        content = content,
        onOkButtonClicked = { onDismiss() },
        onDismiss = { onDismiss() }
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
