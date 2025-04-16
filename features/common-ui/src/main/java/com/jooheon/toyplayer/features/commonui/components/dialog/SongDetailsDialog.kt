package com.jooheon.toyplayer.features.commonui.components.dialog

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import org.jaudiotagger.audio.AudioFileIO
import java.io.File

@Composable
fun SongDetailsDialog(
    song: Song,
    onDismissRequest: () -> Unit
) {
    if(song == Song.default) return
    val content = parseSongInfo(song) ?: return

    DialogColumn(
        fraction = 0.7f,
        padding = 16.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        DialogButton(
            text = stringResource(Strings.ok),
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            onClick = onDismissRequest
        )
    }
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

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, Strings.label_file_name)) }
        append(stringResourceWithLineBreak(songFile.name))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, Strings.label_file_path)) }
        append(stringResourceWithLineBreak(songFile.absolutePath))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, Strings.label_last_modified)) }
        append(stringResourceWithLineBreak(MusicUtil.getDateModifiedString(songFile.lastModified())))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, Strings.label_file_size)) }
        append(stringResourceWithLineBreak(songFile.length().toString()))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, Strings.label_file_format)) }
        append(stringResourceWithLineBreak(audioHeader.format))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, Strings.label_track_length)) }
        append(stringResourceWithLineBreak(MusicUtil.toReadableDurationString((audioHeader.trackLength * 1000).toLong())))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, Strings.label_bit_rate)) }
        append(stringResourceWithLineBreak(audioHeader.bitRate + " kb/s"))

        withStyle(style = titleTextStyle) { append(stringResourceWithColon(context, Strings.label_sampling_rate)) }
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
