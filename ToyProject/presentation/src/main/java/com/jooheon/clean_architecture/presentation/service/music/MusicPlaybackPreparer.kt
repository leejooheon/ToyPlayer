package com.jooheon.clean_architecture.presentation.service.music

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase

class MusicPlaybackPreparer(
    private val dataSource: MusicPlayerUseCase,
    private val onPlayerPrepared: (MediaMetadataCompat) -> Unit
) : MediaSessionConnector.PlaybackPreparer {
    private val TAG = MusicService::class.java.simpleName
    override fun onCommand(
        player: Player,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ) = false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) = Unit

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        dataSource.whenReady {
            val songToBePlayed =
                dataSource.allMusicAsMetadata.find { it.description.mediaId == mediaId }
            Log.d(TAG,"Prepare from media id $songToBePlayed")
            songToBePlayed?.let { onPlayerPrepared(it) }
        }
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
}
