package com.jooheon.clean_architecture.features.musicservice

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Size
import androidx.core.graphics.applyCanvas
import com.jooheon.clean_architecture.toyproject.features.common.utils.GlideUtil
import com.jooheon.clean_architecture.toyproject.features.musicservice.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BitmapProvider(
    context: Context,
    private val bitmapSize: Int,
    private val listener: (Bitmap?) -> Unit,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + BitmapProvider::class.java.simpleName

    private var lastUri: Uri? = null
    private var lastBitmap: Bitmap? = null
    private lateinit var defaultBitmap: Bitmap
    val bitmap: Bitmap
        get() = lastBitmap ?: defaultBitmap

    init {
        initDefaultBitmap(context)
    }

    private fun initDefaultBitmap(context: Context) {
        if(::defaultBitmap.isInitialized) return
        defaultBitmap = Bitmap.createBitmap(
            bitmapSize,
            bitmapSize,
            Bitmap.Config.ARGB_8888
        ).applyCanvas {
            drawColor(Color.BLACK) // FIXME
        }
    }

    fun requireLoadBitmap(uri: Uri) = lastUri != uri

    suspend fun load(
        context: Context,
        uri: Uri,
    ): Bitmap {
        if(lastUri == uri) return bitmap
        lastUri = uri

        val result = suspendCancellableCoroutine<Bitmap?> { continuation ->
            val bigNotificationImageSize = context.resources.getDimensionPixelSize(R.dimen.notification_big_image_size)
            GlideUtil.loadBitmap(
                context = context,
                uri = uri,
                size = Size(bigNotificationImageSize, bigNotificationImageSize),
                onDone = {
                    val bitmap = it ?: defaultBitmap
                    continuation.resume(bitmap)
                }
            )
        }

        val newBitmap = result ?: defaultBitmap
        lastBitmap = newBitmap
        listener.invoke(bitmap)

        return bitmap
    }
}