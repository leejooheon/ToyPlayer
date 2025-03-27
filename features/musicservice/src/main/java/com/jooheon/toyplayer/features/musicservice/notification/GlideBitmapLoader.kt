package com.jooheon.toyplayer.features.musicservice.notification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Size
import androidx.annotation.OptIn
import androidx.core.graphics.applyCanvas
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import androidx.core.graphics.createBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jooheon.toyplayer.features.common.bitmap.loadGlideBitmap

@OptIn(UnstableApi::class)
class GlideBitmapLoader(
    private val context: Context,
    private val bitmapSize: Int,
    private val scope: CoroutineScope,
): BitmapLoader {
    private lateinit var defaultBitmap: Bitmap

    init {
        initDefaultBitmap()
    }

    override fun supportsMimeType(mimeType: String) = false

    private fun initDefaultBitmap() {
        if(::defaultBitmap.isInitialized) return
        defaultBitmap = createBitmap(bitmapSize, bitmapSize).applyCanvas {
            drawColor(Color.BLACK) // FIXME
        }
    }
    override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> {
        return scope.future(Dispatchers.IO) {
            BitmapFactory.decodeByteArray(data, 0, data.size) ?: error("Could not decode image data")
        }
    }

    override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> {
        return scope.future(Dispatchers.IO) {
            val bitmap = try {
                suspendCancellableCoroutine { continuation ->
                    loadGlideBitmap(
                        context = context,
                        uri = uri,
                        size = Size(bitmapSize, bitmapSize),
                        onDone = { continuation.resume(it) }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } ?: defaultBitmap

            return@future bitmap
        }
    }
}