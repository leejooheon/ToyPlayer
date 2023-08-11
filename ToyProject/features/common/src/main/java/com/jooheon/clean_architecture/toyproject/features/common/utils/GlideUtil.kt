package com.jooheon.clean_architecture.toyproject.features.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.HttpException
import com.jooheon.clean_architecture.toyproject.features.common.R
import java.util.concurrent.ExecutionException

object GlideUtil {
    private val TAG = GlideUtil::class.java.simpleName
    val TMP_ESSENTIAL_TITLE_URL = "https://image.bugsm.co.kr/thumb/500x500/mimg/essentialmode/images/original"

    suspend fun asBitmap(context: Context, uri: Uri): Bitmap? {
        try {
            val futureTarget = Glide.with(context)
                .asBitmap()
                .customLoad(uri)
                .submit()

            val bitmap = futureTarget.get()
            Glide.with(context).clear(futureTarget)

            return bitmap
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return context.getDrawable(R.drawable.ic_placeholder)?.toBitmap()
    }

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