package com.jooheon.clean_architecture.toyproject.features.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Size
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.jooheon.clean_architecture.toyproject.features.common.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException

object GlideUtil {
    fun ImageView.loadWithGlide(
        uri: Uri,
        placeholderResId: Int = R.drawable.ic_placeholder
    ) {
        val context = this.context ?: return

        Glide.with(context)
            .customLoad(uri)
            .placeholder(placeholderResId)
            .error(placeholderResId)
            .into(this)
    }

    suspend fun loadBitmapSync(
        context: Context,
        uri: Uri,
        size: Size,
    ): Bitmap? {
        val bitmap = withContext(Dispatchers.IO) {
            try {
                val futureTarget = Glide.with(context)
                    .asBitmap()
                    .customLoad(uri)
                    .submit(size.width, size.height)
                val bitmap = futureTarget.get()

                Glide.with(context).clear(futureTarget)

                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        return bitmap
    }


    private fun RequestManager.customLoad(uri: Uri): RequestBuilder<Drawable> {
        val url = uri.toString()
        return if(URLUtil.isContentUrl(url)) {
            load(uri)
        } else {
            load(com.jooheon.clean_architecture.toyproject.features.common.GlideUrlWithCacheKey(url))
        }
    }
    private fun RequestBuilder<Bitmap>.customLoad(uri: Uri): RequestBuilder<Bitmap> {
        val url = uri.toString()
        return if(URLUtil.isContentUrl(url)) {
            load(uri)
        } else {
            load(com.jooheon.clean_architecture.toyproject.features.common.GlideUrlWithCacheKey(url))
        }
    }
}