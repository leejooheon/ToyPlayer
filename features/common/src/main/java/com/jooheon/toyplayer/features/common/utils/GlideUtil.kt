package com.jooheon.toyplayer.features.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Size
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jooheon.toyplayer.features.common.R

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

    @SuppressLint("CheckResult")
    fun loadBitmap(
        context: Context,
        uri: Uri,
        size: Size,
        onDone: (Bitmap?) -> Unit,
    ) {
        Glide.with(context)
            .asBitmap()
            .customLoad(uri)
            .into(object : CustomTarget<Bitmap>(size.width, size.height) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onDone.invoke(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    onDone.invoke(null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    onDone.invoke(null)
                }
            })
    }

    private fun RequestManager.customLoad(uri: Uri): RequestBuilder<Drawable> {
        val url = uri.toString()
        return if(URLUtil.isContentUrl(url)) {
            load(uri)
        } else {
            load(com.jooheon.toyplayer.features.common.GlideUrlWithCacheKey(url))
        }
    }
    private fun RequestBuilder<Bitmap>.customLoad(uri: Uri): RequestBuilder<Bitmap> {
        val url = uri.toString()
        return if(URLUtil.isContentUrl(url)) {
            load(uri)
        } else {
            load(com.jooheon.toyplayer.features.common.GlideUrlWithCacheKey(url))
        }
    }
}