package com.jooheon.clean_architecture.features.musicservice

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Size
import androidx.core.graphics.applyCanvas
import com.jooheon.clean_architecture.toyproject.features.common.utils.GlideUtil

class BitmapProvider(
    context: Context,
    private val bitmapSize: Int,
) {
    init {
        initDefaultBitmap(context)
    }

    private lateinit var defaultBitmap: Bitmap

    private var lastBitmap: Bitmap? = null

    var lastUri: Uri? = null
        private set

    private fun initDefaultBitmap(context: Context) {
        val isSystemInDarkMode = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if(::defaultBitmap.isInitialized) return

        defaultBitmap = Bitmap.createBitmap(
            bitmapSize,
            bitmapSize,
            Bitmap.Config.ARGB_8888
        ).applyCanvas {
//            val color = if(isSystemInDarkMode) {
//                md_theme_light_primary
//            } else {
//                md_theme_dark_primary
//            }
            drawColor(Color.BLACK) // FIXME
        }
    }

    suspend fun load(context: Context, uri: Uri, onDone: (Bitmap) -> Unit) {
        val bitmap = GlideUtil.loadBitmapSync(
            context = context,
            uri = uri,
            size = Size(bitmapSize, bitmapSize)
        ) ?: defaultBitmap

        if(bitmap != defaultBitmap) {
            lastUri = uri
            lastBitmap = bitmap
        }

        onDone.invoke(bitmap)
    }

    fun bitmap(uri: Uri): Bitmap {
        val bitmap = lastBitmap ?: defaultBitmap

        return if(uri == lastUri) {
            bitmap
        } else {
            defaultBitmap
        }
    }

    fun requireLoadImage(
        uri: Uri
    ): Boolean {
        if(uri == lastUri) {
            if(lastBitmap != defaultBitmap)
                return false
        }

        return true
    }
}