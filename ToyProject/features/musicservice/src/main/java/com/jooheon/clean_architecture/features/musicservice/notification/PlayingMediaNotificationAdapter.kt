package com.jooheon.clean_architecture.features.musicservice.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@UnstableApi
class PlayingMediaNotificationAdapter @Inject constructor(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val pendingIntent: PendingIntent?,
) : PlayerNotificationManager.MediaDescriptionAdapter {

    private val lock = Any()
    private var bitmapJob: Job? = null
    private val bitmapProvider = BitmapProvider(
        context = context,
        bitmapSize = 256 * (context.resources.displayMetrics.density).roundToInt()
    )

    override fun createCurrentContentIntent(player: Player) = pendingIntent
    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.albumTitle ?: Song.default.album
    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.displayTitle ?: Song.default.title
    override fun getCurrentSubText(player: Player): CharSequence =
        player.mediaMetadata.subtitle ?: Song.default.artist

    @OptIn(InternalCoroutinesApi::class)
    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        val albumArtUri = player.mediaMetadata.artworkUri ?: return null

        synchronized(lock) {
            if(bitmapProvider.requireLoadBitmap(albumArtUri)) {
                bitmapJob?.cancel()
                bitmapJob = applicationScope.launch(Dispatchers.IO) {
                    val bitmap = bitmapProvider.load(
                        context = context,
                        uri = albumArtUri,
                    )
                    callback.onBitmap(bitmap)
                }
            }
        }
        return bitmapProvider.bitmap
    }
}